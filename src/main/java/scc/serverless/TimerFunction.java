package scc.serverless;


import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import scc.cosmosLayer.CosmosDBLayer;
import scc.models.ChannelDAO;
import scc.models.UserDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Azure Functions with Timer Trigger.
 */
public class TimerFunction {
    @FunctionName("deleted-users-cleanup")
    public void deletedUsersCleanup(@TimerTrigger(name = "deletedUsersCleanup",
            schedule = "30 */1 * * * *")
                               ExecutionContext context) {
        CosmosDBLayer db = CosmosDBLayer.getInstance();
        CosmosContainer users_container = db.getUsersContainer();
        CosmosContainer channels_container = db.getChannelsContainer();

        CosmosPagedIterable<UserDAO> deleted_users = users_container.queryItems("SELECT * FROM Users", new CosmosQueryRequestOptions(), UserDAO.class);
        CosmosPagedIterable<ChannelDAO> channels_list = channels_container.queryItems("SELECT * FROM Users", new CosmosQueryRequestOptions(), ChannelDAO.class);

        for(ChannelDAO channel : channels_list){
            List<String> user_ids = new ArrayList(Arrays.asList(channel.getMembers()));
            for(UserDAO user : deleted_users){
                
            }

        }

    }

    @FunctionName("deleted-channels-cleanup")
    public void deletedChannelsCleanup(@TimerTrigger(name = "deletedChannelsCleanup",
            schedule = "30 */1 * * * *")
                                            ExecutionContext context) {

    }
}
