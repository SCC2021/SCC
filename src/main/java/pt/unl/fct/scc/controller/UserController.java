package pt.unl.fct.scc.controller;

import com.azure.cosmos.models.CosmosItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.service.ChannelService;
import pt.unl.fct.scc.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/rest/users")
public class UserController {
    Logger logger = Logger.getLogger(this.getClass().toString());
    @Autowired
    UserService userService;

    @Autowired
    ChannelService channelService;

    /**
     * GET "/"
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getUsers() {
        List<User> res = userService.getUsers();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * POST "/"
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * DELETE "/id"
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id, HttpServletRequest request) {
        if (!this.CheckUser(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            userService.delUserById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    /**
     * PUT "/id"
     *
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user, HttpServletRequest request) {
        CosmosItemResponse res;
        if (!this.CheckUser(request, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * GET "/id"
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable String id) {
        User res;
        try {
            res = userService.getUserById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null) {
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * GET "/id/channels"
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/channels")
    public ResponseEntity<?> getUserChannelsByID(@PathVariable String id) {
        User res;
        try {
            res = userService.getUserById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null) {
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res.getChannelIds(), HttpStatus.OK);
    }

    @PostMapping("/{userId}/subscribe/{channelId}")
    public ResponseEntity<?> subscribe(@PathVariable String userId, @PathVariable String channelId){
        if (userService.getUserById(userId) == null || !channelService.addUser(channelId, userId, true)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.subscibeToChannel(userId, channelId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean CheckUser(HttpServletRequest request, String id) {
        Cookie[] cookies = request.getCookies();
        String userId = "";
        for (Cookie c : cookies) {
            userId = c.getValue().split("\\.")[1];
        }

        return userId.equals(id);
    }
}
