package scc.serverless;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import scc.cosmosLayer.CosmosDBLayer;
import scc.models.UserDAO;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger. These functions can be accessed at:
 * {Server_URL}/api/{route}
 * Complete URL appear when deploying functions.
 */
public class HttpFunction {
    @FunctionName("http-stats")
    public HttpResponseMessage run(@HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "serverless/stats")
                                           HttpRequestMessage<Optional<String>> request,
                                   final ExecutionContext context) {
        StringBuffer result = new StringBuffer();
        result.append("Serverless stats: v. 0001 : \n");
//		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
//			Long vall = jedis.incr("cnt:http");
//			result.append("HTTP functions called ");
//			result.append(vall);
//			result.append(" times.\n");
//
//			String val = jedis.get("cnt:cosmos");
//			if( val == null)
//				val = "0";
//			result.append("Cosmos functions called ");
//			result.append(val);
//			result.append(" times.\n");
//
//			val = jedis.get("cnt:blob");
//			if( val == null)
//				val = "0";
//			result.append("Blob functions called ");
//			result.append(val);
//			result.append(" times.\n");
//
//			val = jedis.get("cnt:timer");
//			if( val == null)
//				val = "0";
//			result.append("Timer functions called ");
//			result.append(val);
//			result.append(" times.\n");
//		}
        return request.createResponseBuilder(HttpStatus.OK).body(result.toString()).build();
    }


    @FunctionName("trending-channels")
    public HttpResponseMessage calculateTrendingChannels(@HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "trending")
                                                                 HttpRequestMessage<Optional<String>> request,
                                                         final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK).body("").build();
    }
}
