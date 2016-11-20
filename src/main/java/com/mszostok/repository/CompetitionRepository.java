package com.mszostok.repository;

import com.mszostok.domain.Competition;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
  @Query("select c from Competition c where c.startDate <= :time and c.endDate >= :time")
  Collection<Competition> findAllActive(@Param("time") DateTime time);
}
