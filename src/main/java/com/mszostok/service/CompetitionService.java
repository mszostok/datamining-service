package com.mszostok.service;

import com.google.common.base.Charsets;
import com.mszostok.domain.Competition;
import com.mszostok.domain.Description;
import com.mszostok.domain.Participation;
import com.mszostok.domain.User;
import com.mszostok.enums.FileLogicType;
import com.mszostok.exception.CompetitionException;
import com.mszostok.exception.InternalException;
import com.mszostok.exception.ParticipationException;
import com.mszostok.exception.SubmissionException;
import com.mszostok.model.Member;
import com.mszostok.repository.CompetitionRepository;
import com.mszostok.repository.DescriptionRepository;
import com.mszostok.repository.ParticipationRepository;
import com.mszostok.web.dto.CompetitionCollectionDto;
import com.mszostok.web.dto.CompetitionDto;
import com.mszostok.web.dto.CompetitionGeneralInfoDto;
import com.mszostok.web.dto.ManageCompetitionCollectionDto;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mszostok.service.ScoreComputationService.scoreFor;

@Slf4j
@Service
@Transactional
public class CompetitionService {
  public static final char CSV_SEPARATOR = ',';
  public static final int MAX_LEADERBOARD_SIZE = 10;
  public static final int FREQUENCY_OF_PARTICIPATION_IN_MIN = 10; //TODO: env + default

  @Autowired
  private DescriptionRepository descriptionRepository;

  @Autowired
  private CompetitionRepository competitionRepository;

  @Autowired
  private StorageService storageService;

  @Autowired
  private UserService userService;

  @Autowired
  private ParticipationService participationService;

  @Autowired
  private ParticipationRepository participationRepository;

  public Collection<CompetitionCollectionDto> getAllActiveCompetitions() {
    DateTime now = new DateTime();
    Collection<Competition> actives = competitionRepository.findAllActive(now);

    return actives.stream()
      .map(CompetitionCollectionDto::new)
      .collect(Collectors.toList());
  }

  public CompetitionGeneralInfoDto getGeneralInfoFor(final Integer idCompetition) {
    return Optional.ofNullable(competitionRepository.findOne(idCompetition))
      .map(CompetitionGeneralInfoDto::new)
      .map(generalInfo -> {
        PageRequest pageRequest = new PageRequest(0, MAX_LEADERBOARD_SIZE, Sort.Direction.DESC, "bestScore");
        Page<Participation> topParticipation = participationRepository.findByCompetition_IdCompetition(idCompetition, pageRequest);

        AtomicInteger idx = new AtomicInteger();
        List<Member> leaderboard = new LinkedList<>();
        topParticipation.forEach(p -> leaderboard.add(new Member(idx.incrementAndGet(), p.getUser().getUsername())));

        generalInfo.setLeaderboard(leaderboard);
        return generalInfo;
      })
      .orElseThrow(() -> new CompetitionException("Could not find competition with id: " + idCompetition));
  }

  public Integer save(final CompetitionDto competitionDto) {
    //TODO: remove html
    Competition competition = new Competition();
    competition.setName(competitionDto.getName());
    competition.setEndDate(competitionDto.getEndDate());
    competition.setStartDate(competitionDto.getStartDate());
    competition.setScoreFnId(competitionDto.getScoreFnId());
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

  public Competition getActiveCompetitionById(final Integer competitionId) {
    DateTime now = new DateTime();
    return competitionRepository.findActiveByCompetitionId(competitionId, now)
      .orElseThrow(() -> new CompetitionException("Could not find active competition with id: " + competitionId));
  }


  private void checkIfUserCanParticipate(final Integer competitionId, final User user) {
    participationService.getLastTakeDate(competitionId, user).ifPresent(LastTake -> {
        DateTime nextAvailablePart = LastTake.plusMinutes(FREQUENCY_OF_PARTICIPATION_IN_MIN);
        if (!nextAvailablePart.isBeforeNow()) {
          throw new ParticipationException("You can send next submission after: " + nextAvailablePart.toString("MM/dd/yyyy HH:mm:ss z"));
        }
      }
    );
  }
  public void processSubmission(final MultipartFile file, final Integer competitionId) {
    try {
      User user = userService.getCurrentLoggedUser();
      checkIfUserCanParticipate(competitionId, user);

      Resource keyFile = storageService.loadFileAsResource(competitionId, FileLogicType.KEY);
      Integer scoreFnId = getActiveCompetitionById(competitionId).getScoreFnId();

      CSVReader submissionCSVFile = new CSVReader(new InputStreamReader(file.getInputStream(), Charsets.UTF_8), CSV_SEPARATOR);
      CSVReader keyCSVFile = new CSVReader(new InputStreamReader(keyFile.getInputStream(), Charsets.UTF_8), CSV_SEPARATOR);

      List<String[]> keyList = keyCSVFile.readAll();
      List<String[]> submissionList = submissionCSVFile.readAll();


      if (keyList.size() != submissionList.size()) {
        log.info("User sent incorrect submission file");
        throw new SubmissionException("Number of row lines are not equals");
      }

      Double actualScore = scoreFor()
                          .submission(submissionList)
                          .and().key(keyList)
                          .withMetric(ScoreComputationService.ScoreFunctionType.find(scoreFnId));

      participationService.updateParticipation(user, competitionId, actualScore);
    } catch (IOException ex) {
      log.error("while processing submission with file: {} for competition: {}, :", file.getOriginalFilename(), competitionId, ex);
      throw new InternalException("Calculating score for sent submission has failed.");
    }
  }

  public Collection<CompetitionCollectionDto> getAllCreatedCompetitionsForLoggedUser() {
    return competitionRepository.findByUser(userService.getCurrentLoggedUser()).stream()
      .map(CompetitionCollectionDto::new)
      .collect(Collectors.toList());
  }

  public Collection<ManageCompetitionCollectionDto> getAllCompetitions() {
    Collection<Competition> competitions = competitionRepository.findAll();

    return competitions.stream()
      .map(ManageCompetitionCollectionDto::new)
      .collect(Collectors.toList());
  }

  public void deleteCompetition(final Integer id) {
    if (competitionRepository.findOne(id) == null) {
      throw new CompetitionException(String.format("Competition with id: %s does not exits", id));
    }
    competitionRepository.delete(id);
  }
}
