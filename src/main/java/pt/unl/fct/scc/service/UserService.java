package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.DeletedDAO;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.model.UserDAO;
import pt.unl.fct.scc.util.GsonMapper;
import pt.unl.fct.scc.util.Hash;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserService {

    CosmosContainer usersCosmosContainer;
    CosmosContainer deletedUsersCosmosContainer;
    private final Gson gson;
    private final RedisCache redisCache;
    private final String CACHE_LIST = "recentUsers";
    private final Hash hash;


    public UserService(CosmosDBService cosmosDBService, RedisCache redisCache, GsonMapper gsonMapper, Hash hash) {
        this.usersCosmosContainer = cosmosDBService.getContainer("Users");
        this.deletedUsersCosmosContainer = cosmosDBService.getContainer("DeletedUsers");
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
        this.hash = hash;
    }

    public CosmosItemResponse<UserDAO> createUser(UserDAO user) {
        user.setPwd(hash.of(user.getPwd()));
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(user), 20);
        return usersCosmosContainer.createItem(user);
    }

    public List<UserDAO> getUsers() {
        List<UserDAO> list = redisCache.getListFromCache(CACHE_LIST, UserDAO.class);
        if (list != null) {
            return list;
        }

        CosmosPagedIterable<UserDAO> res = usersCosmosContainer.queryItems("SELECT * FROM Users ", new CosmosQueryRequestOptions(), UserDAO.class);

        List<UserDAO> ret = new LinkedList<>();
        for (UserDAO m : res) {
            ret.add(m);
        }
        return ret;
    }

    public UserDAO getUserById(String id) {
        UserDAO cache = redisCache.getUserFromCacheList(CACHE_LIST, id);
        if (cache != null) {
            return cache;
        }

        CosmosPagedIterable<UserDAO> res = usersCosmosContainer.queryItems("SELECT * FROM Users WHERE Users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
        for (UserDAO m : res) {
            return m; // There should only be one message with the ID
        }
        return null;
    }

    public CosmosItemResponse<Object> delUserById(String id) {
        redisCache.deleteUserFromCacheList(CACHE_LIST, id);
        DeletedDAO deleted = new DeletedDAO();
        deleted.setId(id);
        deletedUsersCosmosContainer.createItem(deleted);
        System.out.println("Added to deleted DB");
        return usersCosmosContainer.deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
    }

    public void subscibeToChannel(String user, String channelId) {
        UserDAO u = this.getUserById(user);

        List<String> channelIds = u.getChannelIds();
        if (channelIds == null) channelIds = (new ArrayList<>());
        channelIds.add(channelId);
        u.setChannelIds(channelIds);

        CosmosPatchOperations op = CosmosPatchOperations.create().replace("/channelIds",channelIds);
        usersCosmosContainer.patchItem(user,new PartitionKey(user),op,UserDAO.class);

        redisCache.deleteChannelFromCacheList(CACHE_LIST,channelId);
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(u), 20);

    }

    public void updateUser(User user){
        usersCosmosContainer.replaceItem(user, user.getId(), new PartitionKey(user.getId()), new CosmosItemRequestOptions());

        redisCache.deleteUserFromCacheList(CACHE_LIST, user.getId());
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(user), 20);
    }

}
