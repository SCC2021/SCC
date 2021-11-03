package pt.unl.fct.scc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

@Service
public class RedisCache {

    @Value("${azure.reddis.hostname}")
    private String redisHostName;

    @Value("${azure.reddis.redisKey}")
    private String redisKey;

    private JedisPool jedisPool;

    @PostConstruct
    public void init(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        this.jedisPool = new JedisPool(poolConfig,redisHostName,6380,1000,redisKey, true);
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

}
