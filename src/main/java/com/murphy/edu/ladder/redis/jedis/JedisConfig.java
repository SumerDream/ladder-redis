package com.murphy.edu.ladder.redis.jedis;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * redis config
 */
@ConfigurationProperties(prefix = "ladder.jedis")
@Data
@Accessors(chain = true)
public class JedisConfig {

    private JedisConfig() {
    }

    private static class SingletonHolder {
        private final static JedisConfig INSTANCE = new JedisConfig();
    }

    public static JedisConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /***redis节点,例如 127.0.0.1:6379,默认端口6379***/
    private String jedisNodes = null;

    /***redis模式,单机模式: standalone 哨兵模式: sentinel 集群模式: cluster***/
    private String jedisMode = null;

    /***redis密码***/
    private String jedisPassword = null;

    /***redis名称***/
    private String jedisName = null;

    /***配置使用哪一个jedis库 0 ~ 15***/
    private int jedisDatabase = 0;

    /***连接池最大连接数量***/
    private int jedisPoolMaxTotal = 200;

    /***连接池最大空闲连接数量***/
    private int jedisPoolMaxIdle = 50;

    /***连接池最小空闲连接数量***/
    private int jedisPoolMinIdle = 20;

    /***获取连接池连接最长等待时间***/
    private int jedisPoolMaxWait = 2000;

    /***读写超时时间设置***/
    private int jedisSoTimeout = 2000;

    /***全局异常处理方式 catch 或者 throw 默认为抛出(使用此框架期间,尽可能的把所有异常处理方式以用户配置的方式进行处理),但不保证任何异常(比如项目启动阶段,则抛出异常处理)***/
    private String exceptionSolution = "throw";

    /**
     * 注入配置完成之后,初始化jedis连接池,提前初始化,防止首次调用可能出现超时
     */
    public JedisConfig init() {
        JedisUtil.getString(JedisUtil.JEDIS_INIT);
        return this;
    }
}
