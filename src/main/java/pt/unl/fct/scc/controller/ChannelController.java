package pt.unl.fct.scc.controller;

import com.azure.cosmos.models.CosmosItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.ChannelDAO;
import pt.unl.fct.scc.service.ChannelService;

import java.util.List;

@RestController
@RequestMapping("/rest/channels")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @GetMapping
    public ResponseEntity<?> getChannels(){
        List<ChannelDAO> res = channelService.getChannels();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createChannel(@RequestBody Channel channel){
        CosmosItemResponse res;
        try{
            res = channelService.createChannel(new ChannelDAO(channel));
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteChannel(@PathVariable String id){
        CosmosItemResponse res;
        try {
            res = channelService.delChannelById(id);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> updateChannel(@PathVariable String id, @RequestBody Channel channel){
        CosmosItemResponse res;
        try {
            channelService.delChannelById(id);
            res = channelService.createChannel(new ChannelDAO(channel));
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<?> getChannelByID(@PathVariable String id){
        ChannelDAO res;
        try {
            res = channelService.getChannelById(id);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null){
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
