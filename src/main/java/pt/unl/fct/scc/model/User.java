package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {
    @Indexed(unique = true)
    private String userID;
    private String name;
    private String pwd;
    private String photoId;
    private List<String> channelIds;
}
