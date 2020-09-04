package com.murphy.edu.ladder.redis.redisson;


import com.murphy.edu.ladder.redis.RedisMode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;

import java.util.ResourceBundle;

/**
 * @author Dream
 * @ Redisson初始化中心
 * @date 2019年7月5日13:10:41
 */
@Slf4j
public class RedissonCenter {

    static RedisMode redisMode = null;

    static RedissonClient redissonClient = null;

    private static final String REDIS_PROTOCOL_PRE = "redis://";

    static {
        try {
            log.info("-----初始化redisson开始-----");
            if (RedissonConfig.getInstance().getRedissonMode() == null) {
                log.info("-----redisson读取配置文件开始-----");
                initConfig();
                log.info("-----redisson读取配置文件结束-----");
            }
            initPool();
            log.info("-----redisson连接池配置成功-----");
        } catch (Exception e) {
            log.error("-----初始化redisson配置失败-----");
            log.error(e.getMessage(), e);
        }
        log.info("-----初始化redisson结束-----");
    }


    /**
     * 加载配置文件
     *
     * @date 2019年7月5日13:10:41
     */
    private static void initConfig() {
        ResourceBundle resource;

        if (RedissonConfigLocation.getConfigLocation() == null) {
            log.info("-----redisson读取默认配置文件classpath:redisson.properties-----");
            resource = ResourceBundle.getBundle("redisson");
        } else {
            log.info("-----redisson读取指定配置文件" + RedissonConfigLocation.getConfigLocation() + ".properties -----");
            resource = ResourceBundle.getBundle(RedissonConfigLocation.getConfigLocation());
        }

        RedissonConfig redissonConfig = RedissonConfig.getInstance();

        redissonConfig.setRedissonMode(resource.getString("redisson.mode"));

        redissonConfig.setRedissonAddress(resource.getString("redisson.address"));

        redissonConfig.setRedissonPassword(resource.getString("redisson.password"));

        redissonConfig.setRedissonConnectionPoolSize(Integer.parseInt(resource.getString("redisson.connection.pool.size")));

        redissonConfig.setRedissonIdleConnectionTimeout(Integer.parseInt(resource.getString("redisson.idle.connection.timeout")));

        redissonConfig.setRedissonConnectTimeout(Integer.parseInt(resource.getString("redisson.connect.timeout")));

        redissonConfig.setRedissonTimeout(Integer.parseInt(resource.getString("redisson.timeout")));

        redissonConfig.setRedissonMasterName(resource.getString("redisson.master.name"));

        redissonConfig.setRedissonMasterConnectionPoolSize(Integer.parseInt(resource.getString("redisson.master.connection.pool.size")));

        redissonConfig.setRedissonSlaveConnectionPoolSize(Integer.parseInt(resource.getString("redisson.slave.connection.pool.size")));

        redissonConfig.setRedissonScanInterval(Integer.parseInt(resource.getString("redisson.scan.interval")));

        redissonConfig.setRedissonDatabase(Integer.parseInt(resource.getString("redisson.database")));

        redissonConfig.setRedissonCodec(resource.getString("redisson.codec"));

        redisMode = RedisMode.MODE_MAP.get(redissonConfig.getRedissonMode());
    }


    /**
     * 获取连接池信息
     *
     * @date 2019年7月5日13:10:41
     */
    private static void initPool() {

        RedissonConfig redissonConfig = RedissonConfig.getInstance();

        redisMode = RedisMode.MODE_MAP.get(redissonConfig.getRedissonMode());

        Config config = new Config();

        String[] addr = redissonConfig.getRedissonAddress().split(",");

        for (int i = 0; i < addr.length; i++) {
            addr[i] = REDIS_PROTOCOL_PRE + addr[i];
        }

        try {
            config.setCodec((Codec) Class.forName(redissonConfig.getRedissonCodec()).newInstance());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        switch (RedissonCenter.redisMode) {
            //单机模式
            case STANDALONE:
                SingleServerConfig singleServerConfig = config.useSingleServer()

                        .setAddress(addr[0])

                        .setConnectionPoolSize(redissonConfig.getRedissonConnectionPoolSize())

                        .setIdleConnectionTimeout(redissonConfig.getRedissonIdleConnectionTimeout())

                        .setConnectTimeout(redissonConfig.getRedissonConnectTimeout())

                        .setTimeout(redissonConfig.getRedissonTimeout())

                        .setDatabase(redissonConfig.getRedissonDatabase())

                        .setRetryAttempts(redissonConfig.getRedissonRetryAttempts())

                        .setRetryInterval(redissonConfig.getRedissonRetryInterval());
                if (redissonConfig.getRedissonPassword() != null && !"".equals(redissonConfig.getRedissonPassword())) {
                    singleServerConfig.setPassword(redissonConfig.getRedissonPassword());
                }

                redissonClient = Redisson.create(config);
                break;
            //哨兵模式
            case SENTINEL:
                SentinelServersConfig sentinelServersConfig = config.useSentinelServers()

                        .setMasterName(redissonConfig.getRedissonMasterName())

                        .addSentinelAddress(addr)

                        .setMasterConnectionPoolSize(redissonConfig.getRedissonMasterConnectionPoolSize())

                        .setSlaveConnectionPoolSize(redissonConfig.getRedissonSlaveConnectionPoolSize())

                        .setIdleConnectionTimeout(redissonConfig.getRedissonIdleConnectionTimeout())

                        .setTimeout(redissonConfig.getRedissonTimeout())

                        .setDatabase(redissonConfig.getRedissonDatabase())

                        .setRetryAttempts(redissonConfig.getRedissonRetryAttempts())

                        .setRetryInterval(redissonConfig.getRedissonRetryInterval());
                if (redissonConfig.getRedissonPassword() != null && !"".equals(redissonConfig.getRedissonPassword())) {
                    sentinelServersConfig.setPassword(redissonConfig.getRedissonPassword());
                }
                redissonClient = Redisson.create(config);
                break;
            //集群模式
            case CLUSTER:
                ClusterServersConfig clusterServersConfig = config.useClusterServers()

                        .setScanInterval(redissonConfig.getRedissonScanInterval())

                        .addNodeAddress(addr)

                        .setMasterConnectionPoolSize(redissonConfig.getRedissonMasterConnectionPoolSize())

                        .setSlaveConnectionPoolSize(redissonConfig.getRedissonSlaveConnectionPoolSize())

                        .setIdleConnectionTimeout(redissonConfig.getRedissonIdleConnectionTimeout())

                        .setConnectTimeout(redissonConfig.getRedissonTimeout())

                        .setPassword(redissonConfig.getRedissonPassword())

                        .setRetryAttempts(redissonConfig.getRedissonRetryAttempts())

                        .setRetryInterval(redissonConfig.getRedissonRetryInterval());
                if (redissonConfig.getRedissonPassword() != null && !"".equals(redissonConfig.getRedissonPassword())) {
                    clusterServersConfig.setPassword(redissonConfig.getRedissonPassword());
                }
                redissonClient = Redisson.create(config);
                break;
            default:
                log.error("redisson运行模式配置错误,redisMode : " + redisMode);
                break;
        }
    }
}
