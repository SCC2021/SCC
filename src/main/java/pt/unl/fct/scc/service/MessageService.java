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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.LinkedList;
import java.util.List;

@Service
public class MessageService {
    private final CosmosContainer cosmosContainer;
    private final Jedis jedis;
    private final Gson gson;

    public MessageService(CosmosDBService cosmosDBService, RedisCache redisCache, GsonMapper gsonMapper){
        this.cosmosContainer = cosmosDBService.getContainer("Messages");
        this.jedis = redisCache.getJedisPool().getResource();
        this.gson = gsonMapper.getGson();
    }

    public CosmosItemResponse<MessageDAO> createMessage(MessageDAO message){
        jedis.set("msg:" + message.getId(),gson.toJson(message)); //Just stores
        Long cnt = jedis.lpush("messages:"+message.getChannelDest(),gson.toJson(message)); //Stores in a set for channel
        Long cnt2 = jedis.lpush("recentMessages",gson.toJson(message)); //Stores in a set for channel
        if (cnt > 21){
            jedis.ltrim("messages:"+message.getChannelDest(),0,20);
        }
        if (cnt2 > 21){
            jedis.ltrim("recentMessages",0,20);
        }
        return cosmosContainer.createItem(message);
    }

    public List<MessageDAO> getMessages() {
        List<MessageDAO> ret = new LinkedList<>();
        List<String> messages = jedis.lrange("recentMessages",0,-1);
        if (messages.size() > 0){
            for (String s: messages) {
                ret.add(gson.fromJson(s,MessageDAO.class));
            }
            return ret;
        }
        CosmosPagedIterable<MessageDAO> res = cosmosContainer.queryItems("SELECT * FROM Messages ", new CosmosQueryRequestOptions(), MessageDAO.class);

        for (MessageDAO m : res){
            ret.add(m);
        }
        return ret;
    }

    public MessageDAO getMessageById( String id) {
        if (jedis.exists("msg:"+id)){
            return gson.fromJson(jedis.get("msg:"+id),MessageDAO.class);
        }
        CosmosPagedIterable<MessageDAO> res = cosmosContainer.queryItems("SELECT * FROM Messages WHERE Messages.id=\"" + id + "\"", new CosmosQueryRequestOptions(), MessageDAO.class);
        for (MessageDAO m : res){
            return m; // There should only be one message with the ID
        }
        return null;

    }

    public CosmosItemResponse<Object> delMessageById(String id) {
        MessageDAO m = gson.fromJson(jedis.get("msg:"+id),MessageDAO.class);
        jedis.del("msg:"+id);
       
        PartitionKey key = new PartitionKey( id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }
}
