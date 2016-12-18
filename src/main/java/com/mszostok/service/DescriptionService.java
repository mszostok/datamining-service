package com.mszostok.service;

import com.mszostok.domain.Competition;
import com.mszostok.domain.Description;
import com.mszostok.exception.DescriptionException;
import com.mszostok.repository.DescriptionRepository;
import com.mszostok.web.dto.CompetitionDto;
import com.mszostok.web.dto.DescriptionDto;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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

  public Description save(final CompetitionDto competitionDto, final Competition competition) {
    Description description = new Description();
    description.setIntroduction(htmlCleaner(competitionDto.getIntroductionDescription()));
    description.setFormula(htmlCleaner(competitionDto.getFormulaDescription()));
    description.setDataset(htmlCleaner(competitionDto.getDatasetDescription()));
    description.setCompetition(competition);

    return descriptionRepository.save(description);
  }

  public static String htmlCleaner(final String htmlBody) {
    return Jsoup.clean(htmlBody, "http://www.com", Whitelist.relaxed().preserveRelativeLinks(true));
  }

  public void updateIntroductionForCompetition(final Integer competitionId, final DescriptionDto descriptionDto) {
    Description description = descriptionRepository.findByCompetition_IdCompetition(competitionId)
      .orElseThrow(() -> new DescriptionException("Cannot find descriptions for requested competition"));

    description.setIntroduction(htmlCleaner(descriptionDto.getBody()));
  }

  public void updateFormulaForCompetition(final Integer competitionId, final DescriptionDto descriptionDto) {
    Description description = descriptionRepository.findByCompetition_IdCompetition(competitionId)
      .orElseThrow(() -> new DescriptionException("Cannot find descriptions for requested competition"));

    description.setFormula(htmlCleaner(descriptionDto.getBody()));
  }

  public void updateDatasetForCompetition(final Integer competitionId, final DescriptionDto descriptionDto) {
    Description description = descriptionRepository.findByCompetition_IdCompetition(competitionId)
      .orElseThrow(() -> new DescriptionException("Cannot find descriptions for requested competition"));

    description.setDataset(htmlCleaner(descriptionDto.getBody()));
  }

}
