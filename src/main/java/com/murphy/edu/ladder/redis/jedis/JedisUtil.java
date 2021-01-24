package com.murphy.edu.ladder.redis.jedis;

import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * 对外开放的操作工具类
 */
@Slf4j
public class JedisUtil {

    static DecimalFormat df = new DecimalFormat("0.00");

    static String JEDIS_INIT = "jedis::init";

    static String SCRIPT_LOCK = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then redis.call('expire', KEYS[1], ARGV[2]) return 1 else return 0 end";

    static String SCRIPT_UNLOCK = "if redis.call('get',  KEYS[1]) == ARGV[1] then return redis.call('del',  KEYS[1]) else return 0 end";


    private static JedisCommands commands = null;

    static {
        switch (JedisCenter.redisMode) {
            case STANDALONE:
            case SENTINEL:
                commands = new JedisStandaloneAndSentinelImpl();
                break;
            case CLUSTER:
                commands = new JedisClusterImpl();
                break;
            default:
                break;
        }
        log.info("取法乎上-redis当前运行模式{}", JedisCenter.redisMode.getMode());
    }

    /**
     * 设置Byte类型缓存
     *
     * @param key   key
     * @param value value
     * @author Dream
     * @date 2019年7月5日13:28:51
     * return Status code reply
     */
    public static String setByte(final byte[] key, final byte[] value) {
        return commands.setByte(key, value);
    }

    /**
     * 设置Byte类型缓存,使用gzip压缩成byte[]存储
     *
     * @param key   key
     * @param value value
     * @author Dream
     * @date 2019年7月5日13:28:51
     * return Status code reply
     * 注意如果Object value 是byte[] ,则会将字节数据再次进行Gzip压缩,取出的结果就可能不是想要的结果
     * 一般Object value 是一个Java model
     */
    public static String setByteGzip(final byte[] key, final Object value) {
        return commands.setByteGzip(key, value);
    }

    /**
     * 设置Byte类型缓存,设置过期时间
     *
     * @param key     key
     * @param value   value
     * @param seconds seconds
     * @author Dream
     * @date 2019年7月5日13:28:51
     * return Status code reply
     */
    public static String setByte(final byte[] key, final byte[] value, final int seconds) {
        return commands.setByte(key, value, seconds);
    }

    /**
     * 设置Byte类型缓存,使用gzip压缩成byte[]存储,设置过期时间
     *
     * @param key     key
     * @param value   value
     * @param seconds seconds
     * @author Dream
     * @date 2019年7月5日13:28:51
     * return Status code reply
     * 不保证原子性
     */
    public static String setByteGzip(final byte[] key, final Object value, final int seconds) {
        return commands.setByteGzip(key, value, seconds);
    }

    /**
     * 获取缓存 byte[]
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     * return byte[] b
     */
    public static byte[] getByte(final byte[] key) {
        return commands.getByte(key);
    }

    /**
     * 将字节数据(经过gip压缩)转成Object返回,
     *
     * @param key  key
     * @param type type
     * @author Dream
     * @date 2019年7月5日13:28:51
     * return T t
     */
    public static <T> T getGipByte2Object(final byte[] key, TypeReference<T> type) {
        return commands.getGipByte2Object(key, type);
    }

    /**
     * 设置数据过期时间 byte[] key
     *
     * @param key     key
     * @param seconds seconds
     * @author Dream
     * @date 2019年7月5日13:28:51
     * return  t
     */
    public static Long expire(final byte[] key, final int seconds) {
        return commands.expire(key, seconds);
    }

    /**
     * 设置String类型缓存
     *
     * @param key    key
     * @param value  value
     * @param expire 小于或等于0时, 表示长期有效
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setString(String key, String value, Integer expire) {
        return commands.setString(key, value, expire);
    }

    /**
     * 设置String类型缓存, 缓存时间默认为一天
     *
     * @param key   key
     * @param value value
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setString(String key, String value) {
        return commands.setString(key, value);
    }

    /**
     * 获取String类型缓存
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static String getString(String key) {
        return commands.getString(key);
    }

    /**
     * 整数类型的key值操作, 原子性操作
     *
     * @param key key
     * @param num 正数为增, 负数为减
     * @return 返回操作后的key值
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Long operateNum(String key, Long num) {
        return commands.operateNum(key, num);
    }

    /**
     * 获取整数类型key值
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Long getNum(String key) {
        return commands.getNum(key);
    }

    /**
     * 浮点数类型key值操作, 原子性操作
     *
     * @param key key
     * @param amt 正数为增, 负数为减
     * @return 返回操作后的key值,  保留小数点后两位
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static BigDecimal operateFloat(String key, BigDecimal amt) {
        return commands.operateFloat(key, amt);
    }

    /**
     * 获取浮点类型key值, 默认保留小数点后两位
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static BigDecimal getFloat(String key) {
        return commands.getFloat(key);
    }

    /**
     * 存贮数据bean, 数据对象永久有效
     *
     * @param key  数据键
     * @param bean bean实例
     * @return 是否设置成功,  如果成功返回true
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setBean(String key, Object bean) {
        return commands.setBean(key, bean);
    }

    /**
     * 存贮数据bean
     *
     * @param key    数据键
     * @param bean   bean实例
     * @param expire 超时时间, 单位秒, 0表示长期有效
     * @return 是否设置成功,  如果成功返回true
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setBean(String key, Object bean, Integer expire) {
        return commands.setBean(key, bean, expire);
    }

    /**
     * 获取bean, 返回json字符串类型
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static String getBean(String key) {
        return commands.getBean(key);
    }

    /**
     * 取得缓存bean
     *
     * @param key  数据键
     * @param type bean类型
     * @return bean实例
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static <T> T getBean(String key, TypeReference<T> type) {
        return commands.getBean(key, type);
    }

    /**
     * 取得缓存bean并同步更新超时时间
     *
     * @param key    数据键
     * @param type   bean类型
     * @param expire 超时时间, 单位秒, 如果不修改填写0
     * @return bean实例
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static <T> T getBean(String key, TypeReference<T> type, Integer expire) {
        return commands.getBean(key, type, expire);
    }

    /**
     * 删除缓存数据
     *
     * @param key key
     * @return 删除结果,  如果成功返回true
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean remove(String key) {
        return commands.remove(key);
    }

    /**
     * 判断lock是否加锁
     * <p>
     * 分布式原子操作, 如果加锁成功, 返回加锁唯一序列, 用于解锁
     * <p>
     * 如果加锁失败, 返回null
     * <p>
     * 为了更好的使用分布式锁, 我们强烈建议传入合理的过期时间参数, 以防止死锁或者锁等待类似的情况 单位 秒
     *
     * @param lockKey 加锁的key
     * @param expire  锁自动过期时间, 单位秒
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static String operateRedisLock(String lockKey, int expire) {
        return commands.operateRedisLock(lockKey, expire);
    }

    /**
     * 删除redis锁
     * <p>
     * 分布式原子操作, 如果释放锁成功, 返回true, 否则返回false
     *
     * @param lockKey   释放锁的key
     * @param lockValue 必须是加锁成功返回的字符串
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean cancelRedisLock(String lockKey, String lockValue) {
        return commands.cancelRedisLock(lockKey, lockValue);
    }

    /**
     * 设置超时时间
     *
     * @param key    数据键
     * @param expire 超时时间, 单位秒
     */
    public static void expire(String key, Integer expire) {
        commands.expire(key, expire);
    }

    /**
     * 设置hash类型缓存 默认缓存24小时
     *
     * @param key   Hash名称
     * @param field Hash中的key
     * @param value 值s
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setHashCached(String key, String field, String value) {
        return commands.setHashCached(key, field, value);
    }

    /**
     * 设置hash类型的缓存
     *
     * @param key    hash的key
     * @param field  hash中的field
     * @param value  hash中的field的值
     * @param expire 0或小于0表示长期有效
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setHashCached(String key, String field, String value, Integer expire) {
        return commands.setHashCached(key, field, value, expire);
    }

    /**
     * 设置hash类型缓存, 当且仅当field不存在时, 才会执行成功, 返回true
     * 若域 field 已经存在, 该操作无效
     * 如果 key 不存在, 一个新哈希表被创建并执行 HSETNX 命令
     *
     * @param key   key
     * @param field field
     * @param value value
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setHashCachedNX(String key, String field, String value) {
        return commands.setHashCachedNX(key, field, value);
    }

    /**
     * 获取hash类型缓存
     *
     * @param key   Hash名称
     * @param field Hash中的key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static String getHashCached(String key, String field) {
        return commands.getHashCached(key, field);
    }

    /**
     * 获取hash类型数量
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Long getHashLen(String key) {
        return commands.getHashLen(key);
    }

    /**
     * 删除hash 中的key
     *
     * @param key   key
     * @param field field
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean removeHashKey(String key, String field) {
        return commands.removeHashKey(key, field);
    }

    /**
     * 获取hash类型下所有的键值对
     *
     * @param key hash的key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Map<String, String> getHashAll(String key) {
        return commands.getHashAll(key);
    }

    /**
     * hash类型操作数字
     *
     * @param key   key
     * @param field field
     * @param num   num
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Long hOperateNum(String key, String field, Long num) {
        return commands.hOperateNum(key, field, num);
    }

    /**
     * hash获取数字
     *
     * @param key   key
     * @param field field
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Long hGetNum(String key, String field) {
        return commands.hGetNum(key, field);
    }

    /**
     * hash类型操作浮点数
     *
     * @param key   key
     * @param field field
     * @param amt   amt
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static BigDecimal hOperateFloat(String key, String field, BigDecimal amt) {
        return commands.hOperateFloat(key, field, amt);
    }

    /**
     * hash类型获取浮点数
     *
     * @param key   key
     * @param field field
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static BigDecimal hGetFloat(String key, String field) {
        return commands.hGetFloat(key, field);
    }

    /**
     * 获取list中的数据
     *
     * @param key   key
     * @param start 开始下标, 0表示第一个元素
     * @param end   结束下标, -1表示最后一个元素, -2表示倒数第二个元素, 以此类推
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static List<String> getList(String key, int start, int end) {
        return commands.getList(key, start, end);
    }

    /**
     * 从左侧向list类型中插入数据
     * 如果key不存在, 就直接创建list
     *
     * @param key    key
     * @param vaules vaules
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setLeftList(String key, String... vaules) {
        return commands.setLeftList(key, vaules);
    }

    /**
     * 从右侧向list类型中插入数据
     * 如果key不存在, 就直接创建list
     *
     * @param key    key
     * @param vaules vaules
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setRightList(String key, String... vaules) {
        return commands.setRightList(key, vaules);
    }

    /**
     * 返回list中的元素个数
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Integer getListLength(String key) {
        return commands.getListLength(key);
    }

    /**
     * 移除list中值为value的元素
     *
     * @param key   key
     * @param value vaule
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean removeList(String key, String value) {
        return commands.removeList(key, value);
    }

    /**
     * 向Set类型中插入数据
     *
     * @param key    key
     * @param values vaules
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setSet(String key, String... values) {
        return commands.setSet(key, values);
    }

    /**
     * 返回set中的所有数据
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Set<String> getSet(String key) {
        return commands.getSet(key);
    }

    /**
     * 返回set中的元素个数
     *
     * @param key key
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Integer getSetLength(String key) {
        return commands.getSetLength(key);
    }

    /**
     * 向set中插入数据
     *
     * @param key    key
     * @param values values
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean addSet(String key, String... values) {
        return commands.addSet(key, values);
    }

    /**
     * 移除set中值为values的元素
     *
     * @param key    key
     * @param values values
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean removeSet(String key, String... values) {
        return commands.removeSet(key, values);
    }

    /**
     * 返回多个集合中的交集
     *
     * @param keys keys
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Set<String> sinterSet(String... keys) {
        return commands.sinterSet(keys);
    }

    /**
     * 返回多个集合中的并集
     *
     * @param keys keys
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Set<String> sunionSet(String... keys) {
        return commands.sunionSet(keys);
    }

    /**
     * 返回多个集合中第一个元素的差集
     * 例如：set1 11 22 33, set2 22, set3 33, 调用方法则返回11
     *
     * @param keys keys
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static Set<String> sdiffSet(String... keys) {
        return commands.sdiffSet(keys);
    }

    /**
     * 如果 key 已经存在,  并且值为字符串,  那么这个命令会把 value 追加到原来值（value）的结尾。
     * 如果 key 不存在,  那么它将首先创建一个空字符串的key,  再执行追加操作,  这种情况 APPEND 将类似于 SET 操作。
     *
     * @param key         key
     * @param appendValue appendValue
     * @return 返回append后字符串值（value）的长度, 失败或异常返回0
     */
    public static long append(String key, String appendValue) {
        return commands.append(key, appendValue);
    }

    /**
     * 对key对应的数字做减 1操作。如果key不存在,  那么在操作之前,  这个key对应的值会被置为0。
     * 这个操作最大支持在64位有符号的整型数字。
     *
     * @param key key
     * @return 如果key不存在,  则创建并赋值为0然后-1返回-1, 如果key存在并且value是数字类型, 则返回-1后的值
     * 如果value不是整型或者异常则返回-9999
     */
    public static long decr(String key) {
        return commands.decr(key);
    }

    /**
     * 对key对应的数字做减1操作, 如果key存在, 则将值-1(此时会忽略value), 如果key不存在, 则初始化为value,如果initValue不存在,则初始化为0
     *
     * @param key       key
     * @param initValue 第一位有效
     * @return 返回-1后的值,如果value不是数字类型或者操作异常,则返回-9999
     */
    public static long initAndDecr(String key, long... initValue) {
        return commands.initAndDecr(key, initValue);
    }

    /**
     * 对key对应的数字做减 1 操作, 强制初始化成initValue, 注意如果key存在的话, 则会被强制覆盖成给定的initValue
     * 如果initValue不存在,则会被初始化成 0
     *
     * @param key       key
     * @param initValue 第一位有效, 默认为 0
     * @return 返回-1后的值 如果异常 返回-9999
     */
    public static long initForceAndDecr(String key, long... initValue) {
        return commands.initForceAndDecr(key, initValue);
    }

    /**
     * 对key对应的数字做减value操作。如果key不存在,  那么在操作之前,  这个key对应的值会被置为0。
     * 这个操作最大支持在64位有符号的整型数字。
     *
     * @param key     key
     * @param byValue 第一位有效, 默认为 1
     * @return 如果key不存在,  则创建并赋值为0然后-value返回-value, 如果key存在并且value是数字类型, 则返回-value后的值
     * 如果value不是整型则返回-9999
     */
    public static long decrBy(String key, long... byValue) {
        return commands.decrBy(key, byValue);

    }

    /**
     * 对key对应的数字做减byValue操作, 如果key存在, 则将值-byValue(此时会忽略initValue),如果byValue不存在则-1 如果key不存在, 则初始化为initValue,
     *
     * @param key       key
     * @param initValue initValue
     * @param byValue   第一位有效, 默认为0
     * @return 返回 -byValue 后的值
     */
    public static long initAndDecrBy(String key, long initValue, long... byValue) {
        return commands.initAndDecrBy(key, initValue, byValue);
    }

    /**
     * 执行LUA脚本(单机 哨兵)
     *
     * @param script script
     * @return 返回LUA执行结果, 异常返回-9999
     */
    public static Object eval(String script) {
        return commands.eval(script);
    }

    /**
     * 执行LUA脚本
     *
     * @param script   脚本
     * @param keyCount KEYS参数个数,  其余的为ARGV参数
     * @param params   数组参数
     */
    public static Object eval(String script, int keyCount, String... params) {
        return commands.eval(script, keyCount, params);
    }

    /**
     * 设置一个key在某个unixTime时间戳点过期, 如果小于当前时间, 则会立即过期
     *
     * @param key       key
     * @param timestamp timestamp
     * @return 成功返回true, 失败返回false
     */
    public static boolean expireAt(String key, long timestamp) {
        return commands.expireAt(key, timestamp);
    }

    /**
     * 设置一个值, 并设置在unixTime某个时间戳点过期
     * 请注意, value如果非基本数据类型以及非String类型, 请注意序列化
     *
     * @param key       key
     * @param value     value
     * @param timestamp timestamp
     * @return 成功返回true,  失败返回false
     */
    public static boolean setAndExpireAt(String key, Object value, long timestamp) {
        return commands.setAndExpireAt(key, value, timestamp);
    }

    /**
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中
     * 有效的经度从-180度到180度。
     * 有效的纬度从-85.05112878度到85.05112878度。
     *
     * @param key       key
     * @param longitude 经度
     * @param latitude  纬度
     * @param member    成员
     * @return 添加到sorted set元素的数目,  但不包括已更新score的元素
     */
    public static long geoAdd(String key, double longitude, double latitude, String member) {
        return commands.geoAdd(key, longitude, latitude, member);
    }

    /**
     * 返回一个list<GeoCoordinate>  包含了给定key 中指定member成员的经纬度
     *
     * @param key     key
     * @param members members
     */
    public static List<GeoCoordinate> geoPos(String key, String... members) {
        return commands.geoPos(key, members);
    }

    /**
     * 返回两个给定位置之间的距离, 单位 米
     * GEODIST 命令在计算距离时会假设地球为完美的球形,   在极限情况下,   这一假设最大会造成 0.5% 的误差
     *
     * @param key     key
     * @param member1 member1
     * @param member2 member2
     */
    public static Double geoDist(String key, String member1, String member2) {
        return commands.geoDist(key, member1, member2);
    }

    /**
     * 返回两个给定位置之间的距离, 指定单位
     * GEODIST 命令在计算距离时会假设地球为完美的球形,   在极限情况下,   这一假设最大会造成 0.5% 的误差
     * m 表示单位为米。
     * km 表示单位为千米。
     * mi 表示单位为英里。
     * ft 表示单位为英尺。
     *
     * @param key     key
     * @param member1 member1
     * @param member2 member2
     */
    public static Double geoDist(String key, String member1, String member2, GeoUnit unit) {
        return commands.geoDist(key, member1, member2, unit);
    }

    /**
     * 以给定的经纬度为中心,   返回键包含的位置元素当中,   与中心的距离不超过给定最大距离的所有位置元素
     * m 表示单位为米。
     * km 表示单位为千米。
     * mi 表示单位为英里。
     * ft 表示单位为英尺。
     *
     * @param key       key
     * @param longitude 经度
     * @param latitude  纬度
     * @param radius    给定范围半径
     * @param unit      单位
     */
    public static List<GeoRadiusResponse> geoRadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return commands.geoRadius(key, longitude, latitude, radius, unit);
    }

    /**
     * 以member为中心 找出位于指定范围内的元素
     * m 表示单位为米。
     * km 表示单位为千米。
     * mi 表示单位为英里。
     * ft 表示单位为英尺。
     *
     * @param key    key
     * @param member 给定的中心点
     * @param radius 半径
     * @param unit   单位
     */
    public static List<GeoRadiusResponse> geoRadiusByMember(String key, String member, double radius, GeoUnit unit) {
        return commands.geoRadiusByMember(key, member, radius, unit);
    }

    /**
     * 返回hash里面field是否存在
     *
     * @param key   key
     * @param field field
     * @return 1 存在,  0不存在,  -1 异常
     */
    public static int hexists(String key, String field) {
        return commands.hexists(key, field);
    }

    /**
     * 返回key是否存在
     *
     * @param key key
     * @return 1 存在,  0不存在,  -1 异常
     */
    public static int exists(String key) {
        return commands.exists(key);
    }

    /**
     * 删除当前redis中所有的key, 单机/哨兵 删除当前库  集群下删除所有数据,此方法慎重使用,尤其强依赖缓存的系统
     *
     * @return 删除的key数量
     */
    public static long delAllKeys() {
        return commands.delAllKeys();
    }
}
