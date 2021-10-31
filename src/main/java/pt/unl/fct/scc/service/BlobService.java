package pt.unl.fct.scc.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.exceptions.BlobNotFoundException;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

@Service
public class BlobService {
    Logger logger = Logger.getLogger(this.getClass().toString());

    @Value("${azure.myblob.url}")
    private String azureUrl;

    @Value("${azure.myblob.container}")
    private String containerName;

    private BlobContainerClient blobContainerClient;

    @PostConstruct
    public void init(){
        this.blobContainerClient = container();
    }

    private BlobContainerClient container(){
        logger.info("url: " + azureUrl);
        logger.info("container: " + containerName);

        BlobServiceClient serviceClient = new BlobServiceClientBuilder().connectionString(azureUrl).buildClient();
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(containerName);
        return containerClient;
    }

    private BlobClient getClient(String key){
        return this.blobContainerClient.getBlobClient(key);
    }

    public void upload(String key, byte[] data){
        BlobClient client = getClient(key);
        BinaryData bdata = BinaryData.fromBytes(data);
        if (!client.exists()){
            client.upload(bdata);
        }
    }

    public byte[] download(String key) throws BlobNotFoundException {
        BlobClient client = getClient(key);
        if (!client.exists()) throw new BlobNotFoundException();
        return client.downloadContent().toBytes();
    }

}
