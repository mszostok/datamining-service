package com.mszostok.service;

import com.mszostok.domain.Participation;
import com.mszostok.domain.User;
import com.mszostok.repository.ParticipationRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {

  @Autowired
  private ParticipationRepository participationRepository;

  @Autowired
  private CompetitionService competitionService;

  public Participation saveParticipation(final User user, final Integer competitionId, final Double score) {
    return participationRepository.findOneByCompetition_IdCompetitionAndUser_IdUser(competitionId, user.getIdUser())
        .map(p -> {
          if (score > p.getBestScore())
            p.setBestScore(score);

          p.setTakeNumber(p.getTakeNumber() + 1);
          p.setLastTakeDate(DateTime.now());
          p.setLastScore(score);
          return p;
        })
        .orElseGet(() -> {
          Participation participation = new Participation();

          participation.setCompetition(competitionService.getCompetition(competitionId));
          participation.setUser(user);
          participation.setLastTakeDate(DateTime.now());
          participation.setLastScore(score);
          participation.setBestScore(score);
          participation.setTakeNumber(1);

          return participationRepository.save(participation);
        });
  }

}
