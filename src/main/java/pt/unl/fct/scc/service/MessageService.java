package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.MessageDAO;

@Service
public class MessageService {
    CosmosContainer cosmosContainer;

    public MessageService(CosmosDBService cosmosDBService){
        this.cosmosContainer = cosmosDBService.getContainer("Messages");
    }

    public CosmosItemResponse<MessageDAO> createMessage(MessageDAO message){
        return cosmosContainer.createItem(message);
    }

    public CosmosPagedIterable<MessageDAO> getMessages() {
        return cosmosContainer.queryItems("SELECT * FROM Messages ", new CosmosQueryRequestOptions(), MessageDAO.class);
    }

    public CosmosPagedIterable<MessageDAO> getMessageById( String id) {
        return cosmosContainer.queryItems("SELECT * FROM Messages WHERE Messages.id=\"" + id + "\"", new CosmosQueryRequestOptions(), MessageDAO.class);
    }

    public CosmosItemResponse<Object> delMessageById(String id) {
        PartitionKey key = new PartitionKey( id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }
}
