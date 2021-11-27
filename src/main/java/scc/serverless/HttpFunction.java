package scc.serverless;

import java.util.*;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.annotation.*;

import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;

import com.microsoft.azure.functions.*;
import scc.cosmosLayer.CosmosDBLayer;
import scc.models.UserDAO;

/**
 * Azure Functions with HTTP Trigger. These functions can be accessed at:
 * {Server_URL}/api/{route}
 * Complete URL appear when deploying functions.
 */
public class HttpFunction {
	@FunctionName("http-stats")
	public HttpResponseMessage run(@HttpTrigger(name = "req", 
										methods = {HttpMethod.GET }, 
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

	@FunctionName("get-redis")
	public HttpResponseMessage getRedis(@HttpTrigger(name = "req", 
											methods = {HttpMethod.GET }, 
											authLevel = AuthorizationLevel.ANONYMOUS, 
											route = "serverless/redis/{key}") 
				HttpRequestMessage<Optional<String>> request,
				@BindingName("key") String key, 
				final ExecutionContext context) {
//		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
//			jedis.incr("cnt:http");
//			String val = jedis.get(key);
//		}
		return request.createResponseBuilder(HttpStatus.OK).body("GET key = " + key + "; val = ").build();
	}

	@FunctionName("lrange-redis")
	public HttpResponseMessage lrangeRedis(@HttpTrigger(name = "req", 
											methods = {HttpMethod.GET }, 
											authLevel = AuthorizationLevel.ANONYMOUS, 
											route = "serverless/redis/lrange/{key}") 
				HttpRequestMessage<Optional<String>> request,
				@BindingName("key") String key, 
				final ExecutionContext context) {
//		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
//			jedis.incr("cnt:http");
//			List<String> val = jedis.lrange(key, 0, -1);
//		}
		return request.createResponseBuilder(HttpStatus.OK).body("GET key = " + key + "; val = ").build();
	}

	@FunctionName("set-redis")
	public HttpResponseMessage setRedis(@HttpTrigger(name = "req", 
											methods = {HttpMethod.POST }, 
											authLevel = AuthorizationLevel.ANONYMOUS, 
											route = "serverless/redis/{key}") 
				HttpRequestMessage<Optional<String>> request,
				@BindingName("key") String key, 
				final ExecutionContext context) {
		String val = request.getBody().orElse("");
//		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
//			jedis.incr("cnt:http");
//			jedis.set(key, val);
//		}
		return request.createResponseBuilder(HttpStatus.OK).body("SET key = " + key + "; val = " + val).build();
	}

	@FunctionName("echo")
	public HttpResponseMessage echo(@HttpTrigger(name = "req", 
										methods = {HttpMethod.GET }, 
										authLevel = AuthorizationLevel.ANONYMOUS, 
										route = "serverless/echo/{text}") 
				HttpRequestMessage<Optional<String>> request,
				@BindingName("text") String txt, 
				final ExecutionContext context) {
//		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
//			jedis.incr("cnt:http");
//		}

		return request.createResponseBuilder(HttpStatus.OK).body(txt).build();
	}

	@FunctionName("clean-deleted_channels")
	public HttpResponseMessage cleanDeletedChannels(@HttpTrigger(name = "req",
			methods = {HttpMethod.GET},
			authLevel = AuthorizationLevel.ANONYMOUS,
			route = "rest/users/{id}")
												  HttpRequestMessage<Optional<String>> request,
													@BindingName("id") String id,

													final ExecutionContext context) {
		CosmosDBLayer db = CosmosDBLayer.getInstance();
		CosmosContainer users = db.getUsersContainer();
		CosmosPagedIterable<UserDAO> output = users.queryItems("SELECT * FROM Users WHERE Users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);

		return request.createResponseBuilder(HttpStatus.OK).body(output.toString()).build();
	}
}