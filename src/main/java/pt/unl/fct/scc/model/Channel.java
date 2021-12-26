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
public class Channel {
    @Indexed(unique = true)
    private String channelID;
    private String name;
    private boolean priv;
    private String owner;
    private List<String> members;
}
