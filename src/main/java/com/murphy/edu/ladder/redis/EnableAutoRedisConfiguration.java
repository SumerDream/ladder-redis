package com.murphy.edu.ladder.redis;

import com.murphy.edu.ladder.redis.jedis.JedisConfig;
import com.murphy.edu.ladder.redis.redisson.RedissonConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Dream
 * @version 1.0
 * @description
 * @date 2020/9/3 4:29 下午
 */
@EnableConfigurationProperties({JedisConfig.class, RedissonConfig.class})
public class EnableAutoRedisConfiguration {
}
