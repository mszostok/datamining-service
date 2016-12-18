package com.mszostok.web.rest;


import com.mszostok.service.MailService;
import com.mszostok.web.dto.SendEmailDto;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Api(tags = "mailing : Operations about mailing", description = "Operations about mailing")
@RequestMapping("/emails")
public class MailResource {


  private final MailService mailService;

  @Autowired
  public MailResource(final MailService mailService) {
    Assert.notNull(mailService, "Mail Svc must not be null");
    this.mailService = mailService;
  }

  @ApiOperation(value = "Send email for all participants")
  @ApiResponses(value = {
        @ApiResponse(code = 202, message = "ACCEPTED"),
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @ResponseStatus(ACCEPTED)
  @RequestMapping(value = "/send-async", method = POST)
  public void sendEmailToParticipants(@Valid @RequestBody final SendEmailDto sendEmailDto) {
     mailService.sendEmailToParticipants(sendEmailDto);
  }

}
