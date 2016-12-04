package com.mszostok.web.dto;

import com.mszostok.domain.Participation;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ParticipationDto {
  @NotNull
  private Integer competitionId;
  @NotEmpty
  private String competitionName;
  @NotNull
  private Double lastScore;
  @NotNull
  private Double bestScore;

  public ParticipationDto(final Participation participation) {
    this.competitionId = participation.getCompetition().getIdCompetition();
    this.competitionName = participation.getCompetition().getName();
    this.lastScore = participation.getLastScore();
    this.bestScore = participation.getBestScore();
  }
}
