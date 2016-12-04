package com.mszostok.web.dto;

import com.mszostok.domain.Competition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageCompetitionCollectionDto extends CompetitionCollectionDto {
  private String startDate;
  private String endDate;
  private Integer participationNumber;

  public ManageCompetitionCollectionDto(final Competition competition) {
    super(competition);
    this.startDate = competition.getStartDate().toString("MM/dd/yyyy HH:mm:ss");
    this.endDate = competition.getEndDate().toString("MM/dd/yyyy HH:mm:ss");
  }
}
