package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.ChannelDAO;
import pt.unl.fct.scc.util.GsonMapper;

import java.util.LinkedList;
import java.util.List;

@Service
public class ChannelService {

    private final CosmosContainer channelsCosmosContainer;
    private final CosmosContainer deletedChannelsCosmosContainer;
    private final RedisCache redisCache;
    private final Gson gson;
    private final String CACHE_LIST = "recentChannels";


    public ChannelService(CosmosDBService cosmosDBService, RedisCache redisCache, GsonMapper gsonMapper) {
        this.channelsCosmosContainer = cosmosDBService.getContainer("Channels");
        this.deletedChannelsCosmosContainer = cosmosDBService.getContainer("DeletedChannels");
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
    }

    public CosmosItemResponse<ChannelDAO> createChannel(ChannelDAO channelDAO) {
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(channelDAO), 20);
        return channelsCosmosContainer.createItem(channelDAO);
    }

    public List<ChannelDAO> getChannels() {
        List<ChannelDAO> list = redisCache.getListFromCache(CACHE_LIST, ChannelDAO.class);
        if (list != null) {
            return list;
        }

        CosmosPagedIterable<ChannelDAO> res = channelsCosmosContainer.queryItems("SELECT * FROM Channels", new CosmosQueryRequestOptions(), ChannelDAO.class);
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

        CosmosPagedIterable<ChannelDAO> res = channelsCosmosContainer.queryItems("SELECT * FROM Channels WHERE Channels.id=\"" + id + "\"", new CosmosQueryRequestOptions(), ChannelDAO.class);
        for (ChannelDAO m : res) {
            return m; // There should only be one message with the ID
        }
        return null;
    }

    public CosmosItemResponse<Object> delChannelById(String id) {
        redisCache.deleteChannelFromCacheList(CACHE_LIST, id);

        PartitionKey key = new PartitionKey(id);
        deletedChannelsCosmosContainer.createItem(id);
        return channelsCosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public boolean addUser(String channelId, String user, boolean isSubscribe) {
        ChannelDAO ch = this.getChannelById(channelId);
        if (ch == null) return false;
        if (ch.isPriv() && isSubscribe) return false;

        String[] members = ch.getMembers();
        String[] newMemebers = new String[members.length+1];

        for (int i = 0; i < members.length; i++) {
            if (members[i].equals(user)) return false;
            newMemebers[i] = members[i];
        }

        newMemebers[members.length] = user;
        ch.setMembers(newMemebers);

        CosmosPatchOperations op = CosmosPatchOperations.create().replace("/members",newMemebers);
        channelsCosmosContainer.patchItem(channelId,new PartitionKey(channelId),op, ChannelDAO.class);

        redisCache.deleteUserFromCacheList(CACHE_LIST, user);
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(ch), 20);


        return true;
    }

    public void updateChannel(Channel ch){
        channelsCosmosContainer.replaceItem(ch, ch.getId(), new PartitionKey(ch.getId()), new CosmosItemRequestOptions());

        redisCache.deleteUserFromCacheList(CACHE_LIST, ch.getId());
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(ch), 20);
    }
}
