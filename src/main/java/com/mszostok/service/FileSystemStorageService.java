package com.mszostok.service;

import com.google.common.base.Charsets;
import com.mszostok.configuration.AppConfig;
import com.mszostok.enums.FileLogicType;
import com.mszostok.exception.StorageException;
import com.mszostok.exception.UploadException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Service("fileSystemStorageService")
public class FileSystemStorageService implements StorageService {
  public static final char CSV_SEPARATOR = ',';

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

  private Path createPathFor(final String... values) throws IOException {
    StringBuilder subPath = new StringBuilder();
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
    try {
      if (file.isEmpty()) {
        log.info("Was uploaded empty file {}", file.getOriginalFilename());
        throw new StorageException("Empty file " + file.getOriginalFilename());
      }

      Path dirTestingName = createPathFor(FileLogicType.TESTING.getName());
      Path dirKeyName = createPathFor(FileLogicType.KEY.getName());
      String testingFileUUID = UUID.randomUUID().toString();
      String keyFileUUID = UUID.randomUUID().toString();

      CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), Charsets.UTF_8), CSV_SEPARATOR);
      Writer keyWriter = new OutputStreamWriter(new FileOutputStream(dirKeyName.resolve(keyFileUUID).toFile()), Charsets.UTF_8);
      Writer testingWriter = new OutputStreamWriter(new FileOutputStream(dirTestingName.resolve(testingFileUUID).toFile()), Charsets.UTF_8);
      CSVWriter keyCSVWriter = new CSVWriter(keyWriter, CSV_SEPARATOR);
      CSVWriter testingCVSWriter = new CSVWriter(testingWriter, CSV_SEPARATOR);

      reader.forEach(line -> {
        testingCVSWriter.writeNext(Arrays.copyOfRange(line, 1, line.length));
        keyCSVWriter.writeNext(Arrays.copyOfRange(line, 0, 1));
      });

      testingCVSWriter.close();
      keyCSVWriter.close();

      uploadService.save(competitionId, file.getOriginalFilename(), FileLogicType.KEY, rootLocation.relativize(dirKeyName) + "/" + keyFileUUID);
      uploadService.save(competitionId, file.getOriginalFilename(), FileLogicType.TESTING, rootLocation.relativize(dirTestingName) + "/" + testingFileUUID);
    } catch (IOException ex) {
      log.error("While storing file: {}", ex);
      throw new StorageException("While storing file " + file.getOriginalFilename(), ex);
    }
  }

  @Override
  public void storeTrainingFile(final MultipartFile file, final Integer competitionId) {
    try {
      if (file.isEmpty()) {
        log.info("Was uploaded empty file {}", file.getOriginalFilename());
        throw new StorageException("Empty file " + file.getOriginalFilename());
      }

      Path dirName = createPathFor("training");
      String trainingFileUUID = UUID.randomUUID().toString();

      Files.copy(file.getInputStream(), dirName.resolve(trainingFileUUID));

      uploadService.save(competitionId, file.getOriginalFilename(), FileLogicType.TRAINING, rootLocation.relativize(dirName) + "/" + trainingFileUUID);
    } catch (IOException ex) {
      log.error("While storing file: {}", ex);
      throw new StorageException("While storing file " + file.getOriginalFilename(), ex);
    }
  }
  //
  //  @Override

  //  public Resource loadTrainingFileAsResource(final Integer competitionId) {
  //    return uploadService.getByCompetitionIdAndType(competitionId, FileLogicType.TRAINING)
  //      .map(upload -> loadAsResource(upload.getRefLink()))
  //      .orElseThrow(() -> {
  //        log.warn("Could not find trainig file for competition submission rank: {} ", competitionId);
  //        return new UploadException("Could not find testing file for competition");
  //      });
  //  }
  //
  //  @Override
  //  public Resource loadTestingFileAsResource(final Integer competitionId) {
  //    return uploadService.getByCompetitionIdAndType(competitionId, FileLogicType.TESTING)
  //      .map(upload -> loadAsResource(upload.getRefLink()))
  //      .orElseThrow(() -> {
  //        log.warn("Could not find testing file for competition submission rank: {} ", competitionId);
  //        return new UploadException("Could not find testing file for competition");
  //      });
  //  }

  @Override
  public Resource loadFileAsResource(final Integer competitionId, final FileLogicType type) {
    return uploadService.getByCompetitionIdAndType(competitionId, type)
      .map(upload -> loadAsResource(upload.getRefLink()))
      .orElseThrow(() -> {
        log.warn("Could not find {} file for competition submission id: {} ", type.getName(), competitionId);
        return new UploadException(String.format("Could not find %s file for this competition", type.getName()));
      });
  }

}
