package com.murphy.edu.ladder.redis.jedis;


import com.murphy.edu.ladder.redis.RedisMode;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import java.util.*;

/**
 * @author Dream
 * @ jedis初始化中心
 * @date 2019年7月5日13:10:41
 */
@Slf4j
public class JedisCenter {

    static RedisMode redisMode = null;

    private static Pool<Jedis> pool = null;

    static JedisCluster jedisCluster = null;

    static {
        try {
            log.info("-----初始化jedis开始-----");
            if (JedisConfig.getInstance().getJedisMode() == null) {
                log.info("-----jedis读取配置文件开始-----");
                initConfig();
                log.info("-----jedis读取配置文件结束-----");
            }
            intPool();
            log.info("-----jedis连接池配置成功-----");
        } catch (Exception e) {
            log.error("-----初始化jedis配置失败-----");
            log.error(e.getMessage(), e);
        }
        log.info("-----初始化jedis结束-----");
    }


    /**
     * 加载配置文件
     *
     * @date 2019年7月5日13:10:41
     */
    private static void initConfig() {
        ResourceBundle resource;
        if (JedisConfigLocation.getConfigLocation() == null) {
            log.info("-----jedis读取默认配置文件classpath:jedis.properties-----");
            resource = ResourceBundle.getBundle("jedis");
        } else {
            log.info("-----jedis读取指定配置文件" + JedisConfigLocation.getConfigLocation() + ".properties -----");
            resource = ResourceBundle.getBundle(JedisConfigLocation.getConfigLocation());
        }

        JedisConfig jedisConfig = JedisConfig.getInstance();

        jedisConfig.setJedisMode(resource.getString("jedis.mode"));

        redisMode = RedisMode.MODE_MAP.get(jedisConfig.getJedisMode());

        jedisConfig.setJedisPoolMaxTotal(Integer.parseInt(resource.getString("jedis.pool.max.total")));

        jedisConfig.setJedisPoolMaxIdle(Integer.parseInt(resource.getString("jedis.pool.max.idle")));

        jedisConfig.setJedisPoolMinIdle(Integer.parseInt(resource.getString("jedis.pool.min.idle")));

        jedisConfig.setJedisPoolMaxWait(Integer.parseInt(resource.getString("jedis.pool.max.wait")));

        jedisConfig.setJedisSoTimeout(Integer.parseInt(resource.getString("jedis.so.timeout")));

        jedisConfig.setJedisName(resource.getString("jedis.name"));

        jedisConfig.setJedisNodes(resource.getString("jedis.nodes"));

        jedisConfig.setJedisPassword(resource.getString("jedis.password"));

        jedisConfig.setJedisDatabase(Integer.parseInt(resource.getString("jedis.database")));
    }


    /**
     * 获取连接池信息
     *
     * @date 2019年7月5日13:10:41
     */
    private static void intPool() {

        JedisConfig jedisConfig = JedisConfig.getInstance();

        redisMode = RedisMode.MODE_MAP.get(jedisConfig.getJedisMode());

        //连接池配置
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(jedisConfig.getJedisPoolMaxTotal());

        config.setMaxIdle(jedisConfig.getJedisPoolMaxIdle());

        config.setMinIdle(jedisConfig.getJedisPoolMinIdle());

        config.setMaxWaitMillis(jedisConfig.getJedisPoolMaxWait());

        config.setTestOnBorrow(true);

        config.setTestOnReturn(true);
        //判断是否需要密码
        boolean nullPwd = jedisConfig.getJedisPassword() == null || "".equals(jedisConfig.getJedisPassword());
        int redisDatabase = (jedisConfig.getJedisDatabase() > 15 || jedisConfig.getJedisDatabase() < 0) ? 0 : jedisConfig.getJedisDatabase();
        switch (JedisCenter.redisMode) {
            //单机模式
            case STANDALONE:
                String[] hostAndPort = HostAndPort.extractParts(jedisConfig.getJedisNodes());
                pool = new JedisPool(config, hostAndPort[0], Integer.parseInt(hostAndPort[1]), jedisConfig.getJedisSoTimeout(), nullPwd ? null : jedisConfig.getJedisPassword(), redisDatabase, false);
                break;
            //哨兵模式
            case SENTINEL:
                Set<String> sentinels = new HashSet<>(Arrays.asList(jedisConfig.getJedisNodes().split(",")));
                pool = new JedisSentinelPool(jedisConfig.getJedisName(), sentinels, config, jedisConfig.getJedisSoTimeout(), nullPwd ? null : jedisConfig.getJedisPassword(), redisDatabase);
                break;
            //集群模式
            case CLUSTER:
                Set<HostAndPort> hostAndPorts = new LinkedHashSet<>();
                for (String node : jedisConfig.getJedisNodes().split(",")) {
                    hostAndPorts.add(HostAndPort.parseString(node));
                }
                if (nullPwd) {
                    jedisCluster = new JedisCluster(hostAndPorts, jedisConfig.getJedisPoolMaxWait(), jedisConfig.getJedisSoTimeout(), 1, config);
                } else {
                    jedisCluster = new JedisCluster(hostAndPorts, jedisConfig.getJedisPoolMaxWait(), jedisConfig.getJedisSoTimeout(), 1, jedisConfig.getJedisPassword(), config);
                }
                break;
            default:
                log.error("jedis运行模式配置错误,redisMode : " + redisMode);
                break;
        }
    }

    /**
     * 获取Jedis实例操作
     *
     * @return Jedis
     * @date 2019年7月5日13:10:41
     */
    public static Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return jedis;
    }


    /**
     * 销毁jedis实例
     *
     * @param jedis jedis
     * @author Dream
     * @date 2019年7月5日13:10:41
     */
    public static void closeJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
