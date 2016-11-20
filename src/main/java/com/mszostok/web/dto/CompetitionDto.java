package com.mszostok.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CompetitionDto {
  @NotEmpty
  @Size(max = 50)
  private String name;
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private DateTime startDate;
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private DateTime endDate;
  @NotEmpty
  private String shortDescription;
  @NotEmpty
  private String introductionDescription;
  @NotEmpty
  private String formulaDescription;
  @NotEmpty
  private String datasetDescription;
}
