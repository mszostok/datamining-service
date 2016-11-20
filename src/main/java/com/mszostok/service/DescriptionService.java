package com.mszostok.service;

import com.mszostok.domain.Description;
import com.mszostok.exception.DescriptionException;
import com.mszostok.repository.DescriptionRepository;
import com.mszostok.web.dto.CompetitionDto;
import javassist.runtime.Desc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DescriptionService {

  @Autowired
  private DescriptionRepository descriptionRepository;

  public String getIntroductionFor(final Integer competitionId) {
    return descriptionRepository.findByCompetition_IdCompetition(competitionId)
      .orElseThrow(() -> new DescriptionException("Could not find description for requested competition id: " + competitionId))
      .getIntroduction();
  }

  public String getFormulaFor(final Integer competitionId) {
    return descriptionRepository.findByCompetition_IdCompetition(competitionId)
      .orElseThrow(() -> new DescriptionException("Could not find description for requested competition id: " + competitionId))
      .getFormula();
  }

  public String getDatasetFor(final Integer competitionId) {
    return descriptionRepository.findByCompetition_IdCompetition(competitionId)
      .orElseThrow(() -> new DescriptionException("Could not find description for requested competition id: " + competitionId))
      .getDataset();
  }

  public Description save(final CompetitionDto competitionDto) {
    Description description = new Description();
    description.setIntroduction(competitionDto.getIntroductionDescription());
    description.setFormula(competitionDto.getFormulaDescription());
    description.setDataset(competitionDto.getDatasetDescription());

    return descriptionRepository.save(description);
  }
}
