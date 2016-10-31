package com.mszostok.utils;

import com.mszostok.Fast;
import com.mszostok.model.UserCtx;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Category(Fast.class)
public class SecurityUtilsTest {

  @Test
  public void testGetCurrentUserLoginWhenUserIsLoggedWithCtx() {
    // given
    Optional<String> expectedLogin = Optional.of("email");
    List<GrantedAuthority> authorities = Stream.of("xyz", "abc")
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());
    UserCtx dummyUserCtx = new UserCtx("email", authorities);

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(dummyUserCtx, null, dummyUserCtx.getAuthorities()));
    SecurityContextHolder.setContext(securityContext);

    // when
    Optional<String> login = SecurityUtils.getCurrentUserLogin();

    // then
    assertThat(login).isPresent().isEqualTo(expectedLogin);
  }

  @Test
  public void testGetCurrentUserLoginWhenUserIsLogged() {
    // given
    Optional<String> expectedLogin = Optional.of("email");

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("email", "user"));
    SecurityContextHolder.setContext(securityContext);

    // when
    Optional<String> login = SecurityUtils.getCurrentUserLogin();

    // then
    assertThat(login).isPresent().isEqualTo(expectedLogin);
  }

  @Test
  public void testGetCurrentUserLoginWhenUserIsNotLogged() {
    // given
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(securityContext);

    // when
    Optional<String> login = SecurityUtils.getCurrentUserLogin();

    // then
    assertThat(login).isEmpty();
  }

  @Test
  public void testUtilityClassWellDefined() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    // given
    Constructor<SecurityUtils> constructor = SecurityUtils.class.getDeclaredConstructor();

    //when

    // then
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}