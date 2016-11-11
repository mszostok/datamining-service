package com.mszostok.web.rest;

import com.mszostok.service.MailService;
import com.mszostok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public final class RootResource {

  @Autowired
  private UserService userService;
  @Autowired
  private MailService mailService;

  @RequestMapping(value = "/dashboard", produces = "application/json", method = RequestMethod.GET)
  public ResponseEntity<String> bar() {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>("{\"message\": \"Welcome in dashboard \"}", httpHeaders, HttpStatus.OK);
  }
}
