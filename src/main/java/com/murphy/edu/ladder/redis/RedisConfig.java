package com.murphy.edu.ladder.redis;

import com.murphy.edu.ladder.redis.jedis.JedisConfig;
import com.murphy.edu.ladder.redis.redisson.RedissonConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @author Dream
 * @version 1.0
 * @description redis配置
 * @date 2020/8/1 4:59 下午
 */
public class RedisConfig {

    //jedis

    @Value("${ladder.jedis.jedis-mode:}")
    private String jedisMode;

    @Value("${ladder.jedis.jedis-nodes:}")
    private String jedisNodes;

    @Value("${ladder.jedis.jedis-database:0}")
    private int jedisDatabase;

    @Value("${ladder.jedis.jedis-password:}")
    private String jedisPassword;

    @Value("${ladder.jedis.jedis-name:}")
    private String jedisName;

    @Value("${ladder.jedis.jedis-pool-max-total:100}")
    private int jedisPoolMaxTotal;

    @Value("${ladder.jedis.jedis-pool-max-idle:20}")
    private int jedisPoolMaxIdle;

    @Value("${ladder.jedis.jedis-pool-min-idle:10}")
    private int jedisPoolMinIdle;

    @Value("${ladder.jedis.jedis-pool-max-wait:3000}")
    private int jedisPoolMaxWait;

    @Value("${ladder.jedis.jedis-so-timeout:3000}")
    private int jedisSoTimeout;

    //redisson

    @Value("${ladder.redisson.redisson-mode:}")
    private String redissonMode;

    @Value("${ladder.redisson.redisson-address:}")
    private String redissonAddress;

    @Value("${ladder.redisson.redisson-database:0}")
    private int redissonDatabase;

    @Value("${ladder.redisson.redisson-password:}")
    private String redissonPassword;

    @Value("${ladder.redisson.redisson-master-name:}")
    private String redissonMasterName;

    @Value("${ladder.redisson.redisson-codec:org.redisson.codec.FstCodec}")
    private String redissonCodec;

    @Value("${ladder.redisson.redisson-connect-timeout:10000}")
    private int redissonConnectTimeout;

    @Value("${ladder.redisson.redisson-connection-pool-size:64}")
    private int redissonConnectionPoolSize;

    @Value("${ladder.redisson.redisson-idle-connection-timeout:10000}")
    private int redissonIdleConnectionTimeout;

    @Value("${ladder.redisson.redisson-master-connection-pool-size:64}")
    private int redissonMasterConnectionPoolSize;

    @Value("${ladder.redisson.redisson-retry-attempts:3}")
    private int redissonRetryAttempts;

    @Value("${ladder.redisson.redisson-retry-interval:1500}")
    private int redissonRetryInterval;

    @Value("${ladder.redisson.redisson-scan-interval:2000}")
    private int redissonScanInterval;

    @Value("${ladder.redisson.redisson-slave-connection-pool-size:64}")
    private int redissonSlaveConnectionPoolSize;

    @Value("${ladder.redisson.redisson-timeout:3000}")
    private int redissonTimeout;

    @Bean
    @Conditional(JedisInitCondition.class)
    public JedisConfig jedisConfig() {

        return JedisConfig.getInstance()

                .setJedisMode(jedisMode)

                .setJedisNodes(jedisNodes)

                .setJedisDatabase(jedisDatabase)

                .setJedisPassword(jedisPassword)

                .setJedisName(jedisName)

                .setJedisPoolMaxTotal(jedisPoolMaxTotal)

                .setJedisPoolMaxIdle(jedisPoolMaxIdle)

                .setJedisPoolMinIdle(jedisPoolMinIdle)

                .setJedisPoolMaxWait(jedisPoolMaxWait)

                .setJedisSoTimeout(jedisSoTimeout)

                .init();
    }


    @Bean
    @Conditional(RedissonInitCondition.class)
    public RedissonConfig redissonConfig() {

        return RedissonConfig.getInstance()

                .setRedissonMode(redissonMode)

                .setRedissonAddress(redissonAddress)

                .setRedissonCodec(redissonCodec)

                .setRedissonConnectionPoolSize(redissonConnectionPoolSize)

                .setRedissonConnectTimeout(redissonConnectTimeout)

                .setRedissonDatabase(redissonDatabase)

                .setRedissonIdleConnectionTimeout(redissonIdleConnectionTimeout)

                .setRedissonMasterName(redissonMasterName)

                .setRedissonPassword(redissonPassword)

                .setRedissonScanInterval(redissonScanInterval)

                .setRedissonSlaveConnectionPoolSize(redissonSlaveConnectionPoolSize)

                .setRedissonTimeout(redissonTimeout)

                .setRedissonRetryInterval(redissonRetryInterval)

                .setRedissonMasterConnectionPoolSize(redissonMasterConnectionPoolSize)

                .setRedissonRetryAttempts(redissonRetryAttempts)

                .init();


    }
}
