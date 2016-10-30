package com.mszostok.configuration;

import com.mszostok.security.RestUnauthorizedEntryPoint;
import com.mszostok.security.ajax.AjaxAuthenticationProvider;
import com.mszostok.security.ajax.AjaxLoginProcessingFilter;
import com.mszostok.security.jwt.JwtAuthenticationProvider;
import com.mszostok.security.jwt.JwtTokenAuthenticationProcessingFilter;
import com.mszostok.security.jwt.SkipPathRequestMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  public static final String JWT_TOKEN_HEADER_PARAM = "Authorization";
  public static final String LOGIN_ENTRY_POINT = "/auth/login";
  public static final String TOKEN_REFRESH_ENTRY_POINT = "/auth/token";
  public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/**";

  @Autowired
  private RestUnauthorizedEntryPoint authenticationEntryPoint;
  @Autowired
  private AjaxAuthenticationProvider ajaxAuthenticationProvider;
  @Autowired
  private JwtAuthenticationProvider jwtAuthenticationProvider;
  @Autowired
  private AuthenticationManager authenticationManager;

  @Bean
  protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter() throws Exception {
    AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(LOGIN_ENTRY_POINT);
    filter.setAuthenticationManager(authenticationManager);
    return filter;
  }

  @Bean
  protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
    List<String> pathsToSkip = Arrays.asList(TOKEN_REFRESH_ENTRY_POINT);
    log.info("Path skipped from authorization: {}", pathsToSkip);

    SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, TOKEN_BASED_AUTH_ENTRY_POINT);
    JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(matcher);
    filter.setAuthenticationManager(authenticationManager);
    return filter;
  }

  @Bean
  protected BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected final void configure(final HttpSecurity http) throws Exception {
    http
      .csrf().disable() // disabled CSRF protection because we are not using Cookies.
        .exceptionHandling()
        .authenticationEntryPoint(this.authenticationEntryPoint)
      .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
        .antMatchers(LOGIN_ENTRY_POINT).permitAll() // Login end-point
        .antMatchers(TOKEN_REFRESH_ENTRY_POINT).permitAll() // Token refresh end-point
      .and()
        .addFilterBefore(buildAjaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  protected final void configure(final AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(ajaxAuthenticationProvider);
    auth.authenticationProvider(jwtAuthenticationProvider);
  }
}
