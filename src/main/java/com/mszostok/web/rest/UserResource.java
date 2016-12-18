package com.mszostok.web.rest;

import com.mszostok.domain.User;
import com.mszostok.service.CompetitionService;
import com.mszostok.service.ParticipationService;
import com.mszostok.service.UserService;
import com.mszostok.utils.SecurityUtils;
import com.mszostok.web.dto.CompetitionCollectionDto;
import com.mszostok.web.dto.ManageActiveDto;
import com.mszostok.web.dto.ManageUserDto;
import com.mszostok.web.dto.ParticipationDto;
import com.mszostok.web.dto.PasswordRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@Api(tags = "user : Operations about user", description = "Operations about user")
//todo: /users
public class UserResource {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final ParticipationService participationService;

  private final CompetitionService competitionService;

  @Autowired
  public UserResource(final UserService userService, final PasswordEncoder passwordEncoder,
                      final ParticipationService participationService, final CompetitionService competitionService) {
    Assert.notNull(userService, "User Svc must not be null");
    Assert.notNull(passwordEncoder, "Password Encoder must not be null");
    Assert.notNull(participationService, "Participation Svc must not be null");
    Assert.notNull(competitionService, "Competition Svc must not be null");
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.participationService = participationService;
    this.competitionService = competitionService;
  }


  @ApiOperation(value = "Update profile")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Validation error, more information will be provided in sent json"),
      @ApiResponse(code = 500, message = "Something went wrong in Server")})
  @RequestMapping(value = "/profile", method = PUT, produces = {APPLICATION_JSON_VALUE, TEXT_PLAIN_VALUE})
  public ResponseEntity<?> updateProfile(@Valid @RequestBody final UserDto userDto) {

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);


    return SecurityUtils.getCurrentUserLogin().map(email -> userService.getActiveUserByEmail(email)
      .map(user -> {
        if (userService.isUsernameInUse(user, userDto)) {
          return new ResponseEntity<>(Collections.singletonMap("message", "Username already in use"), jsonHeaders, HttpStatus.BAD_REQUEST);
        }
        if (userService.isEmailInUse(user, userDto)) {
          return new ResponseEntity<>(Collections.singletonMap("message", "E-mail already in use"), jsonHeaders, HttpStatus.BAD_REQUEST);
        }
        User updated = userService.updateProfile(userDto);
        return new ResponseEntity<>(new UserDto(updated), HttpStatus.OK);
      })
      .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR))
    ).orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @ApiOperation(value = "Get profile", response = UserDto.class)
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")})
  @RequestMapping(value = "/profile", method = GET, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDto> getProfile() {

    return SecurityUtils.getCurrentUserLogin().map(email -> userService.getActiveUserByEmail(email)
      .map(user -> new ResponseEntity<>(new UserDto(user), HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR))
    ).orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @ApiOperation(value = "Change player password")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 400, message = "Validation error, more information will be provided in sent JSON"),
        @ApiResponse(code = 500, message = "Something wrong in Server")})
  @RequestMapping(value = "/change-password", method = PUT)
  public ResponseEntity<?> changePassForLoggedUser(@Valid @RequestBody final PasswordRequest passReq) {
    User user = userService.getCurrentLoggedUser();
    if (!passwordEncoder.matches(passReq.getOldPassword(), user.getPassword())) {
      return new ResponseEntity<>(Collections.singletonMap("error", "incorrect old password"), HttpStatus.BAD_REQUEST);
    }

    userService.changePassword(passReq.getNewPassword());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Get all competitions that user participate")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 400, message = "Validation error, more information will be provided in sent JSON"),
        @ApiResponse(code = 500, message = "Something wrong in Server")})
  @RequestMapping(value = "competitions/participation", method = GET)
  public Collection<ParticipationDto> getAllParticipation() {
    return participationService.getAllParticipationForLoggedUser();
  }

  @ApiOperation(value = "Get all created competitions name")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Something wrong in Server")})
  @RequestMapping(value = "competitions/created", method = GET)
  public Collection<CompetitionCollectionDto> getAllCreatedCompetitions() {
    return competitionService.getAllCreatedCompetitionsForLoggedUser();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @ApiOperation(value = "Get all users. You will need to have admin scope")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something wrong in Server")})
  @RequestMapping(value = "/users", method = GET)
  public Collection<ManageUserDto> getAllUsers() {
    return userService.getAllUsers().stream().map(ManageUserDto::new).collect(Collectors.toList());
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @ApiOperation(value = "Delete user by id. You will need to have admin scope")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something wrong in Server")})
  @ResponseStatus(NO_CONTENT)
  @RequestMapping(value = "/users/{id}", method = DELETE)
  public void deleteUser(@PathVariable("id") final Integer id) {
    userService.deleteUserById(id);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @ApiOperation(value = "Update isActive state for user. You will need to have admin scope")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something wrong in Server")})
  @ResponseStatus(NO_CONTENT)
  @RequestMapping(value = "/users/{id}/active", method = PUT)
  public void setActive(@RequestBody final ManageActiveDto manageActiveDto,
                        @PathVariable("id") final Integer id) {
    userService.setActiveById(id, manageActiveDto);
  }
}
