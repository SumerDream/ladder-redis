# ladder-redis
轻量级redis实现, 只需一个注解即可使用redis.
## 2021年01月24日16:50:11升级2.0.0版本,主要变化:@注册jvm钩子,允许jvm销毁时候释放资源池&&增加用户选择异常处理方式&&增加字节类型操作&&增加gzip压缩
## 去除(不再维护)redisson相关,继续维护和优化jedis相关代码.
## 背景
- 在日常开发过程中, 缓存是很常用的一个中间件, 有时候也是必不可少的一个工具, 其中redis较为常用, 在本人工作中, 缓存的选用, 也是redis居多(这里不对缓存相关内容进行展开).
- 通常做Java的小伙伴, 可能选用spring-redis居多, 本人在使用的过程中, 总觉得没有一款用着比较顺手舒服的redis操作工具, 有的是繁琐的配置, 还要考虑序列化反序列化, 单点集群配置差异较大等, 而操作的时候, api的使用也没有那么友好.
- 因此自己想着开发一个工具类, 想象使用仅仅需要引入一个pom依赖, 开启一个注解, 即可自动装配redis, api也都是静态方法调用, 包含绝大部分方法, 甚至分布式锁也能有效的处理和解决等.
## 实现功能
基于原生jedis2.9.0封装,实现了开发中常用的redis操作,以及分布式锁(基于lua脚本)
## 使用
1. 下载项目到本地, 可以选择安装到环境私服中.

2. 基于maven构建, 在pom文件中引入

       <dependency>
         <groupId>com.murphy.edu</groupId>  
         <artifactId>ladder-redis</artifactId>   
         <version>2.0.0.RELEASE</version>    
       </dependency>
       
3. spring项目看这里(主要为spring项目设计)
       
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

![运行](https://github.com/SumerDream/resources/blob/master/%E8%BF%90%E8%A1%8C.png)

## 普通Java项目最佳实践

- 添加属性文件

![运行](https://github.com/SumerDream/resources/blob/master/%E5%B1%9E%E6%80%A7%E6%96%87%E4%BB%B6.jpg)

- 运行

![运行](https://github.com/SumerDream/resources/blob/master/%E6%99%AE%E9%80%9A%E9%A1%B9%E7%9B%AE%E8%BF%90%E8%A1%8C.png)
