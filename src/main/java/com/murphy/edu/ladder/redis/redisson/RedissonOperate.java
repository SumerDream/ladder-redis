package com.murphy.edu.ladder.redis.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonOperate implements RedissonCommands {

    private static final String LOCK_KEY_PRE = "redisson:lock:key:";

    private RedissonClient redissonClient = RedissonCenter.redissonClient;

    @Override
    public boolean setString(String key, String value, final long expire, final TimeUnit timeUnit) {
        try {
            redissonClient.getBucket(key).set(value, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean setString(String key, String value) {
        try {
            redissonClient.getBucket(key).set(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean isExists(String key) {
        try {
            return redissonClient.getBucket(key).isExists();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hset(String key, String field, Object value) {
        try {
            Object previousValue = redissonClient.getMap(key).put(field, value);
            return previousValue != null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public <T> T hget(String key, String field) {
        try {
            return (T) redissonClient.getMap(key).get(field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public long hdel(String key, Object... field) {
        try {
            return redissonClient.getMap(key).fastRemove(field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public boolean setnx(String key, Object value) {
        try {
            return redissonClient.getBucket(key).trySet(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getString(String key) {
        try {
            Object value = redissonClient.getBucket(key).get();
            return value == null ? null : value.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean getLock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        try {
            RLock lock = redissonClient.getLock(LOCK_KEY_PRE + lockKey);
            lock.lock(leaseTime, timeUnit);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean unLock(String lockKey) {
        try {
            RLock lock = redissonClient.getLock(LOCK_KEY_PRE + lockKey);
            lock.unlock();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean forceUnlock(String lockKey) {
        try {
            RLock lock = redissonClient.getLock(LOCK_KEY_PRE + lockKey);
            return lock.forceUnlock();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RAtomicDouble getAtomicDouble(String key) {
        try {
            return redissonClient.getAtomicDouble(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RAtomicLong getAtomicLong(String key) {
        try {
            return redissonClient.getAtomicLong(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> RList<T> getList(String key) {
        try {
            return redissonClient.getList(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> RSet<T> getSet(String key) {
        try {
            return redissonClient.getSet(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <K, V> RMap<K, V> getMap(String key) {
        try {
            return redissonClient.getMap(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> RGeo<T> getGeo(String key) {
        try {
            return redissonClient.getGeo(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> RBloomFilter<T> getBloomFilter(String key) {
        try {
            return redissonClient.getBloomFilter(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> RQueue<T> getQueue(String key) {
        try {
            return redissonClient.getQueue(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> RBlockingQueue<T> getBlockingQueue(String key) {
        try {
            return redissonClient.getBlockingQueue(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <K, V> RStream<K, V> getStream(String key) {
        try {
            return redissonClient.getStream(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RLock getFairLock(String key) {
        try {
            return redissonClient.getFairLock(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RLock getMultiLock(RLock... locks) {
        try {
            return redissonClient.getMultiLock(locks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RLock getRedLock(RLock... locks) {
        try {
            return redissonClient.getRedLock(locks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RReadWriteLock getReadWriteLock(String lockKey) {
        try {
            return redissonClient.getReadWriteLock(lockKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RCountDownLatch getCountDownLatch(String key) {
        try {
            return redissonClient.getCountDownLatch(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RBitSet getBitSet(String key) {
        try {
            return redissonClient.getBitSet(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <K, V> RListMultimap<K, V> getListMultimap(String key) {
        try {
            return redissonClient.getListMultimap(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RRemoteService getRemoteService(String key) {
        try {
            return redissonClient.getRemoteService(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <K, V> RSetMultimap<K, V> getSetMultimap(String key) {
        try {
            return redissonClient.getSetMultimap(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RTopic getTopic(String key) {
        try {
            return redissonClient.getTopic(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> RBucket<T> getBucket(String key) {
        try {
            return redissonClient.getBucket(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
