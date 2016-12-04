package com.mszostok.web.rest;

import com.mszostok.domain.User;
import com.mszostok.repository.UserRepository;
import com.mszostok.service.MailService;
import com.mszostok.service.UserService;
import com.mszostok.web.dto.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Api(tags = "register : Operations about registering user", description = "Operations about registering user")
public class AccountResource {
  //TODO: remove repository
  private final UserRepository userRepository;

  private final UserService userService;

  private final MailService mailService;

  @Autowired
  public AccountResource(final MailService mailService, final UserService userService, final UserRepository userRepository) {
    Assert.notNull(mailService, "Mail Svc must not be null");
    Assert.notNull(userService, "User Svc must not be null");
    Assert.notNull(userRepository, "User Repository must not be null");
    this.mailService = mailService;
    this.userService = userService;
    this.userRepository = userRepository;
  }


  @ApiOperation(value = "Register new user")
  @ApiResponses(value = {
        @ApiResponse(code = 201, message = "User created successful"),
        @ApiResponse(code = 400, message = "When validating issues occurred, more information will be described in response json"),
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(value = "/register", produces = {APPLICATION_JSON_VALUE, TEXT_PLAIN_VALUE}, method = POST)
  public ResponseEntity<?> registerAccount(@Valid @RequestBody final UserDto userDto) {

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

    return userRepository.findOneByEmail(userDto.getEmail().toLowerCase())
      .map(user -> new ResponseEntity<>(Collections.singletonMap("email", "E-mail address already in use"), jsonHeaders, HttpStatus.BAD_REQUEST))
      .orElseGet(() ->
        userRepository.findOneByUsername(userDto.getUsername())
          .map(user -> new ResponseEntity<>(Collections.singletonMap("username", "Username already in use"), jsonHeaders, HttpStatus.BAD_REQUEST))
          .orElseGet(() -> {
            User user = userService.createSimpleUser(userDto);
            //TODO:
            String baseUrl = "template";
            //mailService.sendActivationEmail(user, baseUrl);
            return new ResponseEntity<>(HttpStatus.CREATED);
          })
      );
  }
}
