package com.chenxin.config.jedisConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisConfig {
    @Value("${jedisCluster.pool}")
    private String jedisCluster;

    private volatile Jedis jedis = null;

    @Bean("jedisTemplate")
    public Jedis getJedis() {
        if (jedis == null) {
            synchronized (JedisConfig.class) {
                if (jedis == null) {
                    jedis = new Jedis("127.0.0.1", 6379);
                }
            }
        }
        return jedis;
    }

//    @Bean
//    public JedisCluster jedisCluster() {
//        Set<HostAndPort> hostAndPorts = new HashSet<>();
//        String[] hostAndportsArr = this.jedisCluster.split("\\|\\|");
//        for (String hostAndPort : hostAndportsArr) {
//            String[] oneHostAndPort = hostAndPort.split(":");
//            HostAndPort hostAndPort1 = new HostAndPort(oneHostAndPort[0], Integer.parseInt(oneHostAndPort[1]));
//            hostAndPorts.add(hostAndPort1);
//        }
//        int timeout = 1000 * 10;
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(20);
//        poolConfig.setMaxIdle(10);
//        poolConfig.setMaxWaitMillis(1000 * 10);
//        poolConfig.setTestOnBorrow(false);
//        return new JedisCluster(hostAndPorts, timeout, poolConfig);
//    }
}
