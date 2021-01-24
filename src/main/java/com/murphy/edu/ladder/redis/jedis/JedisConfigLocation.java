package com.murphy.edu.ladder.redis.jedis;

/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * redis config location
 */
public class JedisConfigLocation {

    private static String configLocation = null;

    public static String getConfigLocation() {
        return configLocation;
    }


    /**
     * 设置配置文件位置,只允许设置一次
     *
     * @param configLocation configLocation
     */
    public static synchronized void setConfigLocation(String configLocation) {
        if (JedisConfigLocation.configLocation == null) {
            JedisConfigLocation.configLocation = configLocation;
        }
    }

}
