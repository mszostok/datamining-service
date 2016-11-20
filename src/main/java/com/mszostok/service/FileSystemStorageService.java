package com.mszostok.service;

import com.mszostok.configuration.AppConfig;
import com.mszostok.exception.CompetitionException;
import com.mszostok.exception.StorageException;
import com.mszostok.exception.UploadException;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Service("fileSystemStorageService")
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;

  @Autowired
  private UploadService uploadService;

  private Function<YearMonth, String> getPathFromYearMonth = yearMonth -> {
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .appendPattern("/yyyy/MM/")
      .toFormatter(Locale.ENGLISH);
    return yearMonth.format(formatter);
  };

  @Autowired
  public FileSystemStorageService(final AppConfig appConfig) {
    this.rootLocation = Paths.get(appConfig.getStorage().getUploadLocation());
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

  private Path createPath(final String... values) throws IOException {
    StringBuffer subPath = new StringBuffer();
    for (final String s : values) {
      subPath.append(s).append("/");
    }
    String directoryDate = getPathFromYearMonth.apply(YearMonth.now());
    Path dirName = rootLocation.resolve(subPath.toString() + directoryDate);
    if (!Files.exists(dirName)) {
      Files.createDirectories(dirName);
    }

    return dirName;
  }

  private void store(final String type, final MultipartFile file, final Integer competitionId) throws StorageException {
    try {
      if (file.isEmpty()) {
        log.info("Was uploaded empty file {}", file.getOriginalFilename());
        throw new StorageException("Empty file " + file.getOriginalFilename());
      }

      Path dirName = createPath(type);
      String uuidFilename = UUID.randomUUID().toString();

      uploadService.save(competitionId, file.getOriginalFilename(), type, rootLocation.relativize(dirName) + "/" + uuidFilename);

      Files.copy(file.getInputStream(), dirName.resolve(uuidFilename));

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

  private Resource loadAsResource(final String filename) {
    try {
      Path file = rootLocation.resolve(filename);
      log.info("file path: {}", file);
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

  @Override
  public void storeTestingFile(final MultipartFile file, final Integer competitionId) {
    store("testing", file, competitionId);
  }

  @Override
  public void storeTrainingFile(final MultipartFile file, final Integer competitionId) {
    store("training", file, competitionId);
  }

  @Override
  public Resource loadTrainingFileAsResource(final Integer uploadId) {
    return uploadService.getByCompetitionIdAndType(uploadId, "training")
      .map(upload -> loadAsResource(upload.getRefLink()))
      .orElseThrow(() -> new CompetitionException("Could not find competition with id: " + uploadId));
  }

  @Override
  public Resource loadTestingFileAsResource(final Integer uploadId) {
    return uploadService.getByCompetitionIdAndType(uploadId, "testing")
      .map(upload -> loadAsResource(upload.getRefLink()))
      .orElseThrow(() -> new UploadException("Could not find upload with id: " + uploadId));
  }
}
