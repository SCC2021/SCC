package pt.unl.fct.scc.serverless;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import redis.clients.jedis.Jedis;

/**
 * Azure Functions with Blob Trigger.
 */
public class BlobStoreFunction
{
	@FunctionName("blobtest")
	public void setLastBlobInfo(@BlobTrigger(name = "blobtest", 
									dataType = "binary", 
									path = "images/{name}", 
									connection = "BlobStoreConnection") 
								byte[] content,
								@BindingName("name") String blobname, 
								final ExecutionContext context) {
		/*try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.incr("cnt:blob");
			jedis.set("serverless::blob::name",
					"Blob name : " + blobname + " ; size = " + (content == null ? "0" : content.length));
		}*/
	}

}
