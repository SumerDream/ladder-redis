package com.murphy.edu.ladder.redis;

import com.murphy.edu.ladder.redis.jedis.JedisConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * autoconfiguration
 */
@EnableConfigurationProperties({JedisConfig.class})
public class EnableAutoRedisConfiguration {
}
