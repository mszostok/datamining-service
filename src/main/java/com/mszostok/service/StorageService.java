package com.mszostok.service;

import com.mszostok.enums.FileLogicType;
import com.mszostok.model.StoredFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
  void init();

  Stream<Path> loadAll();

  void deleteAll();

  void storeTestingFile(MultipartFile file, Integer competitionId);

  void storeTrainingFile(MultipartFile file, Integer competitionId);

  StoredFile storeSubmissionFile(MultipartFile file);

  Resource loadFileAsResource(Integer competitionId, FileLogicType type);
}
