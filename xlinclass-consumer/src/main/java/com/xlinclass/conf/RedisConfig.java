package com.xlinclass.conf;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

@Configuration
public class RedisConfig {

    /**
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory
                = new JedisConnectionFactory();
        jedisConFactory.setHostName("192.168.1.130");
        jedisConFactory.setPort(6379);
        jedisConFactory.setPassword("27*EkVvM!2DVjB25SXIr");
        return jedisConFactory;
    }

    /**
     * jedis连接池配置,直接new 一个对象返回，使用默认值，如有需要可以为其各个属性配置值
     * @return
     */
    @Bean
    public JedisPoolConfig jedisCofig() {
        JedisPoolConfig jedisConfig = new JedisPoolConfig();
        jedisConfig.setMaxIdle(20);
        jedisConfig.setMinIdle(8);
        jedisConfig.setMaxTotal(500);
        jedisConfig.setMaxWaitMillis(6000);
        return jedisConfig;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}
