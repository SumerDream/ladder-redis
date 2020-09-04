package com.murphy.edu.ladder.redis.jedis;

import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dream
 * @ redis命令接口
 * @date 2019年7月5日13:10:41
 */
public interface JedisCommands {

    boolean setString(final String key, final String value, final Integer expire);

    boolean setString(final String key, final String value);

    String getString(final String key);

    Long operateNum(String key, Long num);

    Long getNum(String key);

    BigDecimal operateFloat(String key, BigDecimal amt);

    BigDecimal getFloat(String key);

    boolean setBean(final String key, final Object bean);

    boolean setBean(final String key, final Object bean, final Integer expire);

    String getBean(final String key);

    <T> T getBean(final String key, final TypeReference<T> type);

    <T> T getBean(String key, TypeReference<T> type, Integer expire);

    boolean remove(final String key);

    String operateRedisLock(String lockKey, int expire);

    boolean cancelRedisLock(String lockKey, String lockValue);

    void expire(final String key, final Integer expire);

    boolean setHashCached(final String key, final String field, final String value);

    boolean setHashCached(final String key, final String field, final String value, final Integer expire);

    boolean setHashCachedNX(final String key, final String field, final String value);

    String getHashCached(final String key, final String field);

    Long getHashLen(final String key);

    Long hOperateNum(String key, String field, Long num);

    Long hGetNum(String key, String field);

    BigDecimal hOperateFloat(String key, String field, BigDecimal amt);

    BigDecimal hGetFloat(String key, String field);

    boolean removeHashKey(final String key, final String field);

    Map<String, String> getHashAll(final String key);

    List<String> getList(String key, int start, int end);

    boolean setLeftList(String key, String... vaules);

    boolean setRightList(String key, String... vaules);

    Integer getListLength(String key);

    boolean removeList(String key, String value);

    boolean setSet(String key, String... values);

    Set<String> getSet(String key);

    Integer getSetLength(String key);

    boolean addSet(String key, String... values);

    boolean removeSet(String key, String... values);

    Set<String> sinterSet(String... keys);

    Set<String> sunionSet(String... keys);

    Set<String> sdiffSet(String... keys);

    long append(String key, String appendValue);

    long decr(String key);

    long initAndDecr(String key, long[] initValue);

    long initForceAndDecr(String key, long[] initValue);

    long decrBy(String key, long[] byValue);

    long initAndDecrBy(String key, long initValue, long[] byValue);

    default Object eval(String script) {
        return null;
    }

    Object eval(String script, int keyCount, String[] params);

    boolean expireAt(String key, long timestamp);

    boolean setAndExpireAt(String key, Object value, long timestamp);

    long geoAdd(String key, double longitude, double latitude, String member);

    List<GeoCoordinate> geoPos(String key, String[] members);

    Double geoDist(String key, String member1, String member2);

    Double geoDist(String key, String member1, String member2, GeoUnit unit);

    List<GeoRadiusResponse> geoRadius(String key, double longitude, double latitude, double radius, GeoUnit unit);

    List<GeoRadiusResponse> geoRadiusByMember(String key, String member, double radius, GeoUnit unit);

    int hexists(String key, String field);

    int exists(String key);
}
