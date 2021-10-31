package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.ChannelDAO;

@Service
public class ChannelService {

    private CosmosContainer cosmosContainer;

    public ChannelService(CosmosDBService cosmosDBService){
        this.cosmosContainer = cosmosDBService.getContainer("Channels");
    }

    public CosmosItemResponse<ChannelDAO> createChannel(ChannelDAO channelDAO){
        return cosmosContainer.createItem(channelDAO);
    }

    public CosmosPagedIterable<ChannelDAO> getChannels(){
        return cosmosContainer.queryItems("SELECT * FROM Channels", new CosmosQueryRequestOptions(), ChannelDAO.class);
    }

    public CosmosPagedIterable<ChannelDAO> getChannelById(String id){
        return cosmosContainer.queryItems("SELECT * FROM Channels WHERE Channels.id=\""+id+"\"", new CosmosQueryRequestOptions(), ChannelDAO.class);
    }

    public CosmosItemResponse<Object> delChannelById(String id){
        PartitionKey key = new PartitionKey(id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }

}
