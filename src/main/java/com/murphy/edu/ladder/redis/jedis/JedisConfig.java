package com.murphy.edu.ladder.redis.jedis;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Dream
 * @ jedis配置注入
 * @date 2019年7月5日13:10:41
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

    /***redis模式,单机模式: standalone 哨兵模式: sentinel 集群模式: cluster***/
    private String jedisMode = null;

    /***配置使用哪一个jedis库 0 ~ 15***/
    private int jedisDatabase = 0;

    /***连接池最大连接数量***/
    private int jedisPoolMaxTotal = 400;

    /***连接池最大空闲连接数量***/
    private int jedisPoolMaxIdle = 200;

    /***连接池最小空闲连接数量***/
    private int jedisPoolMinIdle = 50;

    /***获取连接池连接最长等待时间***/
    private int jedisPoolMaxWait = 2000;

    /***读写超时时间设置***/
    private int jedisSoTimeout = 2000;

    /***redis节点***/
    private String jedisNodes = null;

    /***redis名称***/
    private String jedisName = null;

    /***redis密码***/
    private String jedisPassword = null;

    /**
     * 注入配置完成之后,初始化jedis连接池,提前初始化,防止首次调用可能出现超时
     */
    public JedisConfig init() {
        JedisUtil.getString(JedisUtil.JEDIS_INIT);
        return this;
    }
}
