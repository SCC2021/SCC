package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.MessageDAO;
import pt.unl.fct.scc.util.GsonMapper;

import java.util.LinkedList;
import java.util.List;

@Service
public class MessageService {
    private final CosmosContainer cosmosContainer;
    private final Gson gson;
    private final RedisCache redisCache;

    public MessageService(CosmosDBService cosmosDBService, RedisCache redisCache, GsonMapper gsonMapper){
        this.cosmosContainer = cosmosDBService.getContainer("Messages");
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
    }

    public CosmosItemResponse<MessageDAO> createMessage(MessageDAO message){
        redisCache.storeInCacheListLimited("recentMessages",gson.toJson(message),20);
        return cosmosContainer.createItem(message);
    }

    public List<MessageDAO> getMessages() {
        List<MessageDAO> list = redisCache.getListFromCache("recentMessages",MessageDAO.class);
        if (list != null){
            return list;
        }

        CosmosPagedIterable<MessageDAO> res = cosmosContainer.queryItems("SELECT * FROM Messages ", new CosmosQueryRequestOptions(), MessageDAO.class);
        List<MessageDAO> ret = new LinkedList<>();
        for (MessageDAO m : res){
            ret.add(m);
        }
        return ret;
    }

    public MessageDAO getMessageById( String id) {
        MessageDAO cache = redisCache.getMessageFromCacheList("recentMessages", id);
        if (cache != null){
            return cache;
        }

        CosmosPagedIterable<MessageDAO> res = cosmosContainer.queryItems("SELECT * FROM Messages WHERE Messages.id=\"" + id + "\"", new CosmosQueryRequestOptions(), MessageDAO.class);
        for (MessageDAO m : res){
            return m; // There should only be one message with the ID
        }
        return null;

    }

    public CosmosItemResponse<Object> delMessageById(String id) {
        redisCache.deleteMessageFromCacheList("recentMessages", id);

        PartitionKey key = new PartitionKey( id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }
}
