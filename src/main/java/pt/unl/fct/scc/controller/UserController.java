package pt.unl.fct.scc.controller;

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
        logger.info("GET USER");
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
        logger.info("CREATE USER");
        try {
            userService.createUser(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return new ResponseEntity<>(String.format("The userID: %s is already in use.", user.getUserID()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            System.out.println(String.format("Error creating user: %s, Exception class:%s.", user.getUserID(), e.getClass()));
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
        logger.info("DELETE USER");
        if (!this.CheckUser(request, id)) {
            return new ResponseEntity<>("Conflict between your session and the user you're trying to delete", HttpStatus.FORBIDDEN);
        }
        try {
            userService.delUserById(id);
        } catch (Exception e) {
            System.out.println(e.getClass());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(String.format("The user with ID: %s was deleted successfully.", id), HttpStatus.OK);
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
        logger.info("UPDATE USER");
        if (!this.CheckUser(request, id)) {
            return new ResponseEntity<>("Conflict between your session and the user you're trying to update", HttpStatus.FORBIDDEN);
        }
        User check;
        try {
            check = userService.getUserById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (check == null) {
            return new ResponseEntity<>(String.format("The user with ID: %s was not found.", id), HttpStatus.NOT_FOUND);
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
        logger.info("GET USER /id");
        User res;
        try {
            res = userService.getUserById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null) {
            return new ResponseEntity<>(String.format("The user with ID: %s was not found.", id), HttpStatus.NOT_FOUND);
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
    public ResponseEntity<?> getUserChannelsByID(@PathVariable String id, HttpServletRequest request) {
        logger.info("GET CHANNELS FROM USER");
        User res;
        if (!this.CheckUser(request, id)) {
            return new ResponseEntity<>("Conflict between your session and the user you're trying to check", HttpStatus.FORBIDDEN);
        }
        try {
            res = userService.getUserById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null) {
            return new ResponseEntity<>(String.format("The user with ID: %s was not found.", id), HttpStatus.NOT_FOUND);
        }

        if (res.getChannelIds().size() == 0)
            return new ResponseEntity<>("The user is not subscribed in any channels", HttpStatus.OK);

        return new ResponseEntity<>(res.getChannelIds(), HttpStatus.OK);
    }

    @PostMapping("/{userId}/subscribe/{channelId}")
    public ResponseEntity<?> subscribe(@PathVariable String userId, @PathVariable String channelId) {
        logger.info("SUBSCRIBING TO CHANNEL");
        if (userService.getUserById(userId) == null) {
            return new ResponseEntity<>("User not found or the channel is private", HttpStatus.NOT_FOUND);
        } else if (!channelService.addUser(channelId, userId, true)) {
            return new ResponseEntity<>("Channel not found or is private", HttpStatus.FORBIDDEN);
        }
        userService.subscibeToChannel(userId, channelId);

        return new ResponseEntity<>(String.format("The user %s has subscribed to %s", userId, channelId), HttpStatus.OK);
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
