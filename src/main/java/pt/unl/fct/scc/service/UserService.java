package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.UserDAO;
import pt.unl.fct.scc.util.GsonMapper;
import pt.unl.fct.scc.util.Hash;

import java.util.LinkedList;
import java.util.List;

@Service
public class UserService {

    CosmosContainer cosmosContainer;
    private final Gson gson;
    private final RedisCache redisCache;
    private final String CACHE_LIST = "recentUsers";
    private final Hash hash;


    public UserService(CosmosDBService cosmosDBService, RedisCache redisCache, GsonMapper gsonMapper, Hash hash) {
        this.cosmosContainer = cosmosDBService.getContainer("Users");
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
        this.hash = hash;
    }

    public CosmosItemResponse<UserDAO> createUser(UserDAO user) {
        user.setPwd(hash.of(user.getPwd()));
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(user), 20);
        return cosmosContainer.createItem(user);
    }

    public List<UserDAO> getUsers() {
        List<UserDAO> list = redisCache.getListFromCache(CACHE_LIST, UserDAO.class);
        if (list != null) {
            return list;
        }

        CosmosPagedIterable<UserDAO> res = cosmosContainer.queryItems("SELECT * FROM Users ", new CosmosQueryRequestOptions(), UserDAO.class);

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

        CosmosPagedIterable<UserDAO> res = cosmosContainer.queryItems("SELECT * FROM Users WHERE Users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
        for (UserDAO m : res) {
            return m; // There should only be one message with the ID
        }
        return null;
    }

    public CosmosItemResponse<Object> delUserById(String id) {
        redisCache.deleteUserFromCacheList(CACHE_LIST, id);

        PartitionKey key = new PartitionKey(id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }
}
