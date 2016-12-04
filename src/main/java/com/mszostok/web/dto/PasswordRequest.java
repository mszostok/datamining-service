package com.mszostok.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class PasswordRequest {
  @NotEmpty
  private String oldPassword;
  @NotEmpty
  private String newPassword;
}
