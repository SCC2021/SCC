package pt.unl.fct.scc.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.UserDAO;

@Service
public class UserService {

    CosmosContainer cosmosContainer;

    public UserService(CosmosDBService cosmosDBService){
        this.cosmosContainer = cosmosDBService.getContainer("Users");
    }

    public CosmosItemResponse<UserDAO> createUser(UserDAO user){
        return cosmosContainer.createItem(user);
    }

    public CosmosPagedIterable<UserDAO> getUsers() {
        return cosmosContainer.queryItems("SELECT * FROM Users ", new CosmosQueryRequestOptions(), UserDAO.class);
    }

    public CosmosPagedIterable<UserDAO> getUserById( String id) {
        return cosmosContainer.queryItems("SELECT * FROM Users WHERE Users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
    }

    public CosmosItemResponse<Object> delUserById(String id) {
        PartitionKey key = new PartitionKey( id);
        return cosmosContainer.deleteItem(id, key, new CosmosItemRequestOptions());
    }
}
