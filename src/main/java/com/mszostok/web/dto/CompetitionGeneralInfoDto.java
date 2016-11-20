package com.mszostok.web.dto;

import com.mszostok.domain.Competition;
import com.mszostok.model.Leaderboard;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Setter
@Getter
public class CompetitionGeneralInfoDto {
  private Leaderboard leaderboard;
  private DateTime startDate;
  private DateTime endDate;
  private String author;

  public CompetitionGeneralInfoDto() {
  }

  public CompetitionGeneralInfoDto(final Competition competition) {
    this.startDate = competition.getStartDate();
    this.endDate = competition.getEndDate();
    this.author = competition.getUser().getUsername();
  }


}
