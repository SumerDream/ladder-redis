package com.murphy.edu.ladder.redis.jedis;

/**
 * @author Dream
 * @ redis读取配置文件
 * @date 2019年7月5日13:10:41
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
    public static void setConfigLocation(String configLocation) {
        if (JedisConfigLocation.configLocation == null) {
            JedisConfigLocation.configLocation = configLocation;
        }
    }

}
