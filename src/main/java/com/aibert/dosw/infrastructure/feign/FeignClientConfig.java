package com.aibert.dosw.infrastructure.feign;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.BASIC;
  }

  @Bean
  public Request.Options feignRequestOptions() {
    return new Request.Options(5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
  }

  @Bean
  public Retryer feignRetryer() {
    return Retryer.NEVER_RETRY;
  }

  @Bean
  public RequestInterceptor defaultRequestInterceptor() {
    return template -> template.header("Accept", "application/json");
  }
}
