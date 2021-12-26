package pt.unl.fct.scc.service;

import com.google.gson.Gson;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.repository.UserRepo;
import pt.unl.fct.scc.util.GsonMapper;
import pt.unl.fct.scc.util.Hash;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final Gson gson;
    private final RedisCache redisCache;
    private final String CACHE_LIST = "recentUsers";
    private final Hash hash;
    private final MongoTemplate mongoTemplate;
    private final UserRepo userRepo;


    public UserService(RedisCache redisCache, GsonMapper gsonMapper, Hash hash, MongoTemplate mongoTemplate, UserRepo userRepo) {
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
        this.hash = hash;
        this.mongoTemplate = mongoTemplate;
        this.userRepo = userRepo;
    }

    public void createUser(User user) {
        user.setPwd(hash.of(user.getPwd()));
        if (user.getChannelIds() == null){
            user.setChannelIds(new ArrayList<>());
        }
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(user), 20);
        userRepo.save(user);
    }

    public List<User> getUsers() {
        List<User> list = redisCache.getListFromCache(CACHE_LIST, User.class);
        if (list != null) {
            return list;
        }

        list = userRepo.findAll();

        return list;
    }

    public User getUserById(String id) {
        User cache = redisCache.getUserFromCacheList(CACHE_LIST, id);
        if (cache != null) {
            return cache;
        }

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("userID").is(id));
            return mongoTemplate.find(query, User.class).get(0);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public void delUserById(String id) {
        redisCache.deleteUserFromCacheList(CACHE_LIST, id);
        Query query = new Query();
        query.addCriteria(Criteria.where("userID").is(id));
        DeleteResult res = mongoTemplate.remove(query, User.class);
        if (res.getDeletedCount() == 0){
            return;
        }

    }

    public void subscibeToChannel(String user, String channelId) {
        User u = this.getUserById(user);

        if (u == null) return;

        List<String> channelIds = u.getChannelIds();
        if (channelIds == null) channelIds = (new ArrayList<>());
        channelIds.add(channelId);
        u.setChannelIds(channelIds);

        this.updateUser(u);

        redisCache.deleteChannelFromCacheList(CACHE_LIST,channelId);
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(u), 20);

    }

    public void updateUser(User user){
        this.delUserById(user.getUserID());
        this.createUser(user);

        redisCache.deleteUserFromCacheList(CACHE_LIST, user.getUserID());
        redisCache.storeInCacheListLimited(CACHE_LIST, gson.toJson(user), 20);
    }

}
