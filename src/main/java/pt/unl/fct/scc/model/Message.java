package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Message {
    @Indexed(unique = true)
    private String messageID;
    private String responseTo;
    private String channelDest;
    private String user;
    private String body;
    private String mediaId;
    private long sentAt;

    public void setId() {
        UUID uid = UUID.randomUUID();
        this.messageID = uid.toString();
    }

    public void setSent(){
        this.sentAt = System.currentTimeMillis();
    }

}

