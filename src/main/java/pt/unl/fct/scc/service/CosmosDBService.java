package pt.unl.fct.scc.service;

import com.azure.cosmos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CosmosDBService {

    private CosmosClient client;
    private CosmosDatabase db;

    @Value("${azure.cosmos.url}")
    private String azureUrl;

    @Value("${azure.cosmos.primaryKey}")
    private String primaryKey;

    @Value("${azure.cosmos.dbName}")
    private String dbName;

    @PostConstruct
    private void init() {
        this.client = new CosmosClientBuilder()
                .endpoint(azureUrl)
                .key(primaryKey)
                .gatewayMode()        // replace by .directMode() for better performance
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true)
                .buildClient();

        this.db = client.getDatabase(dbName);
    }

    public CosmosContainer getContainer(String containerName) {
        return db.getContainer(containerName);
    }

}
