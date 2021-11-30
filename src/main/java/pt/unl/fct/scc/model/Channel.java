package pt.unl.fct.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    private String id;
    private String name;
    private boolean priv;
    private String owner;
    private String[] members;
}
