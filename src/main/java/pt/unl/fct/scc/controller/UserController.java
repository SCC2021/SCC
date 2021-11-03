package pt.unl.fct.scc.controller;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.model.UserDAO;
import pt.unl.fct.scc.service.UserService;

import java.util.Iterator;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(){
        CosmosPagedIterable res = userService.getUsers();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user){
        CosmosItemResponse res;
        try {
            res = userService.createUser(new UserDAO(user));
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteUser(@PathVariable String id){
        CosmosItemResponse res;
        try {
            res = userService.delUserById(id);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user){
        CosmosItemResponse res;
        try {
            userService.delUserById(id);
            res = userService.createUser(new UserDAO(user));
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<?> getUserByID(@PathVariable String id){
        Iterator res;
        try {
            res = userService.getUserById(id).stream().iterator();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!res.hasNext()){
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }

        User u = (User) res.next();

        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping("/{id}/channels")
    public  ResponseEntity<?> getUserChannelsByID(@PathVariable String id){
        Iterator res;
        try {
            res = userService.getUserById(id).stream().iterator();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!res.hasNext()){
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }

        User u = (User) res.next();

        return new ResponseEntity<>(u.getChannelIds(), HttpStatus.OK);
    }
}
