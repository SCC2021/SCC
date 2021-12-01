package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    private String id;
    private String name;
    private boolean priv;
    private String owner;
    private List<String> members;
}
