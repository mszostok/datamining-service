package com.mszostok.resource;

import com.mszostok.domain.User;
import com.mszostok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
@RequestMapping("/games")
public final class Root {

  @Autowired
  private UserService userService;

  @RequestMapping(value = "/")
  public Collection<User> index() {
    Collection<User> allActiveUsers = userService.getAllActiveUsers();

    return allActiveUsers;
  }
}
