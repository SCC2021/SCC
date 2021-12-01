package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDAO {
    private String _rid;
    private String _ts;
    private String user;
    private String channelDest;
    private String body;
    private String mediaId;
    private String responseTo;
    private String id;
    private long sentAt;


    public MessageDAO(Message m) {
        this.user = m.getUser();
        this.channelDest = m.getChannelDest();
        this.mediaId = m.getMediaId();
        this.body = m.getBody();
        this.responseTo = m.getResponseTo();
        this.id = m.getId();
        this.sentAt = m.getSentAt();
    }
}
