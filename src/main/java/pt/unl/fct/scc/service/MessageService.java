package pt.unl.fct.scc.service;

import com.google.gson.Gson;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.Message;
import pt.unl.fct.scc.repository.MessageRepo;
import pt.unl.fct.scc.util.GsonMapper;

import java.util.List;

@Service
public class MessageService {
    private final Gson gson;
    private final RedisCache redisCache;
    private final MongoTemplate mongoTemplate;
    private final MessageRepo messageRepo;


    public MessageService(RedisCache redisCache, GsonMapper gsonMapper, MongoTemplate mongoTemplate, MessageRepo messageRepo) {
        this.mongoTemplate = mongoTemplate;
        this.messageRepo = messageRepo;
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
    }

    public void createMessage(Message message) {
        messageRepo.save(message);
        redisCache.storeInCacheListLimited("recentMessages", gson.toJson(message), 20);
    }

    public List<Message> getMessages() {
        List<Message> list = redisCache.getListFromCache("recentMessages", Message.class);
        if (list != null) {
            return list;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(false));
        return mongoTemplate.find(query, Message.class);
    }

    public List<Channel> getTrendingChannels() {
        /*String last_15_minutes = String.valueOf(System.currentTimeMillis() - 15*60*1000);
        CosmosPagedIterable<TrendingDAO> res = cosmosContainer.queryItems(String.format("SELECT c.channelDest , count(c.channelDest) as messageCount FROM c WHERE c.sentAt > %s  GROUP BY c.channelDest", last_15_minutes), new CosmosQueryRequestOptions(), TrendingDAO.class);
        List<TrendingDAO> ret = new ArrayList<>();
        for (TrendingDAO t : res) {
            ret.add(t);
        }
        ret.sort(Comparator.comparing(TrendingDAO::getMessageCount).reversed());
        Iterator<TrendingDAO> it = ret.iterator();
        List<ChannelDAO> trending = new LinkedList<>();
        while(it.hasNext() && trending.size() < 4){
            TrendingDAO channel = it.next();
            ChannelDAO cache = redisCache.getChannelFromCacheList("recentChannels", channel.getChannelDest());
            if (cache != null && !cache.isPriv()) {
                trending.add(cache);
            }
        }*/

        return null;
    }

    public Message getMessageById(String id) {
        Message cache = redisCache.getMessageFromCacheList("recentMessages", id);
        if (cache != null)
            return cache;

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("messageID").is(id).and("deleted").is(false));
            return mongoTemplate.find(query, Message.class).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    public void delMessageById(String id) {
        Message m = this.getMessageById(id);
        if (m != null){
            m.setDeleted(true);
            this.updateMessage(m);
        }

        try {
            redisCache.deleteMessageFromCacheList("recentMessages", id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        /*
        Query query = new Query();
        query.addCriteria(Criteria.where("messageID").is(id));
        DeleteResult res = mongoTemplate.remove(query, Message.class);
        if (res.getDeletedCount() == 0) {
            return;
        }
         */
    }

    public void updateMessage(Message message){
        this.purgeMessageById(message.getMessageID());
        this.createMessage(message);
    }

    public List<Message> getDeleted() {
        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(true));
        return mongoTemplate.find(query, Message.class);
    }

    public void purgeMessages() {
        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(true));
        DeleteResult res = mongoTemplate.remove(query, Message.class);
        if (res.getDeletedCount() == 0) {
            return;
        }
    }

    private void purgeMessageById(String messageID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("channelID").is(messageID));
        DeleteResult res = mongoTemplate.remove(query, Message.class);
        if (res.getDeletedCount() == 0) {
            return;
        }
    }
}
