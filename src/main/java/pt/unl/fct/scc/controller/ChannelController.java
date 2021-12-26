package pt.unl.fct.scc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.service.ChannelService;
import pt.unl.fct.scc.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/rest/channels")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> getChannels() {
        List<Channel> res = channelService.getChannels();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createChannel(@RequestBody Channel channel) {
        channel.setChannelID(UUID.randomUUID().toString());
        String owner = channel.getOwner();
        if (channel.getMembers() == null) channel.setMembers(new LinkedList<String>());
        channel.getMembers().add(owner == null ? "":owner);

        try {
           channelService.createChannel(channel);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(String.format("Your channel was created successfully with ID: %s.", channel.getChannelID()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannel(@PathVariable String id) {
        try {
            channelService.delChannelById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(String.format("The channel with ID: %s was deleted successfully.", id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChannel(@PathVariable String id, @RequestBody Channel channel) {
        Channel check;
        try {
            check = channelService.getChannelById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (check == null) {
            return new ResponseEntity<>(String.format("The channel with ID: %s was not found.", channel.getChannelID()), HttpStatus.NOT_FOUND);
        }
        try {
            channelService.updateChannel(channel);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(String.format("The channel with ID: %s was updated successfully.", channel.getChannelID()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChannelByID(@PathVariable String id) {
        Channel res;
        try {
            res = channelService.getChannelById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null) {
            return new ResponseEntity<>(String.format("The channel with ID: %s was not found.", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{channelId}/add/{userId}")
    public ResponseEntity<?> addUserToChannel(@PathVariable String channelId, @PathVariable String userId){
        if (userService.getUserById(userId) == null) {
            return new ResponseEntity<>(String.format("The user with ID: %s was not found.", userId), HttpStatus.NOT_FOUND);
        } else if (!channelService.addUser(channelId, userId, false)) {
            return new ResponseEntity<>("Channel not found or is private", HttpStatus.FORBIDDEN);
        }
        userService.subscibeToChannel(userId, channelId);

        return new ResponseEntity<>(String.format("The user %s has subscribed to %s", userId, channelId),HttpStatus.OK);
    }
}
