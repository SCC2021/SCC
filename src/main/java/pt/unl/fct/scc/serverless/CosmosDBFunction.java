package pt.unl.fct.scc.serverless;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import pt.unl.fct.scc.service.RedisCache;
import redis.clients.jedis.Jedis;

/**
 * Azure Functions with Timer Trigger.
 */
public class CosmosDBFunction {
    @FunctionName("cosmosDBtest")
    public void updateMostRecentUsers(@CosmosDBTrigger(name = "cosmosTest",
    										databaseName = "scc2122dbnmp",
    										collectionName = "users",
    										createLeaseCollectionIfNotExists = true,
    										connectionStringSetting = "AzureCosmosDBConnection") 
        							String[] users,
        							final ExecutionContext context ) {

    }

}
