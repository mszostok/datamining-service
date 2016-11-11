package com.mszostok.web.rest;

import com.mszostok.domain.User;
import com.mszostok.repository.UserRepository;
import com.mszostok.service.MailService;
import com.mszostok.service.UserService;
import com.mszostok.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class AccountResource {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private MailService mailService;


  @RequestMapping(value = "/register", method = POST, produces = {APPLICATION_JSON_VALUE, TEXT_PLAIN_VALUE})
  public ResponseEntity<?> registerAccount(@Valid @RequestBody final UserDto userDto, final HttpServletRequest request) {

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

    return userRepository.findOneByEmail(userDto.getEmail().toLowerCase())
      .map(user -> new ResponseEntity<>(Collections.singletonMap("message", "E-mail address already in use"), jsonHeaders, HttpStatus.BAD_REQUEST))
      .orElseGet(() ->
        userRepository.findOneByUsername(userDto.getUsername())
          .map(user -> new ResponseEntity<>(Collections.singletonMap("message", "Username already in use"), jsonHeaders, HttpStatus.BAD_REQUEST))
          .orElseGet(() -> {
            User user = userService.createSimpleUser(userDto);
            //TODO:
            String baseUrl = "template";
            mailService.sendActivationEmail(user, baseUrl);
            return new ResponseEntity<>(HttpStatus.CREATED);
          })
      );
  }
}
