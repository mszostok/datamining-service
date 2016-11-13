package com.mszostok.service;

import com.mszostok.configuration.AppConfig;
import com.mszostok.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service("fileSystemStorageService")
@Slf4j
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;

  @Autowired
  public FileSystemStorageService(final AppConfig appConfig) {
    this.rootLocation = Paths.get(appConfig.getStorage().getUploadLocation());
    log.info("dupa: {}", rootLocation);
  }

  @PostConstruct
  public void init() {
    try {
      log.debug("Creating directory: {} for storing uploaded files", rootLocation);
      Files.createDirectories(rootLocation);
    } catch (IOException ex) {
      log.error("While initializing storage: ", ex);
      throw new StorageException("While initializing storage", ex);
    }
  }

  @Override
  public void store(final MultipartFile file) throws StorageException {
    try {
      if (file.isEmpty()) {
        log.warn("Was uploaded empty file {}", file.getOriginalFilename());
        throw new StorageException("Empty file " + file.getOriginalFilename());
      }

      Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
    } catch (IOException ex) {
      log.error("While storing file: {}", ex);
      throw new StorageException("While storing file " + file.getOriginalFilename(), ex);
    }
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1)
        .filter(path -> !path.equals(this.rootLocation))
        .map(this.rootLocation::relativize);
    } catch (IOException ex) {
      log.error("While reading stored files: {}", ex);
      throw new StorageException("While reading stored files", ex);
    }

  }

  @Override
  public Path load(final String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public Resource loadAsResource(final String filename) {
    try {
      Path file = load(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        log.error("Could not read file: {}", filename);
        throw new StorageException("Could not read file: " + filename);
      }
    } catch (MalformedURLException ex) {
      //TODO: check how it looks like (if cause is also printed)
      log.error("While reading file: {}", ex);
      throw new StorageException("While reading file: " + filename, ex);
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }
}
