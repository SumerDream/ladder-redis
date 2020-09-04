package com.murphy.edu.ladder.redis.anno;

import com.murphy.edu.ladder.redis.RedisConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Dream
 * @version 1.0
 * @description 开启redis功能
 * @date 2020/8/1 4:59 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RedisConfig.class})
public @interface EnableLadderRedis {

}
