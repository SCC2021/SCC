package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String name;
    private String pwd;
    private String photoId;
    private String[] channelIds;

    public UserDAO( User u) {
        this(u.getId(), u.getName(), u.getPwd(), u.getPhotoId(), u.getChannelIds());
    }

    public UserDAO(String id, String name, String pwd, String photoId, String[] channelIds) {
        super();
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.photoId = photoId;
        this.channelIds = channelIds;
    }
}
