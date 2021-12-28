package pt.unl.fct.scc.service;

import org.springframework.stereotype.Service;
import pt.unl.fct.scc.exceptions.BlobNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Logger;

@Service
public class FileService {
    Logger logger = Logger.getLogger(this.getClass().toString());


    public void upload(String key, byte[] data) {

        File f = new File("images/"+key);
        if (!f.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] download(String key) throws BlobNotFoundException {
        File f = new File("images/"+key);
        if (!f.exists()) throw new BlobNotFoundException();
        try {
            return Files.readAllBytes(f.toPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[1];
    }

}
