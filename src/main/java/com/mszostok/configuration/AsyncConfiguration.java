package com.mszostok.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class AsyncConfiguration implements AsyncConfigurer {

  @Autowired
  private AppConfig appConfig;

  @Override
  @Bean(name = "taskExecutor")
  public Executor getAsyncExecutor() {
    log.info("Async configuration: {}", appConfig.getAsync());

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(appConfig.getAsync().getCorePoolSize());
    executor.setMaxPoolSize(appConfig.getAsync().getMaxPoolSize());
    executor.setQueueCapacity(appConfig.getAsync().getQueueCapacity());
    executor.setThreadNamePrefix("async-executor-");
    executor.initialize();
    return executor;

  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }
}
