# ladder-redis
轻量级redis实现, 只需一个注解即可使用redis.
## 背景
- 在日常开发过程中, 缓存是很常用的一个中间件, 有时候也是必不可少的一个工具, 其中redis较为常用, 在本人工作中, 缓存的选用, 也是redis居多(这里不对缓存相关内容进行展开).
- 通常做Java的小伙伴, 可能选用spring-redis居多, 本人在使用的过程中, 总觉得没有一款用着比较顺手舒服的redis操作工具, 有的是繁琐的配置, 还要考虑序列化反序列化, 单点集群配置差异较大等, 而操作的时候, api的使用也没有那么友好.
- 因此自己想着开发一个工具类, 想象使用仅仅需要引入一个pom依赖, 开启一个注解, 即可自动装配redis, api也都是静态方法调用, 包含绝大部分方法, 甚至分布式锁也能有效的处理和解决等.
## 实现功能
基于原生jedis 2.9.0以及原生redisson3.11.1封装, 实现了开发中常用的redis操作,以及分布式锁(基于lua脚本), redisson工具中还有GEO相关api.
## 使用
1. 下载项目到本地, 可以选择安装到环境私服中.

2. 基于maven构建, 在pom文件中引入

       <dependency>
         <groupId>com.murphy.edu</groupId>  
         <artifactId>ladder-redis</artifactId>   
         <version>1.0.0.RELEASE</version>    
       </dependency>
       
3. spring项目看这里(主要为spring项目设计, 使用最简单)
       
         1) 在启动类加入注解
         @EnableLadderRedis
         
         2) 配置文件中配置redis账号密码等配置
         
         #单机模式: standalone 哨兵模式: sentinel 集群模式: cluster
         ladder.jedis.jedis-mode=standalone
         
         #节点,ip:port  多个请用逗号隔开
         ladder.jedis.jedis-nodes=127.0.0.1:6379
         
         #配置使用哪一个redis库 0 ~ 15
         ladder.jedis.jedis-database=0
         
         #密码
         ladder.jedis.jedis-password=

4. 普通Java项目看这里

       1) 创建redis.properties文件, 放在resources文件夹下
       
       2) 添加如下配置
       #单机模式: standalone 哨兵模式: sentinel 集群模式: cluster
       jedis.mode=standalone
       
       #节点,ip:port  多个请用逗号隔开
       jedis.nodes=127.0.0.1:6379
       
       #配置使用哪一个redis库 0 ~ 15
       jedis.database=0
       
       #密码
       jedis.password=
5. API使用
       
       所有操作都封装成了静态方法, 工具类方式调用
       
       jedis操作工具类: * com.murphy.edu.ladder.redis.jedis.JedisUtil.xxx(..);
       
       redisson操作工具类: * com.murphy.edu.ladder.redis.redisson.RedissonUtil.xxx(..);
       
## 配置详解(普通Java项目)
- jedis配置详解

       # jedis
       #单机模式: standalone 哨兵模式: sentinel 集群模式: cluster
       jedis.mode=standalone
       
       #节点,ip:port  多个请用逗号隔开
       jedis.nodes=127.0.0.1:6379
       
       #配置使用哪一个redis库 0 ~ 15
       jedis.database=0
       
       #密码
       jedis.password=
       
       #主master库名子
       jedis.name=master
       
       #pool可分配多少个jedis实例,通过pool.getResource()来获取,如果赋值为-1,则表示不限制
       jedis.pool.max.total=400
       
       #最大能够保持idel状态的对象数
       jedis.pool.max.idle=200
       
       #最小空闲链接数,创建连接池时建立的连接数量
       jedis.pool.min.idle=50
       
       #表示当borrow一个jedis实例时,最大的等待时间(毫秒),如果超过等待时间,则直接抛出JedisConnectionException
       jedis.pool.max.wait=3000
       
       #读取超时时间
       jedis.so.timeout=3000
       
- redisson配置详解

       # redisson
       #单机模式: standalone 哨兵模式: sentinel 集群模式: cluster
       redisson.mode=standalone
       
       #节点地址,host:port 多个用英文逗号分开
       redisson.address=127.0.0.1:6379
       
       #配置使用哪一个redis库 0 ~ 15 (仅单机和哨兵可用,集群不可用)
       redisson.database=0
       
       #密码,用于节点身份验证的密码
       redisson.password=
       
       #连接池最大容量,连接池的连接数量自动弹性伸缩
       redisson.connection.pool.size=64
       
       #连接空闲超时时间,单位:毫秒,默认值10000,如果当前连接池里的连接数量超过了最小空闲连接数,而同时有连接空闲时间超过了该数值,那么这些连接将会自动被关闭,并从连接池里去掉
       redisson.idle.connection.timeout=10000
       
       #连接超时时间,单位:毫秒,默认值10000,同节点建立连接时的等待超时时间
       redisson.connect.timeout=10000
       
       #命令等待超时,单位:毫秒,默认值3000,等待节点回复命令的时间,该时间从命令发送成功时开始计时
       redisson.timeout=3000
       
       #主服务器的名称,主服务器的名称是哨兵进程中用来监测主从服务切换情况的
       redisson.master.name=master
       
       #主节点连接池大小,主节点的连接池最大容量。连接池的连接数量自动弹性伸缩,默认值64
       redisson.master.connection.pool.size=64
       
       #从节点连接池大小,多从节点的环境里，每个从服务节点里用于普通操作(非发布和订阅)连接的连接池最大容量,连接池的连接数量自动弹性伸缩
       redisson.slave.connection.pool.size=64
       
       #集群扫描间隔时间,对Redis集群节点状态扫描的时间间隔,单位是毫秒
       redisson.scan.interval=2000
       
       #编码,默认值: org.redisson.codec.FstCodec, Redisson的对象编码类是用于将对象进行序列化和反序列化,以实现对该对象在Redis里的读取和存储
       redisson.codec=org.redisson.client.codec.StringCodec
       
       #如果尝试达到retryAttempts(命令失败重试次数)仍然不能将命令发送至某个指定的节点时,将抛出错误,如果尝试在此限制之内发送成功,则开始启用timeout(命令等待超时)计时
       redisson.retry.attempts=3
       
       #在一条命令发送失败以后,等待重试发送的时间间隔,时间单位是毫秒,默认值1500
       redisson.retry.interval=1500

## 配置详解(spring项目)
- jedis配置详解

       #单机模式: standalone 哨兵模式: sentinel 集群模式: cluster
       ladder.jedis.jedis-mode=standalone
       
       #节点,ip:port  多个请用逗号隔开
       ladder.jedis.jedis-nodes=127.0.0.1:6379
       
       #配置使用哪一个redis库 0 ~ 15
       ladder.jedis.jedis-database=0
       
       #密码
       ladder.jedis.jedis-password=
       
       #主master库名子
       ladder.jedis.jedis-name=master
       
       #pool可分配多少个jedis实例,通过pool.getResource()来获取,如果赋值为-1,则表示不限制
       ladder.jedis.jedis-pool-max-total=100
       
       #最大能够保持idel状态的对象数
       ladder.jedis.jedis-pool-max-idle=20
       
       #最小空闲链接数,创建连接池时建立的连接数量
       ladder.jedis.jedis-pool-min-idle=10
       
       #表示当borrow一个jedis实例时,最大的等待时间(毫秒),如果超过等待时间,则直接抛出JedisConnectionException
       ladder.jedis.jedis-pool-max-wait=3000
       
       #读取超时时间
       ladder.jedis.jedis-so-timeout=3000
       
- redisson配置详解

       #单机模式: standalone 哨兵模式: sentinel 集群模式: cluster
       ladder.redisson.mode=standalone
       
       #节点地址,host:port 多个用英文逗号分开
       ladder.redisson.address=127.0.0.1:6379
       
       #配置使用哪一个redis库 0 ~ 15 (仅单机和哨兵可用,集群不可用)
       ladder.redisson.database=0
       
       #密码,用于节点身份验证的密码
       ladder.redisson.password=
       
       #连接池最大容量,连接池的连接数量自动弹性伸缩
       ladder.redisson.connection.pool.size=64
       
       #连接空闲超时时间,单位:毫秒,默认值10000,如果当前连接池里的连接数量超过了最小空闲连接数,而同时有连接空闲时间超过了该数值,那么这些连接将会自动被关闭,并从连接池里去掉
       ladder.redisson.idle.connection.timeout=10000
       
       #连接超时时间,单位:毫秒,默认值10000,同节点建立连接时的等待超时时间
       ladder.redisson.connect.timeout=10000
       
       #命令等待超时,单位:毫秒,默认值3000,等待节点回复命令的时间,该时间从命令发送成功时开始计时
       ladder.redisson.timeout=3000
       
       #主服务器的名称,主服务器的名称是哨兵进程中用来监测主从服务切换情况的
       ladder.redisson.master.name=master
       
       #主节点连接池大小,主节点的连接池最大容量。连接池的连接数量自动弹性伸缩,默认值64
       ladder.redisson.master.connection.pool.size=64
       
       #从节点连接池大小,多从节点的环境里，每个从服务节点里用于普通操作(非发布和订阅)连接的连接池最大容量,连接池的连接数量自动弹性伸缩
       ladder.redisson.slave.connection.pool.size=64
       
       #集群扫描间隔时间,对Redis集群节点状态扫描的时间间隔,单位是毫秒
       ladder.redisson.scan.interval=2000
       
       #编码,默认值: org.redisson.codec.FstCodec, Redisson的对象编码类是用于将对象进行序列化和反序列化,以实现对该对象在Redis里的读取和存储
       ladder.redisson.codec=org.redisson.client.codec.StringCodec
       
       #如果尝试达到retryAttempts(命令失败重试次数)仍然不能将命令发送至某个指定的节点时,将抛出错误,如果尝试在此限制之内发送成功,则开始启用timeout(命令等待超时)计时
       ladder.redisson.retry.attempts=3
       
       #在一条命令发送失败以后,等待重试发送的时间间隔,时间单位是毫秒,默认值1500
       ladder.redisson.retry.interval=1500
       
## 说明
       - 普通Java项目配置和spring项目配置其实是一样的,不同的就是spring项目配置有统一的前缀  ladder. 其他并无区别.
       - 有些配置给了默认值, 你可以使用默认值或者你也可以根据实际情况配置合适的参数.
## spring项目最佳实践

- 基于maven构建, pom文件添加依赖

![依赖](https://github.com/SumerDream/resources/blob/master/%E4%BE%9D%E8%B5%96%E5%AF%BC%E5%85%A5.jpg)

- 启动类添加注解

![注解](https://github.com/SumerDream/resources/blob/master/%E5%90%AF%E5%8A%A8%E7%B1%BB%E5%A2%9E%E5%8A%A0%E6%B3%A8%E8%A7%A3.jpg)

- bootstrap增加配置文件

![配置](https://github.com/SumerDream/resources/blob/master/%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6.jpg)

- 运行

![运行](https://github.com/SumerDream/resources/blob/master/%E8%BF%90%E8%A1%8C.jpg)
