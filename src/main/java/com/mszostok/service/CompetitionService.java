package com.mszostok.service;

import com.google.common.base.Charsets;
import com.mszostok.domain.Competition;
import com.mszostok.domain.Participation;
import com.mszostok.domain.Submission;
import com.mszostok.domain.User;
import com.mszostok.enums.FileLogicType;
import com.mszostok.exception.CompetitionException;
import com.mszostok.exception.InternalException;
import com.mszostok.exception.ParticipationException;
import com.mszostok.exception.SubmissionException;
import com.mszostok.model.Member;
import com.mszostok.model.StoredFile;
import com.mszostok.repository.CompetitionRepository;
import com.mszostok.repository.ParticipationRepository;
import com.mszostok.repository.SubmissionRepository;
import com.mszostok.web.dto.CompetitionCollectionDto;
import com.mszostok.web.dto.CompetitionConfigureDto;
import com.mszostok.web.dto.CompetitionDto;
import com.mszostok.web.dto.CompetitionGeneralInfoDto;
import com.mszostok.web.dto.ManageCompetitionCollectionDto;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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
  public static final int FREQUENCY_OF_PARTICIPATION_IN_MIN = 1; //TODO: env + default

  @Autowired
  private DescriptionService descriptionService;

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

  @Autowired
  private SubmissionRepository submissionRepository;


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
    Competition competition = new Competition();
    competition.setName(competitionDto.getName());
    competition.setEndDate(competitionDto.getEndDate());
    competition.setStartDate(competitionDto.getStartDate());
    competition.setScoreFnId(competitionDto.getScoreFnId());
    competition.setShortDesc(Jsoup.clean(competitionDto.getShortDescription(), Whitelist.simpleText()));
    competition.setUser(userService.getCurrentLoggedUser());
    competition.setAllowParticipationFreqMin(competitionDto.getAllowParticipationFreqMin());
    competition.setDeleted(false);

    Competition savedCompetition = competitionRepository.save(competition);

    descriptionService.save(competitionDto, competition);

    return savedCompetition.getIdCompetition();
  }

  public Competition getActiveCompetitionById(final Integer competitionId) {
    DateTime now = new DateTime();
    return competitionRepository.findActiveByCompetitionId(competitionId, now)
      .orElseThrow(() -> new CompetitionException("Could not find active competition with id: " + competitionId));
  }


  private void checkIfUserCanParticipate(final Competition competition, final User user) {
    participationService.getLastTakeDate(competition.getIdCompetition(), user).ifPresent(LastTake -> {
          DateTime nextAvailablePart = LastTake.plusMinutes(competition.getAllowParticipationFreqMin());
          if (!nextAvailablePart.isBeforeNow()) {
            throw new ParticipationException("You can send next submission after: " + nextAvailablePart.toString("MM/dd/yyyy HH:mm:ss z"));
          }
        }
    );
  }

  private void saveSubmissionEntry(final MultipartFile file, final Participation participation, final Double actualScore) {
    StoredFile storedFile = storageService.storeSubmissionFile(file);

    Submission submission = new Submission();
    submission.setOriginalFileName(storedFile.getFilename());
    submission.setFileRefLink(storedFile.getRefLink());
    submission.setScore(actualScore);
    submission.setTakeDate(participation.getLastTakeDate());
    submission.setParticipation(participation);
    submissionRepository.save(submission);
  }

  public void processSubmission(final MultipartFile file, final Integer competitionId) {
    try {
      User user = userService.getCurrentLoggedUser();
      Competition competition = getActiveCompetitionById(competitionId);
      checkIfUserCanParticipate(competition, user);

      Resource keyFile = storageService.loadFileAsResource(competitionId, FileLogicType.KEY);

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
                          .withMetric(ScoreComputationService.ScoreFunctionType.find(competition.getScoreFnId()));

      Participation participation = participationService.saveOrUpdateParticipation(user, competitionId, actualScore);
      saveSubmissionEntry(file, participation, actualScore);
    } catch (IOException ex) {
      log.error("while processing submission with file: {} for competition: {}, :", file.getOriginalFilename(), competitionId, ex);
      throw new InternalException("Calculating score for sent submission has failed.");
    }
  }

  public Collection<CompetitionCollectionDto> getAllCreatedCompetitionsForLoggedUser() {
    return competitionRepository.findByUserAndDeletedFalse(userService.getCurrentLoggedUser()).stream()
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
    Competition competition = Optional.ofNullable(competitionRepository.findOne(id))
        .orElseThrow(() -> new CompetitionException(String.format("Competition with id: %s does not exits", id)));

    competition.setDeleted(true);
  }

  public CompetitionConfigureDto getConfigureParamsForCompetition(final Integer competitionId) {
    return Optional.ofNullable(competitionRepository.findOne(competitionId))
      .map(CompetitionConfigureDto::new)
      .orElseThrow(() -> new CompetitionException("Could not find competition with id: " + competitionId));
  }

  public void updateConfigureParams(final Integer competitionId, final CompetitionConfigureDto configureDto) {
    Competition competition = Optional.ofNullable(competitionRepository.findOne(competitionId))
        .orElseThrow(() -> new CompetitionException("Could not find competition with id: " + competitionId));

    competition.setName(configureDto.getCompetitionName());
    competition.setStartDate(configureDto.getStartDate());
    competition.setEndDate(configureDto.getEndDate());
    competition.setAllowParticipationFreqMin(configureDto.getAllowParticipationFreqMin());

    competition.setShortDesc(Jsoup.clean(configureDto.getShortDescription(), Whitelist.simpleText()));
  }
}
