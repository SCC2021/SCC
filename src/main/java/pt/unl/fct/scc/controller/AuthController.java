package pt.unl.fct.scc.controller;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.AuthModel;
import pt.unl.fct.scc.service.AuthService;
import pt.unl.fct.scc.service.RedisCache;
import pt.unl.fct.scc.util.GsonMapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/rest/login")
public class AuthController {
    private final AuthService authService;
    private final Gson gson;
    private final RedisCache redisCache;
    private Logger logger = Logger.getLogger(this.getClass().toString());

    public AuthController(AuthService authService, GsonMapper gsonMapper, RedisCache redisCache) {
        this.authService = authService;
        this.gson = gsonMapper.getGson();
        this.redisCache = redisCache;
    }

    @GetMapping()
    public ResponseEntity<?> deleteCache() {
        redisCache.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public void login(@RequestBody AuthModel authModel, HttpServletResponse response) {
        logger.info("Login");
        System.out.println(authModel.getId());
        System.out.println(authModel.getPassword());
        if (!authService.checkAccess(authModel)) {
            response.setStatus(401);
            return;
        }
        UUID sessionId = UUID.randomUUID();
        String sessionValue = sessionId.toString() + "." + authModel.getId();

        redisCache.storeInCache(sessionId.toString(), sessionValue);

        Cookie c = new Cookie("scc.session", sessionValue);
        c.setSecure(false);
        c.setComment("sessionId");
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(3600);
        response.addCookie(c);

    }

}
