package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.ChannelDAO;
import pt.unl.fct.scc.model.MessageDAO;
import pt.unl.fct.scc.model.TrendingDAO;
import pt.unl.fct.scc.util.GsonMapper;

import java.util.*;

@Service
public class MessageService {
    private final CosmosContainer cosmosContainer;
    private final Gson gson;
    private final RedisCache redisCache;

    public MessageService(CosmosDBService cosmosDBService, RedisCache redisCache, GsonMapper gsonMapper) {
        this.cosmosContainer = cosmosDBService.getContainer("Messages");
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
    }

    public CosmosItemResponse<MessageDAO> createMessage(MessageDAO message) {
        System.out.println(message);
        redisCache.storeInCacheListLimited("recentMessages", gson.toJson(message), 20);
        return cosmosContainer.createItem(message);
    }

    public List<MessageDAO> getMessages() {
        List<MessageDAO> list = redisCache.getListFromCache("recentMessages", MessageDAO.class);
        if (list != null) {
            return list;
        }

        CosmosPagedIterable<MessageDAO> res = cosmosContainer.queryItems("SELECT * FROM Messages ", new CosmosQueryRequestOptions(), MessageDAO.class);
        List<MessageDAO> ret = new LinkedList<>();
        for (MessageDAO m : res) {
            ret.add(m);
        }
        return ret;
    }

    public List<ChannelDAO> getTrendingChannels() {
        String last_15_minutes = String.valueOf(System.currentTimeMillis() - 15*60*1000);
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
            if (cache != null) {
                trending.add(cache);
            }
        }
        return trending;
    }

    public MessageDAO getMessageById(String id) {
        MessageDAO cache = redisCache.getMessageFromCacheList("recentMessages", id);
        if (cache != null) {
            return cache;
        }

        CosmosPagedIterable<MessageDAO> res = cosmosContainer.queryItems("SELECT * FROM Messages WHERE Messages.id=\"" + id + "\"", new CosmosQueryRequestOptions(), MessageDAO.class);
        for (MessageDAO m : res) {
            return m; // There should only be one message with the ID
        }
        return null;

    }

    public CosmosItemResponse<Object> delMessageById(String id) {
        redisCache.deleteMessageFromCacheList("recentMessages", id);

        PartitionKey key = new PartitionKey(id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }
}
