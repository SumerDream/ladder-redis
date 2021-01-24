package com.murphy.edu.ladder.redis;

import java.util.HashMap;

/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * run mode
 */
public enum RedisMode {

    /******/
    STANDALONE("standalone", "单机模式"),
    /******/
    SENTINEL("sentinel", "哨兵模式"),
    /******/
    CLUSTER("cluster", "集群模式");

    private final String mode;
    private final String desc;

    public String getMode() {
        return mode;
    }

    public String getDesc() {
        return desc;
    }

    RedisMode(String mode, String desc) {
        this.mode = mode;
        this.desc = desc;
    }

    public static final HashMap<String, RedisMode> MODE_MAP = new HashMap<>();

    static {
        MODE_MAP.put(STANDALONE.mode, RedisMode.STANDALONE);
        MODE_MAP.put(SENTINEL.mode, RedisMode.SENTINEL);
        MODE_MAP.put(CLUSTER.mode, RedisMode.CLUSTER);
    }
}
