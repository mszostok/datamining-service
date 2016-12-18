package com.mszostok.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailDto {
  private Integer competitionId;
  private String body;
}
