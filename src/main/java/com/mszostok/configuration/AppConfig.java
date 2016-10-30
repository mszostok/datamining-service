package com.mszostok.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
  private final Async async = new Async();

  @Getter
  @Setter
  @ToString
  public static class Async {
    private int corePoolSize = 2;
    private int maxPoolSize = 50;
    private int queueCapacity = 10000;
  }
}
