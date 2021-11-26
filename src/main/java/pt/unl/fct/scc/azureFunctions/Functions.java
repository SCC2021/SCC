package pt.unl.fct.scc.azureFunctions;

import com.azure.cosmos.CosmosContainer;
import com.microsoft.azure.functions.*;

import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.service.CosmosDBService;

import java.util.Optional;

//@Service
public class Functions {

    /*CosmosContainer userContainer;

    public HttpTriggers(CosmosDBService cosmosDBService){
        this.userContainer = cosmosDBService.getContainer("Users");
    }*/

    @FunctionName("clean-dead-channels")
    public HttpResponseMessage updateUsersAfterChannelDelete(@HttpTrigger(name = "updateUsersAfterChannelDelete",
            methods = {HttpMethod.DELETE},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "/rest/channels/{id}") HttpRequestMessage<Optional<String>> request,
                                                             @BindingName("id") String id,
                                                             final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK).body(String.format("Updated users in deleted channel: %s", id)).build();
    }

    @FunctionName("clean-dead-users")
    public HttpResponseMessage updateChannelsAfterUserDelete(@HttpTrigger(name = "updateChannelsAfterUserDelete",
            methods = {HttpMethod.DELETE},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "/rest/users/{id}")
                                                                     HttpRequestMessage<Optional<String>> request,
                                                             @BindingName("id") String id,
                                                             final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK).body(String.format("Updated channels with deleted user: %s", id)).build();
    }

    @FunctionName("update-message-from-default-user")
    public HttpResponseMessage updateMessagesAfterUserDelete(@HttpTrigger(name = "updateMessagesAfterUserDelete",
            methods = {HttpMethod.DELETE},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "/rest/users/{id}")
                                                                     HttpRequestMessage<Optional<String>> request,
                                                             @BindingName("id") String id,
                                                             final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK).body(String.format("Updated messages from deleted user: %s", id)).build();
    }
}
