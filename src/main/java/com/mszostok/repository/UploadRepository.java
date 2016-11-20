package com.mszostok.repository;

import com.mszostok.domain.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadRepository extends JpaRepository<Upload, Integer> {
  Optional<Upload> findByCompetition_IdCompetitionAndLogicType(Integer competitionId, String logicType);
}
