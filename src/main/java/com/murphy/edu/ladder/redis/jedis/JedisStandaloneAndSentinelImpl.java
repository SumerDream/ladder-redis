package com.murphy.edu.ladder.redis.jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Dream
 * @ 单机和哨兵模式实现
 * @date 2019年7月5日13:10:41
 */
@Slf4j
public class JedisStandaloneAndSentinelImpl implements JedisCommands {

    @Override
    public boolean setString(String key, String value, Integer expire) {
        Jedis jedis = JedisCenter.getJedis();
        boolean result = true;
        try {
            if (expire > 0) {
                jedis.setex(key, expire, value);
            } else {
                jedis.set(key, value);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public boolean setString(String key, String value) {
        return setString(key, value, -1);
    }

    @Override
    public String getString(String key) {
        Jedis jedis = JedisCenter.getJedis();
        String result = null;

        try {
            result = jedis.get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public Long operateNum(String key, Long num) {
        Jedis jedis = JedisCenter.getJedis();
        Long result = null;
        try {
            result = jedis.incrBy(key, num);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作整数类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public Long getNum(String key) {
        Jedis jedis = JedisCenter.getJedis();
        Long result = null;
        try {
            String value = jedis.get(key);
            result = value == null ? 0 : Long.parseLong(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取整数类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public BigDecimal operateFloat(String key, BigDecimal amt) {
        Jedis jedis = JedisCenter.getJedis();
        JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
        BigDecimal rsp = null;
        try {
            Double result = jedis.incrByFloat(key, Double.parseDouble(JedisUtil.df.format(amt)));
            if (result <= 0) {
                jedis.del(key);
                result = (double) 0;
            }
            rsp = new BigDecimal(String.valueOf(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作浮点类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return rsp;
    }

    @Override
    public BigDecimal getFloat(String key) {
        Jedis jedis = JedisCenter.getJedis();
        BigDecimal rsp = null;
        try {
            String result = jedis.get(key);
            JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
            rsp = new BigDecimal(result == null ? "0" : JedisUtil.df.format(Double.valueOf(result)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取浮点类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
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
        Jedis jedis = JedisCenter.getJedis();
        T result = null;

        try {
            if (jedis.exists(key)) {
                result = JSON.parseObject(jedis.get(key), type);
                if (expire > 0) {
                    jedis.expire(key, expire);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }

        return result;
    }

    @Override
    public boolean remove(String key) {
        Jedis jedis = JedisCenter.getJedis();
        boolean result = true;
        try {
            jedis.del(key);
        } catch (Exception e) {
            result = false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }

        return result;
    }

    @Override
    public String operateRedisLock(String lockKey, int expire) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            String lockValue = UUID.randomUUID().toString() + Thread.currentThread().getId();
            Object result = jedis.eval(JedisUtil.SCRIPT_LOCK, 1, lockKey, lockValue, String.valueOf(expire));
            return (result == null || ((Long) result).intValue() == 0) ? null : lockValue;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            JedisCenter.closeJedis(jedis);
        }
    }

    @Override
    public boolean cancelRedisLock(String lockKey, String lockValue) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Object result = jedis.eval(JedisUtil.SCRIPT_UNLOCK, 1, lockKey, lockValue);
            return result != null && ((Long) result).intValue() != 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        //return remove(lock);
    }

    @Override
    public void expire(String key, Integer expire) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            jedis.expire(key, expire);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
    }

    @Override
    public boolean setHashCached(String key, String field, String value) {
        Jedis jedis = JedisCenter.getJedis();
        boolean result = true;
        try {
            jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public boolean setHashCached(String key, String field, String value, Integer expire) {

        Jedis jedis = JedisCenter.getJedis();
        boolean result = true;

        try {
            jedis.hset(key, field, value);
            if (expire > 0) {
                jedis.expire(key, expire);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }

        return result;
    }

    @Override
    public boolean setHashCachedNX(String key, String field, String value) {

        Jedis jedis = JedisCenter.getJedis();
        boolean result = true;

        try {
            Long hsetnx = jedis.hsetnx(key, field, value);
            if (hsetnx == 0) {
                result = false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }

        return result;
    }

    @Override
    public String getHashCached(String key, String field) {

        Jedis jedis = JedisCenter.getJedis();
        String result = null;

        try {
            result = jedis.hget(key, field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }

        return result;
    }

    @Override
    public boolean removeHashKey(String key, String field) {
        Jedis jedis = JedisCenter.getJedis();
        boolean result = true;

        try {
            jedis.hdel(key, field);
        } catch (Exception e) {
            result = false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public Map<String, String> getHashAll(String key) {
        Jedis jedis = JedisCenter.getJedis();
        Map<String, String> map = null;
        try {
            map = jedis.hgetAll(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return map;
    }

    @Override
    public List<String> getList(String key, int start, int end) {
        Jedis jedis = JedisCenter.getJedis();
        List<String> list = null;
        try {
            list = jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return list;
    }

    @Override
    public boolean setLeftList(String key, String... vaules) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.lpush(key, vaules);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return false;
    }

    @Override
    public boolean setRightList(String key, String... vaules) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.rpush(key, vaules);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return false;
    }

    @Override
    public Integer getListLength(String key) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.llen(key);
            return num.intValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return 0;
    }

    @Override
    public boolean removeList(String key, String value) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.lrem(key, 0, value);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return false;
    }

    @Override
    public boolean setSet(String key, String... values) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.sadd(key, values);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return false;
    }

    @Override
    public Set<String> getSet(String key) {
        Jedis jedis = JedisCenter.getJedis();
        Set<String> set = null;
        try {
            set = jedis.smembers(key);
            return set;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return set;
    }

    @Override
    public Integer getSetLength(String key) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.scard(key);
            return num.intValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return 0;
    }

    @Override
    public boolean addSet(String key, String... values) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.sadd(key, values);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return false;
    }

    @Override
    public boolean removeSet(String key, String... values) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            Long num = jedis.srem(key, values);
            if (num != 0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return false;
    }

    @Override
    public Long hOperateNum(String key, String field, Long num) {
        Jedis jedis = JedisCenter.getJedis();
        Long result = null;
        try {
            result = jedis.hincrBy(key, field, num);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作整数类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public Long hGetNum(String key, String field) {
        Jedis jedis = JedisCenter.getJedis();
        Long result = null;
        try {
            String value = jedis.hget(key, field);
            result = value == null ? 0 : Long.parseLong(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取整数类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public BigDecimal hOperateFloat(String key, String field, BigDecimal amt) {
        Jedis jedis = JedisCenter.getJedis();
        JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
        BigDecimal rsp = null;
        try {
            Double result = jedis.hincrByFloat(key, field, Double.parseDouble(JedisUtil.df.format(amt)));
            if (result <= 0) {
                jedis.hdel(key, field);
                result = (double) 0;
            }
            rsp = new BigDecimal(String.valueOf(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis操作浮点类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return rsp;
    }

    @Override
    public BigDecimal hGetFloat(String key, String field) {
        Jedis jedis = JedisCenter.getJedis();
        BigDecimal rsp = null;
        try {
            String result = jedis.hget(key, field);
            JedisUtil.df.setRoundingMode(RoundingMode.HALF_UP);
            rsp = new BigDecimal(result == null ? "0" : JedisUtil.df.format(Double.valueOf(result)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Redis获取浮点类型key异常,key为" + key);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return rsp;
    }

    @Override
    public Long getHashLen(String key) {
        Jedis jedis = JedisCenter.getJedis();
        Long result = null;
        try {
            result = jedis.hlen(key);
            if (result == null) {
                result = 0L;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return result;
    }

    @Override
    public Set<String> sinterSet(String... keys) {
        Jedis jedis = JedisCenter.getJedis();
        Set<String> set = new HashSet<>();
        try {
            set = jedis.sinter(keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return set;
    }

    @Override
    public Set<String> sunionSet(String... keys) {
        Jedis jedis = JedisCenter.getJedis();
        Set<String> set = new HashSet<>();
        try {
            set = jedis.sunion(keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return set;
    }

    @Override
    public Set<String> sdiffSet(String... keys) {
        Jedis jedis = JedisCenter.getJedis();
        Set<String> set = new HashSet<>();
        try {
            set = jedis.sdiff(keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return set;
    }

    @Override
    public long append(String key, String appendValue) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.append(key, appendValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public long decr(String key) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.decr(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public long initAndDecr(String key, long[] initValue) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            if (initValue == null || jedis.exists(key)) {
                return jedis.decr(key);
            } else {
                jedis.set(key, String.valueOf(initValue[0] - 1));
                return initValue[0] - 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public long initForceAndDecr(String key, long[] initValue) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            if (initValue == null) {
                return jedis.decr(key);
            } else {
                jedis.set(key, String.valueOf(initValue[0] - 1));
                return initValue[0] - 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public long decrBy(String key, long[] byValue) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            if (byValue == null) {
                return jedis.decr(key);
            } else {
                return jedis.decrBy(key, byValue[0]);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public long initAndDecrBy(String key, long initValue, long[] byValue) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            if (byValue == null || !jedis.exists(key)) {
                jedis.set(key, String.valueOf(initValue - 1));
                return initValue - 1;
            }
            return jedis.decrBy(key, byValue[0]);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public Object eval(String script) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.eval(script);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public Object eval(String script, int keyCount, String[] params) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.eval(script, keyCount, params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -9_999L;
    }

    @Override
    public boolean expireAt(String key, long timestamp) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.expireAt(key, timestamp) > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return false;
    }

    @Override
    public boolean setAndExpireAt(String key, Object value, long timestamp) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            if (timestamp <= System.currentTimeMillis()) {
                return jedis.del(key) >= 0;
            }
            jedis.setex(key, Long.valueOf((timestamp - System.currentTimeMillis()) / 1000).intValue(), String.valueOf(value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return true;
    }

    @Override
    public long geoAdd(String key, double longitude, double latitude, String member) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.geoadd(key, longitude, latitude, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return 0;
    }

    @Override
    public List<GeoCoordinate> geoPos(String key, String[] members) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.geopos(key, members);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return null;
    }

    @Override
    public Double geoDist(String key, String member1, String member2) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.geodist(key, member1, member2);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return null;
    }

    @Override
    public Double geoDist(String key, String member1, String member2, GeoUnit unit) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.geodist(key, member1, member2, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return null;
    }

    @Override
    public List<GeoRadiusResponse> geoRadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.georadius(key, longitude, latitude, radius, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return null;
    }

    @Override
    public List<GeoRadiusResponse> geoRadiusByMember(String key, String member, double radius, GeoUnit unit) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.georadiusByMember(key, member, radius, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return null;
    }

    @Override
    public int hexists(String key, String field) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.hexists(key, field) ? 1 : 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -1;
    }

    @Override
    public int exists(String key) {
        Jedis jedis = JedisCenter.getJedis();
        try {
            return jedis.exists(key) ? 1 : 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisCenter.closeJedis(jedis);
        }
        return -1;
    }
}
