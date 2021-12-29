package pt.unl.fct.scc.controller;

import com.azure.cosmos.models.CosmosItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.Message;
import pt.unl.fct.scc.service.ChannelService;
import pt.unl.fct.scc.service.MessageService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/rest/messages")
public class MessageController {

    Logger logger = Logger.getLogger(this.getClass().toString());

    @Autowired
    MessageService messageService;

    @Autowired
    ChannelService channelService;

    @GetMapping
    public ResponseEntity<?> getMessages() {
        logger.info("GET MESSAGES");
        List<Message> res = messageService.getMessages();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Message message, HttpServletRequest request) {
        logger.info("CREATING MESSAGE");
        try {
            message.setId();
            message.setSent();
            if (!this.CheckUser(request, message.getUser())) {
                return new ResponseEntity<>("Invalid session, login first", HttpStatus.FORBIDDEN);
            }
            if (channelService.getChannelById(message.getChannelDest()) == null){
                return new ResponseEntity<>("Invalid Channel", HttpStatus.FORBIDDEN);

            }
            messageService.createMessage(message);
            channelService.insertMessage(message);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable String id) {
        logger.info("DELETE MESSAGES");
        try {
            messageService.delMessageById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(String.format("Message with ID: %s deleted successfully.", id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable String id, @RequestBody Message message) {
        logger.info("UPDATING MESSAGES");
        Message res;
        try {
            res = messageService.getMessageById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null)
            return new ResponseEntity<>(String.format("The message with ID: %s was not found.", id), HttpStatus.NOT_FOUND);

        try {
            message.setMessageID(id);
            message.setSent();
            messageService.updateMessage(message);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageByID(@PathVariable String id) {
        logger.info("GET MESSAGE /id");
        Message res;
        try {
            res = messageService.getMessageById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null)
            return new ResponseEntity<>(String.format("The message with ID: %s was not found.", id), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private boolean CheckUser(HttpServletRequest request, String id) {
        Cookie[] cookies = request.getCookies();
        String userId = "";
        for (Cookie c : cookies) {
            userId = c.getValue().split("\\.")[1];
        }

        return userId.equals(id);
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingChannels() {
        logger.info("TREING NOT IMPLEMENTED");
        List<Channel> res = messageService.getTrendingChannels();
        // String.format("SELECT c.channelDest , count(c.channelDest) as messageCount FROM c WHERE c.sentAt > %s  GROUP BY c.channelDest", last_15_minutes
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
