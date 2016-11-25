package com.mszostok.repository;

import com.mszostok.domain.Participation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
  Optional<Participation> findOneByCompetition_IdCompetitionAndUser_IdUser(Integer idCompetition, Integer idUser);


  Page<Participation> findByCompetition_IdCompetition(Integer idCompetition, Pageable pageable);
}
