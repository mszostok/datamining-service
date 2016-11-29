package com.mszostok.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Member {
  @NotNull
  private Integer rank;
  @NotEmpty
  private String username;


  public Member(final int rank, final String username) {
    this.rank = rank;
    this.username = username;
  }
}
