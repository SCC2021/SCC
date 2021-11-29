package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String id;
    private String responseTo;
    private String channelDest;
    private String user;
    private String body;
    private String mediaId;

    public void setId() {
        UUID uid = UUID.randomUUID();
        this.id = uid.toString();
    }

}

