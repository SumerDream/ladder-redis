# ladder-redis
轻量级redis实现, 只需一个注解即可使用redis.
## 背景
- 在日常开发过程中, 缓存是很常用的一个中间件, 有时候也是必不可少的一个工具, 其中redis较为常用, 在本人工作中, 缓存的选用, 也是redis居多(这里不对缓存相关内容进行展开).
- 通常做Java的小伙伴, 可能选用spring-redis居多, 本人在使用的过程中, 总觉得没有一款用着比较顺手舒服的redis操作工具, 有的是繁琐的配置, 还要考虑序列化反序列化, 单点集群配置差异较大等, 而操作的时候, api的使用也没有那么友好.
- 因此自己想着开发一个工具类, 想象使用仅仅需要引入一个pom依赖, 开启一个注解, 即可自动装配redis, api也都是静态方法调用, 包含绝大部分方法, 甚至分布式锁也能有效的处理和解决等.
## 实现功能
基于原生jedis 2.9.0以及原生redisson3.11.1封装, 实现了开发中常用的redis操作,以及分布式锁(基于lua脚本), redisson工具中还有GEO相关api.
## 使用
1. 下载项目到本地,可以选择安装到环境私服中.

2. 基于maven构建, 在pom文件中引入

       <dependency>
 
         <groupId>com.murphy.edu</groupId>  
    
         <artifactId>ladder-redis</artifactId>   
    
         <version>1.0.0.RELEASE</version>    
 
       </dependency>
       
  3. springboot项目看这里(主要为springboot项目设计,使用最简单)
       
       在启动类加入注解
       
       @EnableLadderRedis

4. spring项目看这里

5. 传统Java项目看这里
   
  
