package com.murphy.edu.ladder.redis.redisson;

/**
 * @author Dream
 * @ redisson读取配置文件
 * @date 2019年7月5日13:10:41
 */
public class RedissonConfigLocation {

    private static String configLocation = null;

    public static String getConfigLocation() {
        return configLocation;
    }


    /**
     * 设置配置文件位置,只允许设置一次
     *
     * @param configLocation configLocation
     */
    public static void setConfigLocation(String configLocation) {

        if (RedissonConfigLocation.configLocation == null) {

            RedissonConfigLocation.configLocation = configLocation;
        }
    }

}
