package scc.serverless;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.google.gson.Gson;
import scc.cosmosLayer.CosmosDBLayer;
import scc.models.ChannelDAO;
import scc.models.MessageDAO;
import scc.models.UserDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Azure Functions with Timer Trigger.
 */
public class CosmosDBFunction {

    private final String database_name = "scc52919db";
    private final String connectionStringSetting = "AccountEndpoint=https://scc52919.documents.azure.com:443/;AccountKey=n1iU4IqOoR2Czht6WMHbQzGH0MxnPMCQ9nHHAKqm7FTHSa0caUriL9tGLIBKnVH6UQUx7KJLIYCxm22J6gqJ5w==;";


    /**
     * Cleans the deleted members from the affected channels.
     */
    @FunctionName("clean-deleted-users")
    public void cleanDeletedUsers(@CosmosDBTrigger(name = "cleanDeletedUsers",
            databaseName = database_name,
            collectionName = "DeletedUsers",
            createLeaseCollectionIfNotExists = true,
            connectionStringSetting = connectionStringSetting)
                                          String[] deleted_users,
                                  final ExecutionContext context) {
        Gson gson = new Gson();
        CosmosDBLayer db = CosmosDBLayer.getInstance();
        CosmosContainer channels_container = db.getChannelsContainer();
        CosmosContainer messages_container = db.getMessagesContainer();
        CosmosContainer deleted_users_container = db.getDeletedUsersContainer();
        CosmosPagedIterable<ChannelDAO> channels_list = channels_container.queryItems("SELECT * FROM Channels", new CosmosQueryRequestOptions(), ChannelDAO.class);
        CosmosPagedIterable<MessageDAO> messages_list = messages_container.queryItems("SELECT * FROM Messages", new CosmosQueryRequestOptions(), MessageDAO.class);

        // Lists the deleted_users container and clears it.
        List<UserDAO> deleted_users_list = new ArrayList<>();
        for (String deleted_user_str : deleted_users) {
            UserDAO usr = gson.fromJson(deleted_user_str, UserDAO.class);
            deleted_users_list.add(usr);
            deleted_users_container.deleteItem(usr.getId(), new PartitionKey(usr.getId()), new CosmosItemRequestOptions());
        }

        // Iterates through all channels clearing the deleted users' ids from the members array.
        for (ChannelDAO channel : channels_list) {
            boolean removed = false;
            CosmosPatchOperations op = CosmosPatchOperations.create();
            List<String> user_ids = new ArrayList(Arrays.asList(channel.getMembers()));
            for (UserDAO deleted_user : deleted_users_list)
                if (user_ids.remove(deleted_user.getId()))
                    removed = true;
            if (removed) {
                op.replace("/members", user_ids.toArray());
                channels_container.patchItem(channel.getId(), new PartitionKey(channel.getId()), op, ChannelDAO.class);
            }
        }

        // Iterates through all messages, changing the sender name to "Deleted User" if the user was deleted.
        for (MessageDAO message : messages_list)
            if (deleted_users_list.contains(message.getUser())) {
                CosmosPatchOperations op = CosmosPatchOperations.create().replace("/user", "Deleted User");
                messages_container.patchItem(message.getId(), new PartitionKey(message.getId()), op, MessageDAO.class);
            }
    }

    /**
     * Cleans the deleted channels from the affected users.
     */
    @FunctionName("clean-deleted-channels")
    public void cleanDeletedChannels(@CosmosDBTrigger(name = "cleanDeletedUsers",
            databaseName = database_name,
            collectionName = "DeletedChannels",
            createLeaseCollectionIfNotExists = true,
            connectionStringSetting = connectionStringSetting)
                                          String[] deleted_channels,
                                  final ExecutionContext context) {
        Gson gson = new Gson();
        CosmosDBLayer db = CosmosDBLayer.getInstance();
        CosmosContainer deleted_channels_container = db.getChannelsContainer();
        CosmosContainer users_container = db.getUsersContainer();
        CosmosPagedIterable<UserDAO> users_list = users_container.queryItems("SELECT * FROM Users", new CosmosQueryRequestOptions(), UserDAO.class);

        // Lists the deleted_channels container and clears it.
        List<ChannelDAO> deleted_channels_list = new ArrayList<>();
        for (String deleted_channel_str : deleted_channels) {
            ChannelDAO chn = gson.fromJson(deleted_channel_str, ChannelDAO.class);
            deleted_channels_list.add(chn);
            deleted_channels_container.deleteItem(chn.getId(), new PartitionKey(chn.getId()), new CosmosItemRequestOptions());
        }

        // Iterates through all users and clears the deleted-channel's ids.
        for (UserDAO user : users_list) {
            boolean removed = false;
            CosmosPatchOperations op = CosmosPatchOperations.create();
            List<String> user_channel_ids = new ArrayList(Arrays.asList(user.getChannelIds()));
            for (ChannelDAO deleted_channel : deleted_channels_list)
                if (user_channel_ids.remove(deleted_channel.getId()))
                    removed = true;
            if (removed) {
                op.replace("/channelIds", user_channel_ids.toArray());
                users_container.patchItem(user.getId(), new PartitionKey(user.getId()), op, ChannelDAO.class);
            }
        }
    }
}
