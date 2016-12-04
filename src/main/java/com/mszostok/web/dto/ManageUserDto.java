package com.mszostok.web.dto;

import com.mszostok.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageUserDto extends UserDto {
  private Integer id;

  public ManageUserDto(final User user) {
    super(user);
    this.id = user.getIdUser();
  }
}
