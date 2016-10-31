package com.mszostok.utils;

import com.mszostok.Fast;
import com.mszostok.configuration.JwtConfig;
import com.mszostok.model.UserCtx;
import com.mszostok.model.token.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.thymeleaf.TemplateEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Category(Fast.class)
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class JwtTokenFactoryTest {

  private final DateTime date = new LocalDate().plusMonths(1).toDateTimeAtStartOfDay();

  @InjectMocks
  private JwtTokenFactory jwtTokenFactory = new JwtTokenFactory();

  @Mock
  private JwtConfig jwtConfigMock;

  @Mock
  private MessageSource messageSource;

  @Mock
  private TemplateEngine templateEngineMock;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    DateTimeUtils.setCurrentMillisFixed(date.getMillis());
  }

  @After
  public void tearDown() throws Exception {
    DateTimeUtils.setCurrentMillisSystem();
  }

  @Test
  public void testCreateAccessToken() throws Exception {
    // given
    Date expectedTokenExpiration = date.plusMinutes(10).toDate();
    List<String> expectedScopes = Arrays.asList("xyz", "abc");

    given(jwtConfigMock.getTokenExpirationTime()).willReturn(10);
    given(jwtConfigMock.getTokenIssuer()).willReturn("issuer");
    given(jwtConfigMock.getTokenSigningKey()).willReturn("signKey");

    List<GrantedAuthority> authorities = expectedScopes.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    UserCtx userCtx = new UserCtx("email", authorities);

    // when
    JwtToken accessToken = jwtTokenFactory.createAccessToken(userCtx);

    // then
    verify(jwtConfigMock, times(1)).getTokenIssuer();
    verify(jwtConfigMock, times(1)).getTokenSigningKey();
    verify(jwtConfigMock, times(1)).getTokenExpirationTime();
    Claims claims = Jwts.parser().setSigningKey("signKey")
        .parseClaimsJws(accessToken.getToken()).getBody();
    List scopes = claims.get("scopes", List.class);

    assertThat(scopes).isEqualTo(expectedScopes);
    assertThat(claims.getSubject()).isEqualTo("email");
    assertThat(claims.getIssuer()).isEqualTo("issuer");
    assertThat(claims.getExpiration()).isEqualTo(expectedTokenExpiration);
    assertThat(claims.getIssuedAt()).isEqualTo(date.toDate());
  }

  @Test
  public void testCreateAccessTokenWithEmptyScopes() throws Exception {
    // given
    UserCtx userCtnWithoutScopes = new UserCtx("email", new LinkedList<>());

    // when
    try {
      jwtTokenFactory.createAccessToken(userCtnWithoutScopes);

      // then
      Assert.fail("Expected an IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException ex) {
      assertThat(ex.getMessage()).isEqualTo("User doesn't have any privileges");
    }
  }


  @Test
  public void testCreateRefreshToken() throws Exception {
    // given
    Date expectedTokenExpiration = date.plusMinutes(10).toDate();
    List<String> expectedScope = Collections.singletonList("ROLE_REFRESH_TOKEN");

    given(jwtConfigMock.getRefreshTokenExpTime()).willReturn(10);
    given(jwtConfigMock.getTokenExpirationTime()).willReturn(10);
    given(jwtConfigMock.getTokenIssuer()).willReturn("issuer");
    given(jwtConfigMock.getTokenSigningKey()).willReturn("signKey");

    UserCtx userCtnWithoutScopes = new UserCtx("email", new LinkedList<>());

    // when
    JwtToken refreshToken = jwtTokenFactory.createRefreshToken(userCtnWithoutScopes);

    // then
    Claims claims = Jwts.parser().setSigningKey("signKey")
      .parseClaimsJws(refreshToken.getToken()).getBody();
    List scopes = claims.get("scopes", List.class);

    assertThat(scopes).isEqualTo(expectedScope);
    assertThat(claims.getSubject()).isEqualTo("email");
    assertThat(claims.getIssuer()).isEqualTo("issuer");
    assertThat(claims.getExpiration()).isEqualTo(expectedTokenExpiration);
    assertThat(claims.getIssuedAt()).isEqualTo(date.toDate());
  }

  @Test
  public void textCreateRefreshTokenWhenScopesAreProvided() throws Exception {
    // given
    Date expectedTokenExpiration = date.plusMinutes(10).toDate();
    List<String> expectedScope = Collections.singletonList("ROLE_REFRESH_TOKEN");

    given(jwtConfigMock.getRefreshTokenExpTime()).willReturn(10);
    given(jwtConfigMock.getTokenIssuer()).willReturn("issuer");
    given(jwtConfigMock.getTokenSigningKey()).willReturn("signKey");


    List<GrantedAuthority> authorities = Stream.of("xyz", "abc")
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    UserCtx userCtnWithScopes = new UserCtx("email", authorities);

    // when
    JwtToken refreshToken = jwtTokenFactory.createRefreshToken(userCtnWithScopes);

    // then
    verify(jwtConfigMock, times(1)).getTokenIssuer();
    verify(jwtConfigMock, times(1)).getTokenSigningKey();
    verify(jwtConfigMock, times(1)).getRefreshTokenExpTime();

    Claims claims = Jwts.parser().setSigningKey("signKey")
      .parseClaimsJws(refreshToken.getToken()).getBody();
    List scopes = claims.get("scopes", List.class);

    assertThat(scopes).isEqualTo(expectedScope);
    assertThat(claims.getSubject()).isEqualTo("email");
    assertThat(claims.getIssuer()).isEqualTo("issuer");
    assertThat(claims.getExpiration()).isEqualTo(expectedTokenExpiration);
    assertThat(claims.getIssuedAt()).isEqualTo(date.toDate());
  }
}
