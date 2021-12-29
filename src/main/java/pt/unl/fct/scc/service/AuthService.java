package pt.unl.fct.scc.service;

import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.AuthModel;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.util.Hash;

@Service
public class AuthService {
    private final UserService userService;
    private final Hash hash;
    private final RedisCache redisCache;


    public AuthService(UserService userService, Hash hash, RedisCache redisCache) {
        this.userService = userService;
        this.hash = hash;
        this.redisCache = redisCache;
    }

    public boolean checkAccess(AuthModel authModel) {
        User user = userService.getUserById(authModel.getId());
        if (user == null) return false;
        return (user.getPwd().equals(authModel.getPassword()));
    }


    public boolean searchSession(String sessioId, String value) {
        String session = redisCache.getFromCache(sessioId);
        if (session == null) {
            return false;
        }
        return session.equals(value);
    }
}
