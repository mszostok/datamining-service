package com.mszostok.service;

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

  Resource loadTrainingFileAsResource(Integer id);

  Resource loadTestingFileAsResource(Integer id);
}
