package pt.unl.fct.scc.controller;

import com.azure.cosmos.models.CosmosItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.ChannelDAO;
import pt.unl.fct.scc.model.Message;
import pt.unl.fct.scc.model.MessageDAO;
import pt.unl.fct.scc.service.MessageService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/rest/messages")
public class MessageController {

    @Autowired
    MessageService messageService;

    @GetMapping
    public ResponseEntity<?> getMessages() {
        List<MessageDAO> res = messageService.getMessages();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Message message, HttpServletRequest request) {
        CosmosItemResponse res;
        try {
            message.setId();
            message.setSent();
            if (!this.CheckUser(request,message.getUser())){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            System.out.println(message);
            messageService.createMessage(new MessageDAO(message));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable String id) {
        CosmosItemResponse res;
        try {
            res = messageService.delMessageById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable String id, @RequestBody Message message) {
        CosmosItemResponse res;
        try {
            messageService.delMessageById(id);
            res = messageService.createMessage(new MessageDAO(message));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageByID(@PathVariable String id) {
        MessageDAO res;
        try {
            res = messageService.getMessageById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res == null) {
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
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
        List<ChannelDAO> res = messageService.getTrendingChannels();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
