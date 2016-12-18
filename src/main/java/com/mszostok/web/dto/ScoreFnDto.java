package com.mszostok.web.dto;

import com.mszostok.service.ScoreComputationService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreFnDto {
  private Integer id;
  private String name;

  public ScoreFnDto() {
  }

  public ScoreFnDto(final ScoreComputationService.ScoreFunctionType scoreFunctionType) {
    this.id = scoreFunctionType.getId();
    this.name = scoreFunctionType.getName();
  }
}
