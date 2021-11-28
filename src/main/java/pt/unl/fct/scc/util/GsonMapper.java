package pt.unl.fct.scc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

@Service
public class GsonMapper {
    private Gson gson;
    public GsonMapper(){
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        this.gson = builder.create();
    }
    public Gson getGson(){ return gson; }
}
