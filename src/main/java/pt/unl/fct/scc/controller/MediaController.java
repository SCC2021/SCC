package pt.unl.fct.scc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.scc.exceptions.BlobNotFoundException;
import pt.unl.fct.scc.service.FileService;
import pt.unl.fct.scc.util.Hash;

import java.util.logging.Logger;

@RestController
@RequestMapping("/rest/media")
public class MediaController {
    Logger logger = Logger.getLogger(this.getClass().toString());

    @Autowired
    FileService fileService;

    @PostMapping(consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<String> upload(@RequestBody byte[] media) {
        String key = Hash.of(media);
        try {
            fileService.upload(key, media);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(key, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> download(@PathVariable String id) {
        byte[] data;
        try {
            data = fileService.download(id);
        } catch (BlobNotFoundException e) {
            return new ResponseEntity<>("File Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}