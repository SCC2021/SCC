package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String name;
    private String status;
    private String owner;
    private List<String> members;

    public ChannelDAO(Channel channel){
        this.id = channel.getId();
        this.name = channel.getName();
        this.status = channel.getStatus();
        this.owner = channel.getOwner();
        this.members = channel.getMembers();
    }

}
