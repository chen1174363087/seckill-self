package com.chenxin.config.jedisConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import redis.clients.jedis.JedisPool;

/**
 * @Auther chenxin4
 * @Date 2020/7/10
 * Description 运用jedisPool解决单个jedis并发问题，一个线程打开socket，另一个在关闭，jedis则发生错误，
 * 但是多个jedis操作redis，jedis操作加锁也解决不了并发读写问题（超卖），所以还是使用mysql，查库存-创建订单-减库存需在一个事物中
 */
@Configuration
public class JedisPoolConfig {
    @Value("${redis.ip}")
    private String redisIp;

    @Value("${redis.port}")
    private int redisPort;

    @Bean
    @Scope("singleton")
    public JedisPool jedisPoolSeckill() {
        redis.clients.jedis.JedisPoolConfig config = new redis.clients.jedis.JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(2);
        config.setMinEvictableIdleTimeMillis(5 * 60 * 1000); //5分钟
        //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
        config.setSoftMinEvictableIdleTimeMillis(5 * 60 * 1000); //5分钟

        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(true);
        //在return一个jedis实例的时候，是否要进行验证操作，如果赋值为true，则返回jedis连接池的jedis实例肯定是可用的
        config.setTestOnReturn(true);

        //连接池耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时，会抛出超时异常，默认为true
        config.setBlockWhenExhausted(true);
        //这里超时时间是2s
        return new JedisPool(config, redisIp, redisPort, 2000);
    }
}
