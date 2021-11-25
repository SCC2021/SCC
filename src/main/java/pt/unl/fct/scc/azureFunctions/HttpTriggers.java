package pt.unl.fct.scc.azureFunctions;

import com.microsoft.azure.functions.*;

import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.*;
import redis.clients.jedis.Jedis;

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

}
