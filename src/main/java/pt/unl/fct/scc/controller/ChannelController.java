package pt.unl.fct.scc.controller;

import com.azure.cosmos.models.CosmosItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.ChannelDAO;
import pt.unl.fct.scc.service.ChannelService;
import pt.unl.fct.scc.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/channels")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> getChannels() {
        List<ChannelDAO> res = channelService.getChannels();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createChannel(@RequestBody Channel channel) {
        CosmosItemResponse res;
        channel.setId(UUID.randomUUID().toString());


        String[] members = channel.getMembers();
        String[] newMemebers = new String[members.length+1];

        for (int i = 0; i < members.length; i++) {
            newMemebers[i] = members[i];
        }
        newMemebers[members.length] = channel.getOwner();
        channel.setMembers(newMemebers);

        try {
            res = channelService.createChannel(new ChannelDAO(channel));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(channel, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannel(@PathVariable String id) {
        CosmosItemResponse res;
        try {
            res = channelService.delChannelById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChannel(@PathVariable String id, @RequestBody Channel channel) {
        CosmosItemResponse res;
        try {
            channelService.updateChannel(channel);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(channel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChannelByID(@PathVariable String id) {
        ChannelDAO res;
        try {
            res = channelService.getChannelById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null) {
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{channelId}/add/{userId}")
    public ResponseEntity<?> addUserToChannel(@PathVariable String channelId, @PathVariable String userId){
        if (userService.getUserById(userId) == null || !channelService.addUser(channelId, userId, false)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.subscibeToChannel(userId, channelId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
