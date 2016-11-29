package com.mszostok.web.dto;

import com.mszostok.domain.Competition;
import com.mszostok.model.Member;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;

@Setter
@Getter
public class CompetitionGeneralInfoDto {
  private List<Member> leaderboard;
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
