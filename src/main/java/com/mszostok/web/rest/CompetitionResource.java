package com.mszostok.web.rest;

import com.mszostok.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
public class CompetitionResource {

  @Autowired
  private StorageService storageService;

  @PostMapping("/upload")
  public ResponseEntity<?> handleFileUpload(@RequestParam("file") final MultipartFile file) {

    try {
      storageService.store(file);
    } catch (Exception ex) {
      return new ResponseEntity<>("err", INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>("ok", CREATED);
  }
}
