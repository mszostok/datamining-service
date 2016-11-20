package com.mszostok.service;

import com.mszostok.domain.Competition;
import com.mszostok.domain.Description;
import com.mszostok.exception.CompetitionException;
import com.mszostok.model.Leaderboard;
import com.mszostok.repository.CompetitionRepository;
import com.mszostok.repository.DescriptionRepository;
import com.mszostok.web.dto.CompetitionCollectionDto;
import com.mszostok.web.dto.CompetitionDto;
import com.mszostok.web.dto.CompetitionGeneralInfoDto;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompetitionService {
  @Autowired
  private DescriptionRepository descriptionRepository;

  @Autowired
  private CompetitionRepository competitionRepository;

  @Autowired
  private DescriptionService descriptionSvc;

  @Autowired
  private UserService userService;

  public Collection<CompetitionCollectionDto> getAllActiveCompetitions() {
    DateTime now = new DateTime();
    Collection<Competition> actives = competitionRepository.findAllActive(now);

    return actives.stream()
      .map(CompetitionCollectionDto::new)
      .collect(Collectors.toList());
  }

  public CompetitionGeneralInfoDto getGeneralInfoFor(final Integer competitionId) {
    return Optional.ofNullable(competitionRepository.findOne(competitionId))
      .map(CompetitionGeneralInfoDto::new)
      .map(generalInfo -> {
        generalInfo.setLeaderboard(new Leaderboard());
        return generalInfo;
      })
      .orElseThrow(() -> new CompetitionException("Could not find competition with id: " + competitionId));
  }

  public Integer save(final CompetitionDto competitionDto) {
    Competition competition = new Competition();
    competition.setName(competitionDto.getName());
    competition.setStartDate(competitionDto.getStartDate());
    competition.setEndDate(competitionDto.getEndDate());
    competition.setShortDesc(competitionDto.getShortDescription());
    competition.setUser(userService.getCurrentLoggedUser());
    Competition savedCompetition = competitionRepository.save(competition);

    Description description = new Description();
    description.setIntroduction(competitionDto.getIntroductionDescription());
    description.setFormula(competitionDto.getFormulaDescription());
    description.setDataset(competitionDto.getDatasetDescription());
    description.setCompetition(savedCompetition);
    descriptionRepository.save(description);

    return savedCompetition.getIdCompetition();
  }

  public Optional<Competition> getCompetition(final Integer competitionId) {
    return Optional.ofNullable(competitionRepository.findOne(competitionId));
  }
}
