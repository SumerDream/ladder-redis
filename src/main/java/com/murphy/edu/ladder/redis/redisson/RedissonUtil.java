package com.murphy.edu.ladder.redis.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Dream
 * @ 对外提供的Redisson操作类
 * @date 2019年7月10日12:05:43
 * <p>
 */
@Slf4j
public class RedissonUtil {

    static String REDISSON_INIT = "redisson::init";

    private static RedissonCommands commands;

    static {
        log.info("Redisson当前运行模式{}", RedissonCenter.redisMode.getMode());
        commands = new RedissonOperate();
    }

    /**
     * 设置String类型缓存,缓存时间
     *
     * @param key key
     * @param value value
     * @return 设置成功返回true, 失败或者异常返回false
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setString(final String key, final String value) {
        return commands.setString(key, value);
    }

    /**
     * 设置String类型缓存,缓存时间
     *
     * @param key key
     * @param value value
     * @param expire 有效期,单位秒
     * @return 设置成功返回true, 失败或者异常返回false
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static boolean setString(final String key, final String value, final long expire, final TimeUnit timeUnit) {
        return commands.setString(key, value, expire, timeUnit);
    }

    /**
     * 判断一个key在bucket中是否存在
     *
     * @param key key
     * @return 存在返回true 失败或者异常返回false
     */
    public static boolean isExists(final String key) {
        return commands.isExists(key);
    }

    /**
     * 存储hash类型的值
     *
     * @param key key
     * @param field field
     * @param value value
     * @return 成功返回true 失败返回false
     */
    public static boolean hset(final String key, final String field, Object value) {
        return commands.hset(key, field, value);
    }

    /**
     * 设置一个值,仅当key不存在才会设置成功,若域 key 已经存在, 该操作无效
     *
     * @param key key
     * @param value value
     */

    public static boolean setnx(final String key, final Object value) {
        return commands.setnx(key, value);
    }

    /**
     * 获取hash类型的value
     *
     * @param key key
     * @param field field
     */
    public static <T> T hget(final String key, final String field) {
        return commands.hget(key, field);
    }

    /**
     * 删除hash类型的值,
     *
     * @param key key
     * @param field field
     * @return 返回删除的field个数
     */
    public static long hdel(final String key, Object... field) {
        return commands.hdel(key, field);
    }

    /**
     * 获取String类型缓存
     *
     * @param key key
     * @return 成功返回获取的String, 失败返回null
     * @author Dream
     * @date 2019年7月5日13:28:51
     */
    public static String getString(final String key) {
        return commands.getString(key);
    }

    /**
     * 获取分布式原子性锁
     *
     * @param lockKey lockKey
     * @return 获取成功返回true, 失败返回false
     * @author Dream
     * @date 2019年7月10日18:50:53
     */
    public static boolean getLock(final String lockKey, final long leaseTime, final TimeUnit timeUnit) {
        return commands.getLock(lockKey, leaseTime, timeUnit);
    }

    /**
     * 释放锁
     *
     * @param lockKey lockKey
     * @return 释放锁成功返回true, 失败返回false
     * @author Dream
     * @date 2019年7月10日18:50:53
     */
    public static boolean unLock(final String lockKey) {
        return commands.unLock(lockKey);
    }

    /**
     * 强制释放锁
     *
     * @param lockKey lockKey
     * @return 强制释放锁成功返回true, 失败返回false
     * @author Dream
     * @date 2019年7月10日18:50:53
     */
    public static boolean forceUnlock(final String lockKey) {
        return commands.forceUnlock(lockKey);
    }

    /**
     * 返回一个分布式原子双精度浮点操作对象
     *
     * @param key key
     */
    public static RAtomicDouble getAtomicDouble(final String key) {
        return commands.getAtomicDouble(key);
    }

    /**
     * 返回一个分布式长整型RAtomicLong对象
     * {@link java.util.concurrent.atomic.AtomicLong}
     *
     * @param key key
     */
    public static RAtomicLong getAtomicLong(final String key) {
        return commands.getAtomicLong(key);
    }

    /**
     * 返回一个分布式列表,确保了元素插入时的顺序
     * {@link java.util.List}
     *
     * @param key key
     */
    public static <T> RList<T> getList(final String key) {
        return commands.getList(key);
    }

    /**
     * 返回一个分布式Set
     * {@link java.util.Set}
     *
     * @param key key
     */
    public static <T> RSet<T> getSet(final String key) {
        return commands.getSet(key);
    }

    /**
     * 返回一个分布式映射结构的RMap,同时还保持了元素的插入顺序
     * {@link java.util.concurrent.ConcurrentMap}
     * {@link java.util.Map}
     *
     * @param key key
     */
    public static <K, V> RMap<K, V> getMap(final String key) {
        return commands.getMap(key);
    }

    /**
     * 返回用来储存与地理位置有关的对象桶。
     * @param key key
     */
    public static <T> RGeo<T> getGeo(final String key) {
        return commands.getGeo(key);
    }

    /**
     * 返回Java分布式布隆过滤器
     * @param key key
     */
    public static <T> RBloomFilter<T> getBloomFilter(final String key) {
        return commands.getBloomFilter(key);
    }

    /**
     * 基于Redis的Redisson分布式无界队列（Queue）结构的RQueue
     * {@link java.util.Queue}
     *
     * @param key key
     */
    public static <T> RQueue<T> getQueue(final String key) {
        return commands.getQueue(key);
    }

    /**
     * 返回基于Redis的Redisson分布式无界阻塞队列（Blocking Queue）结构的RBlockingQueue
     * {@link java.util.concurrent.BlockingQueue}
     *
     * @param key key
     */
    public static <T> RBlockingQueue<T> getBlockingQueue(final String key) {
        return commands.getBlockingQueue(key);
    }

    /**
     * 返回一个RStream对象
     *
     * @param key key
     */
    public static <K, V> RStream<K, V> getStream(final String key) {
        return commands.getStream(key);
    }

    /**
     * 基于Redis的Redisson分布式可重入锁RLock
     * {@link java.util.concurrent.locks.Lock}
     * @param key key
     */
    public static RLock getFairLock(final String key) {
        return commands.getFairLock(key);
    }

    /**
     * 基于Redis的Redisson分布式联锁RedissonMultiLock对象可以将多个RLock对象关联为一个联锁，
     * 每个RLock对象实例可以来自于不同的Redisson实例
     * @param locks locks
     */
    public static RLock getMultiLock(RLock... locks) {
        return commands.getMultiLock(locks);
    }

    /**
     * 基于Redis的Redisson红锁RedissonRedLock对象实现了Redlock介绍的加锁算法。该对象也可以用来将多个RLock对象关联为一个红锁，
     * 每个RLock对象实例可以来自于不同的Redisson实例
     *
     * @param locks locks
     */
    public static RLock getRedLock(RLock... locks) {
        return commands.getRedLock(locks);
    }

    /**
     * 基于Redis的Redisson分布式可重入读写锁RReadWriteLock
     * 同时还支持自动过期解锁。该对象允许同时有多个读取锁，但是最多只能有一个写入锁
     * <p>
     * {@link java.util.concurrent.locks.ReadWriteLock}
     *
     * @param lockKey lockKey
     */
    public static RReadWriteLock getReadWriteLock(String lockKey) {
        return commands.getReadWriteLock(lockKey);
    }

    /**
     * 基于Redisson的Redisson分布式闭锁（CountDownLatch）
     * {@link java.util.concurrent.CountDownLatch}
     *
     * @param key key
     */
    public static RCountDownLatch getCountDownLatch(String key) {
        return commands.getCountDownLatch(key);
    }

    /**
     * Redisson的分布式RBitSetJava对象
     * 可以理解为它是一个分布式的可伸缩式位向量
     * {@link java.util.BitSet}
     *
     * @param key key
     * @return
     */
    public static RBitSet getBitSet(String key) {
        return commands.getBitSet(key);
    }

    /**
     * 基于List的Multimap在保持插入顺序的同时允许一个字段下包含重复的元素
     *
     * @param key key
     */
    public static <K, V> RListMultimap<K, V> getListMultimap(String key) {
        return commands.getListMultimap(key);
    }

    /**
     * 分布式远程服务
     * 基于Redis的Java分布式远程服务，可以用来通过共享接口执行存在于另一个Redisson实例里的对象方法。
     * 换句话说就是通过Redis实现了Java的远程过程调用（RPC）。
     * 分布式远程服务基于可以用POJO对象，方法的参数和返回类不受限制，可以是任何类型
     * @param key key
     */
    public static RRemoteService getRemoteService(String key) {
        return commands.getRemoteService(key);
    }

    /**
     * 基于集（Set）的多值映射（Multimap）
     * 基于Set的Multimap不允许一个字段值包含有重复的元素。
     * @param key key
     */
    public static <K, V> RSetMultimap<K, V> getSetMultimap(String key) {
        return commands.getSetMultimap(key);
    }

    /**
     * Redisson的分布式话题RTopic对象实现了发布、订阅的机制
     * 在Redis节点故障转移（主从切换）或断线重连以后，所有的话题监听器将自动完成话题的重新订阅。
     * @param key key
     */
    public static RTopic getTopic(String key) {
        return commands.getTopic(key);
    }

    /**
     * 通用对象桶（Object Bucket）
     * Redisson的分布式RBucketJava对象是一种通用对象桶可以用来存放任类型的对象。
     *
     * @param key key
     */
    public static <T> RBucket<T> getBuckets(String key) {
        return commands.getBucket(key);
    }
}
