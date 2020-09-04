package com.murphy.edu.ladder.redis.jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Dream
 * @description 集群模式redis实现类
 * @date 2019年7月5日13:10:41
 */
@Slf4j
public class JedisClusterImpl implements JedisCommands {

    private JedisCluster jedisCluster = JedisCenter.jedisCluster;

    @Override
    public boolean setString(String key, String value, Integer expire) {
        boolean result = true;
        try {
            if (expire > 0) {
                jedisCluster.setex(key, expire, value);
            } else {
                jedisCluster.set(key, value);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }

    @Override
    public boolean setString(String key, String value) {
        return setString(key, value, -1);
    }

    @Override
    public String getString(String key) {
        String result = null;
        try {
            result = jedisCluster.get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Long operateNum(String key, Long num) {
        Long result = null;
        try {
            result = jedisCluster.incrBy(key, num);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作整数类型key异常,key为" + key);
        }
        return result;
    }

    @Override
    public Long getNum(String key) {
        Long result = null;
        try {
            String value = jedisCluster.get(key);
            result = value == null ? 0 : Long.parseLong(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取整数类型key异常,key为" + key);
        }
        return result;
    }

    @Override
    public BigDecimal operateFloat(String key, BigDecimal amt) {
        JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
        BigDecimal rsp = null;
        try {
            double doubleValue = Double.parseDouble(JedisUtil.df.format(amt));
            Double result = jedisCluster.incrByFloat(key, doubleValue);
            if (result <= 0) {
                jedisCluster.del(key);
                result = (double) 0;
            }
            rsp = new BigDecimal(String.valueOf(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作浮点类型key异常,key为" + key);
        }
        return rsp;
    }

    @Override
    public BigDecimal getFloat(String key) {
        BigDecimal rsp = null;
        try {
            String result = jedisCluster.get(key);
            JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
            rsp = new BigDecimal(result == null ? "0" : JedisUtil.df.format(Double.valueOf(result)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取浮点类型key异常,key为" + key);
        }
        return rsp;
    }

    @Override
    public boolean setBean(String key, Object bean) {
        return setBean(key, bean, -1);
    }

    @Override
    public boolean setBean(String key, Object bean, Integer expire) {
        String value = JSON.toJSONString(bean);
        return setString(key, value, expire);
    }

    @Override
    public String getBean(String key) {
        return getString(key);
    }

    @Override
    public <T> T getBean(String key, TypeReference<T> type) {
        return getBean(key, type, 0);
    }

    @Override
    public <T> T getBean(String key, TypeReference<T> type, Integer expire) {
        T result = null;
        try {
            if (jedisCluster.exists(key)) {
                result = JSON.parseObject(jedisCluster.get(key), type);
                if (expire > 0) {
                    jedisCluster.expire(key, expire);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean remove(String key) {
        boolean result = true;
        try {
            jedisCluster.del(key);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public String operateRedisLock(String lockKey, int expire) {
        try {
            String lockValue = UUID.randomUUID().toString() + Thread.currentThread().getId();
            Object result = jedisCluster.eval(JedisUtil.SCRIPT_LOCK, 1, lockKey, lockValue, String.valueOf(expire));
            return (result == null || ((Long) result).intValue() == 0) ? null : lockValue;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean cancelRedisLock(String lockKey, String lockValue) {
        try {
            Object result = jedisCluster.eval(JedisUtil.SCRIPT_UNLOCK, 1, lockKey, lockValue);
            return result != null && ((Long) result).intValue() != 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void expire(String key, Integer expire) {

        try {
            jedisCluster.expire(key, expire);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean setHashCached(String key, String field, String value) {

        boolean result = true;
        try {
            jedisCluster.hset(key, field, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }

    @Override
    public boolean setHashCached(String key, String field, String value, Integer expire) {


        boolean result = true;

        try {
            jedisCluster.hset(key, field, value);
            if (expire > 0) {
                jedisCluster.expire(key, expire);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        }

        return result;
    }

    @Override
    public boolean setHashCachedNX(String key, String field, String value) {


        boolean result = true;

        try {
            Long hsetnx = jedisCluster.hsetnx(key, field, value);
            if (hsetnx == 0) {
                result = false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        }

        return result;
    }

    @Override
    public String getHashCached(String key, String field) {
        String result = null;
        try {
            result = jedisCluster.hget(key, field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    @Override
    public boolean removeHashKey(String key, String field) {
        boolean result = true;
        try {
            jedisCluster.hdel(key, field);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public Map<String, String> getHashAll(String key) {
        Map<String, String> map = null;
        try {
            map = jedisCluster.hgetAll(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return map;
    }

    @Override
    public List<String> getList(String key, int start, int end) {
        List<String> list = null;
        try {
            list = jedisCluster.lrange(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean setLeftList(String key, String... vaules) {
        try {
            Long num = jedisCluster.lpush(key, vaules);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean setRightList(String key, String... vaules) {
        try {
            Long num = jedisCluster.rpush(key, vaules);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Integer getListLength(String key) {
        try {
            Long num = jedisCluster.llen(key);
            return num.intValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean removeList(String key, String value) {
        try {
            Long num = jedisCluster.lrem(key, 0, value);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean setSet(String key, String... values) {
        try {
            Long num = jedisCluster.sadd(key, values);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Set<String> getSet(String key) {
        Set<String> set = null;
        try {
            set = jedisCluster.smembers(key);
            return set;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return set;
    }

    @Override
    public Integer getSetLength(String key) {
        try {
            Long num = jedisCluster.scard(key);
            return num.intValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean addSet(String key, String... values) {
        try {
            Long num = jedisCluster.sadd(key, values);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean removeSet(String key, String... values) {
        try {
            Long num = jedisCluster.srem(key, values);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Long hOperateNum(String key, String field, Long num) {
        Long result = null;
        try {
            result = jedisCluster.hincrBy(key, field, num);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作整数类型key异常,key为" + key);
        }
        return result;
    }

    @Override
    public Long hGetNum(String key, String field) {
        Long result = null;
        try {
            String value = jedisCluster.hget(key, field);
            result = value == null ? 0 : Long.parseLong(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取整数类型key异常,key为" + key);
        }
        return result;
    }

    @Override
    public BigDecimal hOperateFloat(String key, String field, BigDecimal amt) {
        JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
        BigDecimal rsp = null;
        try {
            double doubleValue = Double.parseDouble(JedisUtil.df.format(amt));
            Double result = jedisCluster.hincrByFloat(key, field, doubleValue);
            if (result <= 0) {
                jedisCluster.hdel(key, field);
                result = (double) 0;
            }
            rsp = new BigDecimal(String.valueOf(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作浮点类型key异常,key为" + key);
        }
        return rsp;
    }

    @Override
    public BigDecimal hGetFloat(String key, String field) {
        BigDecimal rsp = null;
        try {
            String result = jedisCluster.hget(key, field);
            JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
            rsp = new BigDecimal(result == null ? "0" : JedisUtil.df.format(Double.valueOf(result)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取浮点类型key异常,key为" + key);
        }
        return rsp;
    }

    @Override
    public Long getHashLen(String key) {
        Long result = null;
        try {
            result = jedisCluster.hlen(key);
            if (result == null) {
                result = 0L;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Set<String> sinterSet(String... keys) {
        Set<String> set = new HashSet<>();
        try {
            set = jedisCluster.sinter(keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return set;
    }

    @Override
    public Set<String> sunionSet(String... keys) {
        Set<String> set = new HashSet<>();
        try {
            set = jedisCluster.sunion(keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return set;
    }

    @Override
    public Set<String> sdiffSet(String... keys) {
        Set<String> set = new HashSet<>();
        try {
            set = jedisCluster.sdiff(keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return set;
    }

    @Override
    public long append(String key, String appendValue) {
        try {
            return jedisCluster.append(key, appendValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -9_999L;
    }

    @Override
    public long decr(String key) {
        try {
            return jedisCluster.decr(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -9999L;
    }

    @Override
    public long initAndDecr(String key, long[] initValue) {
        try {
            if (initValue == null || jedisCluster.exists(key)) {
                return jedisCluster.decr(key);
            } else {
                jedisCluster.set(key, String.valueOf(initValue[0] - 1));
                return initValue[0] - 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -9_999L;
    }

    @Override
    public long initForceAndDecr(String key, long[] initValue) {
        try {
            if (initValue == null) {
                return jedisCluster.decr(key);
            } else {
                jedisCluster.set(key, String.valueOf(initValue[0] - 1));
                return initValue[0] - 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -9_999L;
    }

    @Override
    public long decrBy(String key, long[] byValue) {
        try {
            if (byValue == null) {
                return jedisCluster.decr(key);
            } else {
                return jedisCluster.decrBy(key, byValue[0]);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -9_999L;
    }

    @Override
    public long initAndDecrBy(String key, long initValue, long[] byValue) {
        try {
            if (byValue == null || !jedisCluster.exists(key)) {
                jedisCluster.set(key, String.valueOf(initValue - 1));
                return initValue - 1;
            }
            return jedisCluster.decrBy(key, byValue[0]);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -9_999L;
    }

    @Override
    public Object eval(String script, int keyCount, String[] params) {
        try {
            return jedisCluster.eval(script, keyCount, params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -9_999L;
    }

    @Override
    public boolean expireAt(String key, long timestamp) {
        try {
            return jedisCluster.expireAt(key, timestamp) > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean setAndExpireAt(String key, Object value, long timestamp) {
        try {
            if (timestamp <= System.currentTimeMillis()) {
                return jedisCluster.del(key) >= 0;
            }
            jedisCluster.setex(key, Long.valueOf((timestamp - System.currentTimeMillis()) / 1000).intValue(), String.valueOf(value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public long geoAdd(String key, double longitude, double latitude, String member) {
        try {
            return jedisCluster.geoadd(key, longitude, latitude, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<GeoCoordinate> geoPos(String key, String[] members) {
        try {
            return jedisCluster.geopos(key, members);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Double geoDist(String key, String member1, String member2) {
        try {
            return jedisCluster.geodist(key, member1, member2);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Double geoDist(String key, String member1, String member2, GeoUnit unit) {
        try {
            return jedisCluster.geodist(key, member1, member2, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<GeoRadiusResponse> geoRadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        try {
            return jedisCluster.georadius(key, longitude, latitude, radius, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<GeoRadiusResponse> geoRadiusByMember(String key, String member, double radius, GeoUnit unit) {
        try {
            return jedisCluster.georadiusByMember(key, member, radius, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int hexists(String key, String field) {
        try {
            return jedisCluster.hexists(key, field) ? 1 : 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public int exists(String key) {
        try {
            return jedisCluster.exists(key) ? 1 : 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -1;
    }

}
