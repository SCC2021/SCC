package pt.unl.fct.scc.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.Message;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.util.GsonMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@Service
@Scope("prototype")
public class RedisCache {

    private Logger logger = Logger.getLogger(this.getClass().toString());

    @Value("${azure.reddis.hostname}")
    private String redisHostName;

    @Value("${azure.reddis.redisKey}")
    private String redisKey;

    @Autowired
    GsonMapper gsonMapper;

    private Gson gson;

    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        System.out.println("STARING REDIS " + redisHostName + " " + redisKey);
        this.gson = gsonMapper.getGson();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(2048);
        jedisPoolConfig.setMaxIdle(200);
        jedisPoolConfig.setMinIdle(2);
        jedisPoolConfig.setNumTestsPerEvictionRun(2048);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(-1);
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(10000);
        jedisPoolConfig.setMaxWaitMillis(10000);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setJmxEnabled(true);
        jedisPoolConfig.setBlockWhenExhausted(true);

        this.jedisPool = new JedisPool(jedisPoolConfig, redisHostName, 6379, 900, redisKey);
    }

    public void storeInCache(String key, String value) {
        logger.info("Caching");
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.close();
    }

    public String getFromCache(String key) {
        logger.info("Geting From Cache");
        Jedis jedis = jedisPool.getResource();
        String ret = jedis.get(key);
        jedis.close();
        return ret;
    }

    public void storeInCacheListLimited(String list_key, String value, int limit) {
        logger.info("Caching in a list");
        Jedis jedis = jedisPool.getResource();
        Long cnt = jedis.lpush(list_key, value); //Stores in a set for channel
        if (cnt > limit + 1) {
            jedis.ltrim(list_key, 0, limit);
        }
        jedis.close();
    }

    public <T> List<T> getListFromCache(String list_key, Class<T> classOfT) {
        logger.info("Getting Cache List");
        Jedis jedis = jedisPool.getResource();
        List<String> list = jedis.lrange(list_key, 0, -1);
        List<T> ret = new LinkedList<>();
        if (list.size() > 0) {
            for (String s : list) {
                ret.add(gson.fromJson(s, classOfT));
            }
            jedis.close();
            return ret;
        }
        logger.warning("Did not find list in cache");
        jedis.close();
        return null;
    }

    public Message getMessageFromCacheList(String list_key, String id) {
        logger.info("Getting from Cache List");
        Jedis jedis = jedisPool.getResource();
        List<String> messages = jedis.lrange(list_key, 0, -1);
        if (messages.size() > 0) {
            for (String s : messages) {
                Message msg = gson.fromJson(s, Message.class);
                if (msg.getMessageID().equals(id)) {
                    jedis.close();
                    return msg;
                }
            }
        }
        logger.warning("Did not find in cache, looking on DB");
        jedis.close();
        return null;
    }

    public Channel getChannelFromCacheList(String list_key, String id) {
        logger.info("Getting from Cache List");
        Jedis jedis = jedisPool.getResource();
        List<String> messages = jedis.lrange(list_key, 0, -1);
        if (messages.size() > 0) {
            for (String s : messages) {
                Channel msg = gson.fromJson(s, Channel.class);
                if (msg.getChannelID().equals(id)) {
                    jedis.close();
                    return msg;
                }
            }
        }
        logger.warning("Did not find in cache, looking on DB");
        jedis.close();
        return null;
    }

    public User getUserFromCacheList(String list_key, String id) {
        logger.info("Getting from Cache List");
        Jedis jedis = jedisPool.getResource();
        List<String> messages = jedis.lrange(list_key, 0, -1);
        if (messages.size() > 0) {
            for (String s : messages) {
                User msg = gson.fromJson(s, User.class);
                if (msg.getUserID().equals(id)) {
                    jedis.close();
                    return msg;
                }
            }
        }
        logger.warning("Did not find in cache, looking on DB");
        jedis.close();
        return null;
    }

    public void deleteMessageFromCacheList(String list_key, String id) {
        logger.info("Deleting in Cache");
        Jedis jedis = jedisPool.getResource();
        List<String> messages = jedis.lrange(list_key, 0, -1);
        List<Message> list = new LinkedList<>();

        if (messages.size() > 0) {
            for (String s : messages) {
                Message msg = gson.fromJson(s, Message.class);
                if (!msg.getMessageID().equals(id)) {
                    list.add(msg);
                }
            }
        }

        jedis.del(list_key);

        for (Message m : list) {
            Long cnt = jedis.lpush(list_key, gson.toJson(m));
        }
        jedis.close();
    }

    public void deleteChannelFromCacheList(String list_key, String id) {
        logger.info("Deleting in Cache");
        Jedis jedis = jedisPool.getResource();
        List<String> messages = jedis.lrange(list_key, 0, -1);
        List<Channel> list = new LinkedList<>();

        if (messages.size() > 0) {
            for (String s : messages) {
                Channel msg = gson.fromJson(s, Channel.class);
                if (!msg.getChannelID().equals(id)) {
                    list.add(msg);
                }
            }
        }

        jedis.del(list_key);

        for (Channel m : list) {
            Long cnt = jedis.lpush(list_key, gson.toJson(m));
        }

        jedis.close();
    }

    public void deleteUserFromCacheList(String list_key, String id) {
        logger.info("Deleting in Cache");
        Jedis jedis = jedisPool.getResource();
        List<String> messages = jedis.lrange(list_key, 0, -1);
        List<User> list = new LinkedList<>();

        if (messages.size() > 0) {
            for (String s : messages) {
                User msg = gson.fromJson(s, User.class);
                if (!msg.getUserID().equals(id)) {
                    list.add(msg);
                }
            }
        }

        jedis.del(list_key);

        for (User m : list) {
            Long cnt = jedis.lpush(list_key, gson.toJson(m));
        }

        jedis.close();
    }


    public void deleteAll() {
        Jedis jedis = jedisPool.getResource();
        List<User> list = new LinkedList<>();
        List<Channel> list2 = new LinkedList<>();
        List<Message> list3 = new LinkedList<>();

        String s1 = "recentChannels";
        String s2 = "recentMessages";
        String s3 = "recentUsers";

        jedis.del(s1);
        jedis.del(s2);
        jedis.del(s3);

        jedis.close();
    }
}
