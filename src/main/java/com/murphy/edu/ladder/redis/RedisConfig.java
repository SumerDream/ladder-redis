package com.murphy.edu.ladder.redis;

import com.murphy.edu.ladder.redis.jedis.JedisConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * config model
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

    @Value("${ladder.jedis.jedis-pool-max-total:200}")
    private int jedisPoolMaxTotal;

    @Value("${ladder.jedis.jedis-pool-max-idle:50}")
    private int jedisPoolMaxIdle;

    @Value("${ladder.jedis.jedis-pool-min-idle:20}")
    private int jedisPoolMinIdle;

    @Value("${ladder.jedis.jedis-pool-max-wait:2000}")
    private int jedisPoolMaxWait;

    @Value("${ladder.jedis.jedis-so-timeout:2000}")
    private int jedisSoTimeout;

    @Value("${ladder.jedis.exception.solution:throw}")
    private String exceptionSolution;

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

}
