package com.mszostok.web.dto;


import com.mszostok.domain.Competition;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CompetitionCollectionDto {

  @NotNull
  private int id;
  @NotEmpty
  private String name;
  @NotEmpty
  private String shortDescription;
  @NotEmpty
  private String author;

  public CompetitionCollectionDto(final Competition competition) {
    this.id = competition.getIdCompetition();
    this.name = competition.getName();
    this.shortDescription = competition.getShortDesc();
    this.author = competition.getUser().getUsername();
  }

  public CompetitionCollectionDto() {
  }
}
