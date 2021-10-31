package pt.unl.fct.scc.controller;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.model.Message;
import pt.unl.fct.scc.model.MessageDAO;
import pt.unl.fct.scc.service.MessageService;

@RestController
@RequestMapping("/rest/messages")
public class MessageController {
    
    @Autowired
    MessageService messageService;

    @GetMapping
    public ResponseEntity<?> getMessages(){
        CosmosPagedIterable res = messageService.getMessages();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Message message){
        CosmosItemResponse res;
        try {
            res = messageService.createMessage(new MessageDAO(message));
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteMessage(@PathVariable String id){
        CosmosItemResponse res;
        try {
            res = messageService.delMessageById(id);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> updateMessage(@PathVariable String id, @RequestBody Message message){
        CosmosItemResponse res;
        try {
            messageService.delMessageById(id);
            res = messageService.createMessage(new MessageDAO(message));
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<?> getMessageByID(@PathVariable String id){
        Object[] res;
        try {
            res = messageService.getMessageById(id).stream().toArray();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (res.length <= 0){
            return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
