package com.mszostok.security.jwt;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


public class SkipPathRequestMatcher implements RequestMatcher {
  private OrRequestMatcher matchers;
  private RequestMatcher processingMatcher;

  public SkipPathRequestMatcher(final List<String> pathsToSkip, final String processingPath) {
    Assert.notNull(pathsToSkip);
    List<RequestMatcher> reqMatcher = pathsToSkip.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());
    matchers = new OrRequestMatcher(reqMatcher);
    processingMatcher = new AntPathRequestMatcher(processingPath);
  }

  @Override
  public boolean matches(final HttpServletRequest request) {
    return !matchers.matches(request) && processingMatcher.matches(request);
  }
}
