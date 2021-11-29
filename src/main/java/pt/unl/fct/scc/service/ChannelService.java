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
import pt.unl.fct.scc.util.GsonMapper;

import java.util.LinkedList;
import java.util.List;

@Service
public class ChannelService {

    private final CosmosContainer cosmosContainer;
    private final RedisCache redisCache;
    private final Gson gson;
    private final String CACHE_LIST = "recentChannels";


    public ChannelService(CosmosDBService cosmosDBService, RedisCache redisCache, GsonMapper gsonMapper) {
        this.cosmosContainer = cosmosDBService.getContainer("Channels");
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
    }

    public CosmosItemResponse<ChannelDAO> createChannel(ChannelDAO channelDAO) {
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(channelDAO), 20);
        return cosmosContainer.createItem(channelDAO);
    }

    public List<ChannelDAO> getChannels() {
        List<ChannelDAO> list = redisCache.getListFromCache(CACHE_LIST, ChannelDAO.class);
        if (list != null) {
            return list;
        }

        CosmosPagedIterable<ChannelDAO> res = cosmosContainer.queryItems("SELECT * FROM Channels", new CosmosQueryRequestOptions(), ChannelDAO.class);
        List<ChannelDAO> ret = new LinkedList<>();
        for (ChannelDAO m : res) {
            ret.add(m);
        }
        return ret;
    }

    public ChannelDAO getChannelById(String id) {
        ChannelDAO cache = redisCache.getChannelFromCacheList(CACHE_LIST, id);
        if (cache != null) {
            return cache;
        }

        CosmosPagedIterable<ChannelDAO> res = cosmosContainer.queryItems("SELECT * FROM Channels WHERE Channels.id=\"" + id + "\"", new CosmosQueryRequestOptions(), ChannelDAO.class);
        for (ChannelDAO m : res) {
            return m; // There should only be one message with the ID
        }
        return null;
    }

    public CosmosItemResponse<Object> delChannelById(String id) {
        redisCache.deleteChannelFromCacheList(CACHE_LIST, id);

        PartitionKey key = new PartitionKey(id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }

}
