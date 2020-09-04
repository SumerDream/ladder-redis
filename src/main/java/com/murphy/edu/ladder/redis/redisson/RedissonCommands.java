package com.murphy.edu.ladder.redis.redisson;

import org.redisson.api.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Dream
 * @ redisson命令接口
 * @date 2019年7月5日13:10:41
 */
public interface RedissonCommands {

    boolean setString(final String key, final String value, final long expire, final TimeUnit timeUnit);

    boolean setString(final String key, final String value);

    boolean isExists(final String key);

    String getString(final String key);

    boolean setnx(final String key, final Object value);

    boolean hset(final String key, final String field, final Object value);

    <T> T hget(final String key, final String field);

    long hdel(final String key, final Object... field);

    boolean getLock(final String lockKey, final long leaseTime, final TimeUnit timeUnit);

    boolean unLock(final String lockKey);

    boolean forceUnlock(final String lockKey);

    RAtomicDouble getAtomicDouble(final String key);

    RAtomicLong getAtomicLong(final String key);

    <T> RList<T> getList(final String key);

    <T> RSet<T> getSet(final String key);

    <K, V> RMap<K, V> getMap(final String key);

    <T> RGeo<T> getGeo(final String key);

    <T> RBloomFilter<T> getBloomFilter(final String key);

    <T> RQueue<T> getQueue(final String key);

    <T> RBlockingQueue<T> getBlockingQueue(final String key);

    <K, V> RStream<K, V> getStream(final String key);

    RLock getFairLock(final String key);

    RLock getMultiLock(RLock... locks);

    RLock getRedLock(RLock... locks);

    RReadWriteLock getReadWriteLock(final String lockKey);

    RCountDownLatch getCountDownLatch(final String key);

    RBitSet getBitSet(final String key);

    <K, V> RListMultimap<K, V> getListMultimap(final String key);

    RRemoteService getRemoteService(final String key);

    <K, V> RSetMultimap<K, V> getSetMultimap(final String key);

    RTopic getTopic(final String key);

    <T> RBucket<T> getBucket(final String key);
}
