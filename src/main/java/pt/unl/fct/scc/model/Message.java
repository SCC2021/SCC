package pt.unl.fct.scc.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document
public class Message {
    private String messageID;
    private String responseTo;
    private String channelDest;
    private String user;
    private String body;
    private String mediaId;
    private long sentAt;
    private Boolean deleted;

    public Message(){
        this.deleted = false;
    }

    public void setId() {
        UUID uid = UUID.randomUUID();
        this.messageID = uid.toString();
    }

    public void setSent() {
        this.sentAt = System.currentTimeMillis();
    }

}

