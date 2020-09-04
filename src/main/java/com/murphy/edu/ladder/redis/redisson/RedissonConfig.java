package com.murphy.edu.ladder.redis.redisson;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Dream
 * @ redisson配置注入
 * @date 2019年7月10日14:50:22
 */
@ConfigurationProperties(prefix = "ladder.redisson")
@Data
@Accessors(chain = true)
public class RedissonConfig {

    private RedissonConfig() {
    }

    private static class SingletonHolder {
        private final static RedissonConfig INSTANCE = new RedissonConfig();
    }

    public static RedissonConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /***单机模式: standalone 哨兵模式: sentinel 集群模式: cluster***/
    private String redissonMode;

    /***节点地址,host:port 多个用英文逗号分开***/
    private String redissonAddress;

    /***密码,用于节点身份验证的密码***/
    private String redissonPassword;

    /***#配置使用哪一个redis库 0 ~ 15***/
    private int redissonDatabase;

    /***连接池最大容量,连接池的连接数量自动弹性伸缩***/
    private int redissonConnectionPoolSize = 64;

    /***连接空闲超时时间,单位:毫秒,默认值10000,如果当前连接池里的连接数量超过了最小空闲连接数,而同时有连接空闲时间超过了该数值,那么这些连接将会自动被关闭,并从连接池里去掉***/
    private int redissonIdleConnectionTimeout = 10000;

    /***连接超时时间,单位:毫秒,默认值10000,同节点建立连接时的等待超时时间***/
    private int redissonConnectTimeout = 10000;

    /***命令等待超时,单位:毫秒,默认值3000,等待节点回复命令的时间,该时间从命令发送成功时开始计时***/
    private int redissonTimeout = 3000;

    /***主服务器的名称,主服务器的名称是哨兵进程中用来监测主从服务切换情况的***/
    private String redissonMasterName;

    /***主节点连接池大小,主节点的连接池最大容量。连接池的连接数量自动弹性伸缩,默认值64***/
    private int redissonMasterConnectionPoolSize = 64;

    /***从节点连接池大小,多从节点的环境里，每个从服务节点里用于普通操作(非发布和订阅)连接的连接池最大容量,连接池的连接数量自动弹性伸缩***/
    private int redissonSlaveConnectionPoolSize = 64;

    /***集群扫描间隔时间,对Redis集群节点状态扫描的时间间隔,单位是毫秒***/
    private int redissonScanInterval = 2000;

    /***编码,默认值: org.redisson.codec.FstCodec, Redisson的对象编码类是用于将对象进行序列化和反序列化,以实现对该对象在Redis里的读取和存储***/
    private String redissonCodec = "org.redisson.codec.FstCodec";

    /***如果尝试达到retryAttempts(命令失败重试次数)仍然不能将命令发送至某个指定的节点时,将抛出错误,如果尝试在此限制之内发送成功,则开始启用timeout(命令等待超时)计时***/
    private int redissonRetryAttempts = 3;

    /***在一条命令发送失败以后,等待重试发送的时间间隔,时间单位是毫秒,默认值1500***/
    private int redissonRetryInterval = 1500;

    /**
     * 注入配置完成之后,初始化jedis连接池,提前初始化,防止首次调用可能出现超时
     */
    public RedissonConfig init() {
        RedissonUtil.getString(RedissonUtil.REDISSON_INIT);
        return this;
    }
}
