package com.mszostok.repository;

import com.mszostok.domain.Competition;
import com.mszostok.domain.User;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
  @Query("select c from Competition c where c.deleted = false and c.startDate <= :time and c.endDate >= :time")
  Collection<Competition> findAllActive(@Param("time") DateTime time);

  Collection<Competition> findByUserAndDeletedFalse(User currentLoggedUser);


  @Query("select c from Competition c where c.deleted = false and c.startDate <= :time and c.endDate >= :time and c.idCompetition = :id")
  Optional<Competition> findActiveByCompetitionId(@Param("id") Integer competitionId, @Param("time") DateTime time);
}
