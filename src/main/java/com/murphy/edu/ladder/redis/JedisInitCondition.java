package com.murphy.edu.ladder.redis;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * init condition
 */
public class JedisInitCondition implements Condition {

    private static final String JEDIS_MODE = "ladder.jedis.jedis-mode";

    private static final String JEDIS_NODES = "ladder.jedis.jedis-nodes";

    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return StringUtils.hasText(context.getEnvironment().getProperty(JEDIS_MODE)) && StringUtils.hasText(context.getEnvironment().getProperty(JEDIS_NODES));
    }
}
