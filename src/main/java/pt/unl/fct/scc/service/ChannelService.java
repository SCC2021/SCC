package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.DeletedDAO;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.repository.ChannelRepo;
import pt.unl.fct.scc.util.GsonMapper;

import java.util.LinkedList;
import java.util.List;

@Service
public class ChannelService {

    private final RedisCache redisCache;
    private final Gson gson;
    private final String CACHE_LIST = "recentChannels";
    private final MongoTemplate mongoTemplate;
    private final ChannelRepo channelRepo;


    public ChannelService(RedisCache redisCache, GsonMapper gsonMapper, MongoTemplate mongoTemplate, ChannelRepo channelRepo) {
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
        this.mongoTemplate = mongoTemplate;
        this.channelRepo = channelRepo;
    }

    public void createChannel(Channel channel) {
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(channel), 20);
        channelRepo.save(channel);
    }

    public List<Channel> getChannels() {
        List<Channel> list = redisCache.getListFromCache(CACHE_LIST, Channel.class);
        if (list != null) {
            return list;
        }

        list = channelRepo.findAll();

        return list;
    }

    public Channel getChannelById(String id) {
        Channel cache = redisCache.getChannelFromCacheList(CACHE_LIST, id);
        if (cache != null) {
            return cache;
        }

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("channelID").is(id));
            return mongoTemplate.find(query, Channel.class).get(0);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public void delChannelById(String id) {
        redisCache.deleteChannelFromCacheList(CACHE_LIST, id);
        Query query = new Query();
        query.addCriteria(Criteria.where("channelID").is(id));
        DeleteResult res = mongoTemplate.remove(query, Channel.class);
        if (res.getDeletedCount() == 0){
            return;
        }
    }

    public boolean addUser(String channelId, String user, boolean isSubscribe) {
        Channel ch = this.getChannelById(channelId);
        if (ch == null) return false;
        if (ch.isPriv() && isSubscribe) return false;

        List<String> members = ch.getMembers();
        members.add(user);
        ch.setMembers(members);

        this.updateChannel(ch);

        redisCache.deleteUserFromCacheList(CACHE_LIST, user);
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(ch), 20);


        return true;
    }

    public void updateChannel(Channel ch){
        this.delChannelById(ch.getChannelID());
        this.createChannel(ch);

        redisCache.deleteUserFromCacheList(CACHE_LIST, ch.getChannelID());
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(ch), 20);
    }
}
