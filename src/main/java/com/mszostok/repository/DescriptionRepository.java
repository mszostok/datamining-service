package com.mszostok.repository;

import com.mszostok.domain.Description;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DescriptionRepository extends JpaRepository<Description, Integer> {
  Optional<Description> findByCompetition_IdCompetition(Integer competitionId);
}
