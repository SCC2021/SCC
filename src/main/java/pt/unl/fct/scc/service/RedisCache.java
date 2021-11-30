package pt.unl.fct.scc.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.ChannelDAO;
import pt.unl.fct.scc.model.MessageDAO;
import pt.unl.fct.scc.model.UserDAO;
import pt.unl.fct.scc.util.GsonMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisShardInfo;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@Service
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

    Jedis jedis;

    @PostConstruct
    public void init() {
        this.gson = gsonMapper.getGson();
        // Connect to the Azure Cache for Redis over the TLS/SSL port using the key.
        JedisShardInfo shardInfo = new JedisShardInfo(redisHostName, 6380, true);
        shardInfo.setPassword(redisKey); /* Use your access key. */
        this.jedis = new Jedis(shardInfo);
    }

    public void storeInCache(String key, String value) {
        logger.info("Caching");
        jedis.set(key, value);
    }

    public String getFromCache(String key) {
        logger.info("Geting From Cache");
        return jedis.get(key);
    }

    public void storeInCacheListLimited(String list_key, String value, int limit) {
        logger.info("Caching in a list");
        Long cnt = jedis.lpush(list_key, value); //Stores in a set for channel
        if (cnt > limit + 1) {
            jedis.ltrim(list_key, 0, limit);
        }
    }

    public <T> List<T> getListFromCache(String list_key, Class<T> classOfT) {
        logger.info("Getting Cache List");
        List<String> list = jedis.lrange(list_key, 0, -1);
        List<T> ret = new LinkedList<>();
        if (list.size() > 0) {
            for (String s : list) {
                ret.add(gson.fromJson(s, classOfT));
            }
            return ret;
        }
        logger.warning("Did not find list in cache");
        return null;
    }

    public MessageDAO getMessageFromCacheList(String list_key, String id) {
        logger.info("Getting from Cache List");
        List<String> messages = jedis.lrange(list_key, 0, -1);
        if (messages.size() > 0) {
            for (String s : messages) {
                MessageDAO msg = gson.fromJson(s, MessageDAO.class);
                if (msg.getId().equals(id)) {
                    return msg;
                }
            }
        }
        logger.warning("Did not find in cache, looking on DB");
        return null;
    }

    public ChannelDAO getChannelFromCacheList(String list_key, String id) {
        logger.info("Getting from Cache List");
        List<String> messages = jedis.lrange(list_key, 0, -1);
        if (messages.size() > 0) {
            for (String s : messages) {
                ChannelDAO msg = gson.fromJson(s, ChannelDAO.class);
                if (msg.getId().equals(id)) {
                    return msg;
                }
            }
        }
        logger.warning("Did not find in cache, looking on DB");
        return null;
    }

    public UserDAO getUserFromCacheList(String list_key, String id) {
        logger.info("Getting from Cache List");
        List<String> messages = jedis.lrange(list_key, 0, -1);
        if (messages.size() > 0) {
            for (String s : messages) {
                UserDAO msg = gson.fromJson(s, UserDAO.class);
                if (msg.getId().equals(id)) {
                    return msg;
                }
            }
        }
        logger.warning("Did not find in cache, looking on DB");
        return null;
    }

    public void deleteMessageFromCacheList(String list_key, String id) {
        logger.info("Deleting in Cache");
        List<String> messages = jedis.lrange(list_key, 0, -1);
        List<MessageDAO> list = new LinkedList<>();

        if (messages.size() > 0) {
            for (String s : messages) {
                MessageDAO msg = gson.fromJson(s, MessageDAO.class);
                if (!msg.getId().equals(id)) {
                    list.add(msg);
                }
            }
        }

        jedis.del(list_key);

        for (MessageDAO m : list) {
            Long cnt = jedis.lpush(list_key, gson.toJson(m));
        }
    }

    public void deleteChannelFromCacheList(String list_key, String id) {
        logger.info("Deleting in Cache");
        List<String> messages = jedis.lrange(list_key, 0, -1);
        List<ChannelDAO> list = new LinkedList<>();

        if (messages.size() > 0) {
            for (String s : messages) {
                ChannelDAO msg = gson.fromJson(s, ChannelDAO.class);
                if (!msg.getId().equals(id)) {
                    list.add(msg);
                }
            }
        }

        jedis.del(list_key);

        for (ChannelDAO m : list) {
            Long cnt = jedis.lpush(list_key, gson.toJson(m));
        }
    }

    public void deleteUserFromCacheList(String list_key, String id) {
        logger.info("Deleting in Cache");
        List<String> messages = jedis.lrange(list_key, 0, -1);
        List<UserDAO> list = new LinkedList<>();

        if (messages.size() > 0) {
            for (String s : messages) {
                UserDAO msg = gson.fromJson(s, UserDAO.class);
                if (!msg.getId().equals(id)) {
                    list.add(msg);
                }
            }
        }

        jedis.del(list_key);

        for (UserDAO m : list) {
            Long cnt = jedis.lpush(list_key, gson.toJson(m));
        }
    }



}
