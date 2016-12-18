package com.mszostok.web.rest;

import com.mszostok.configuration.JwtConfig;
import com.mszostok.configuration.SecurityConfig;
import com.mszostok.domain.User;
import com.mszostok.exception.InvalidJwtToken;
import com.mszostok.exception.UserNotFoundException;
import com.mszostok.model.UserCtx;
import com.mszostok.model.token.JwtToken;
import com.mszostok.model.token.RawAccessJwtToken;
import com.mszostok.model.token.RefreshToken;
import com.mszostok.security.jwt.JwtHeaderTokenExtractor;
import com.mszostok.service.UserService;
import com.mszostok.utils.JwtTokenFactory;
import com.mszostok.web.dto.TokenMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/auth")
@Api(value = "/auth", description = "Auth operations")
public class AuthResource {
  @Autowired
  private JwtHeaderTokenExtractor tokenExtractor;

  @Autowired
  private JwtConfig jwtSettings;

  @Autowired
  private JwtTokenFactory tokenFactory;

  @Autowired
  private UserService userService;

  @ApiOperation(value = "Get new token & refresh token")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something wrong in Server.")})
  @RequestMapping(value = "/token", method = GET, produces = APPLICATION_JSON_VALUE)
  public TokenMap refreshToken(@RequestHeader(SecurityConfig.JWT_TOKEN_HEADER_PARAM) final String tokenHeader) throws IOException, ServletException {
    String tokenPayload = tokenExtractor.extract(tokenHeader);

    RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
    RefreshToken receivedRefreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
          .orElseThrow(InvalidJwtToken::new);

    String subject = receivedRefreshToken.getSubject();
    User user = userService.getActiveUserByEmail(subject)
          .orElseThrow(() -> new UserNotFoundException(String.format("User %s not found", subject)));

    if (user.getRoles() == null) throw new InsufficientAuthenticationException("User has no roles assigned");

    List<GrantedAuthority> authorities = user.getRoles().stream()
          .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
          .collect(Collectors.toList());

    UserCtx userContext = new UserCtx(user.getEmail(), authorities);

    JwtToken accessToken = tokenFactory.createAccessToken(userContext);
    JwtToken refreshToken = tokenFactory.createRefreshToken(userContext);

    TokenMap tokenMap = new TokenMap();
    tokenMap.setToken(accessToken.getToken());
    tokenMap.setRefreshToken(refreshToken.getToken());
    return tokenMap;
  }

}
