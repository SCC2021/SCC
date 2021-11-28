package scc.cosmosLayer;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

public class CosmosDBLayer {
	private static final String CONNECTION_URL = "https://scc52698db.documents.azure.com:443/";
	private static final String DB_KEY = "tVVosAfw3tJSCzmmet8pA7V01bNRQHuXIbqrB1qYsFAJymyN2wJdUeg91p8McFli220dmvPEYzLNkdUecmUKAQ==";
	private static final String DB_NAME = "scc52698db";
	
	private static CosmosDBLayer instance;

	public static synchronized CosmosDBLayer getInstance() {
		if( instance != null)
			return instance;

		CosmosClient client = new CosmosClientBuilder()
		         .endpoint(CONNECTION_URL)
		         .key(DB_KEY)
		         .gatewayMode()		// replace by .directMode() for better performance
		         .consistencyLevel(ConsistencyLevel.SESSION)
		         .connectionSharingAcrossClientsEnabled(true)
		         .contentResponseOnWriteEnabled(true)
		         .buildClient();
		instance = new CosmosDBLayer( client);
		return instance;
		
	}
	
	private CosmosClient client;
	private CosmosDatabase db;

	public CosmosDBLayer(CosmosClient client) {
		this.client = client;
	}
	
	public synchronized CosmosContainer getUsersContainer() {
		if( db != null)
			return db.getContainer("Users");
		db = client.getDatabase(DB_NAME);
		return db.getContainer("Users");
	}

	public synchronized CosmosContainer getChannelsContainer() {
		if( db != null)
			return db.getContainer("Channels");
		db = client.getDatabase(DB_NAME);
		return db.getContainer("Channels");
	}

	public synchronized CosmosContainer getMessagesContainer() {
		if( db != null)
			return db.getContainer("Messages");
		db = client.getDatabase(DB_NAME);
		return db.getContainer("Messages");
	}


	/*public CosmosItemResponse<Object> delUserById(String id) {
		getUsersContainer();
		PartitionKey key = new PartitionKey( id);
		return users.deleteItem(id, key, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<Object> delUser(UserDAO user) {
		getUsersContainer();
		return users.deleteItem(user, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
		getUsersContainer();
		return users.createItem(user);
	}

	public CosmosPagedIterable<UserDAO> getUserById( String id) {
		getUsersContainer();
		return users.queryItems("SELECT * FROM Users WHERE Users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public CosmosPagedIterable<UserDAO> getUsers() {
		getUsersContainer();
		return users.queryItems("SELECT * FROM Users ", new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public void close() {
		client.close();
	}*/
	
	
}
