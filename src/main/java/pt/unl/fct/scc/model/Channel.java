package pt.unl.fct.scc.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Channel {
    @Indexed(unique = true)
    private String channelID;
    private String name;
    private boolean priv;
    private String owner;
    private List<String> members;
    private List<Message> messageList;
    private Boolean deleted;

    public Channel(){
        this.deleted = false;
    }
}
