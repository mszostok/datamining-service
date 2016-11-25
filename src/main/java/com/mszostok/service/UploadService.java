package com.mszostok.service;

import com.mszostok.domain.Competition;
import com.mszostok.domain.Upload;
import com.mszostok.enums.FileLogicType;
import com.mszostok.exception.CompetitionException;
import com.mszostok.repository.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UploadService {

  @Autowired
  private UploadRepository uploadRepository;

  @Autowired
  private UserService userService;


  @Autowired
  private CompetitionService competitionService;

  public Upload save(final Integer competitionId, final String filename, final FileLogicType type,
                     final String refLink) throws CompetitionException {
    Competition competition = competitionService.getCompetition(competitionId);

    if (!competition.getUser().equals(userService.getCurrentLoggedUser())) {
      throw new CompetitionException("Logged user is not owner of competition: " + competition.getName());
    }
    Upload upload = new Upload();

    upload.setCompetition(competition);
    upload.setOriginalFileName(filename);
    upload.setLogicType(type.getName());
    upload.setRefLink(refLink);

    return uploadRepository.save(upload);
  }

  public Optional<Upload> getByCompetitionIdAndType(final Integer id, final FileLogicType type) {
    return uploadRepository.findByCompetition_IdCompetitionAndLogicType(id, type.getName());
  }
}
