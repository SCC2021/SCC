package pt.unl.fct.scc.azureFunctions;

import com.microsoft.azure.functions.*;

import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class HttpTriggers {

    @FunctionName("delete-channel")
    public HttpResponseMessage updateUsersAfterChannelDelete(@HttpTrigger(name = "updateUsersAfterChannelDelete",
            methods = {HttpMethod.DELETE},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "/rest/channels/{id}")
                                            HttpRequestMessage<Optional<String>> request,
                                    @BindingName("id") String id,
                                    final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK).body(String.format("Updated users in deleted channel: %s", id)).build();
    }

    public HttpResponseMessage updateChannelsAfterUserDelete(@HttpTrigger(name = "updateChannelsAfterUserDelete",
            methods = {HttpMethod.DELETE},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "/rest/users/{id}")
                                                                     HttpRequestMessage<Optional<String>> request,
                                                             @BindingName("id") String id,
                                                             final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK).body(String.format("Updated channels with deleted user: %s", id)).build();
    }

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
