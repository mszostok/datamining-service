package com.mszostok.web.dto;

import com.mszostok.domain.Competition;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;

@Getter
@Setter
public class CompetitionConfigureDto  {
  private DateTime startDate;
  private DateTime endDate;
  private String shortDescription;
  private String competitionName;
  @Min(0)
  private Integer allowParticipationFreqMin;

  public CompetitionConfigureDto() {
  }

  public CompetitionConfigureDto(final Competition competition) {
    this.startDate = competition.getStartDate();
    this.endDate = competition.getEndDate();
    this.competitionName  = competition.getName();
    this.shortDescription = competition.getShortDesc();
    this.allowParticipationFreqMin = competition.getAllowParticipationFreqMin();
  }
}
