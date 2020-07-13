package com.chenxin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @Auther chenxin4
 * @Date 2020/7/10
 * Description
 */
@Component
@Scope("singleton")
public class RedisUtils {
    @Autowired
    @Qualifier("jedisPoolSeckill")
    private redis.clients.jedis.JedisPool pool;

    private Jedis getJedis() {
        System.out.println("jedis pool == " + pool);
        return pool.getResource();
    }

    public void set(String key, String value) {
        Jedis jedis = this.getJedis();
        jedis.set(key, value);

    }

    public String get(String key) {
        Jedis jedis = this.getJedis();
        return jedis.get(key);
    }

    public void sadd(String key, String... members) {
        Jedis jedis = this.getJedis();
        jedis.sadd(key, members);
    }

    public Long srem(String key, String... members) {
        Jedis jedis = this.getJedis();
        return jedis.srem(key, members);
    }

    public Set<String> smembers(String key, String... members) {
        Jedis jedis = this.getJedis();
        return jedis.smembers(key);
    }
}
