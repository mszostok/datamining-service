package com.mszostok.service;

import com.mszostok.domain.Upload;
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

  public Upload save(final Integer competitionId, final String filename, final String logicType,
                     final String refLink) throws CompetitionException {
    return competitionService.getCompetition(competitionId)
      .map(competition -> {
        if (!competition.getUser().equals(userService.getCurrentLoggedUser())) {
          throw new CompetitionException("Logged user is not owner of competition: " + competition.getName());
        }
        Upload upload = new Upload();

        upload.setCompetition(competition);
        upload.setOriginalFileName(filename);
        upload.setLogicType(logicType);
        upload.setRefLink(refLink);

        return uploadRepository.save(upload);
      }).orElseThrow(() -> new CompetitionException("Could not find competition with id: " + competitionId));
  }

  public Optional<Upload> getByCompetitionIdAndType(final Integer id, final String type) {
    return uploadRepository.findByCompetition_IdCompetitionAndLogicType(id, type);
  }
}
