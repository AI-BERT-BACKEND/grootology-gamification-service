package com.aibert.dosw.infrastructure.feign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import org.junit.jupiter.api.Test;

class FeignClientConfigTest {

  private final FeignClientConfig config = new FeignClientConfig();

  @Test
  void feignLoggerLevel_returnsBasic() {
    assertSame(Logger.Level.BASIC, config.feignLoggerLevel());
  }

  @Test
  void feignRequestOptions_setsConfiguredTimeouts() {
    Request.Options options = config.feignRequestOptions();

    assertEquals(5000, options.connectTimeoutMillis());
    assertEquals(10000, options.readTimeoutMillis());
    assertTrue(options.isFollowRedirects());
  }

  @Test
  void feignRetryer_returnsNeverRetry() {
    assertSame(Retryer.NEVER_RETRY, config.feignRetryer());
  }

  @Test
  void defaultRequestInterceptor_addsAcceptJsonHeader() {
    RequestInterceptor interceptor = config.defaultRequestInterceptor();
    RequestTemplate template = new RequestTemplate();

    interceptor.apply(template);

    assertTrue(template.headers().containsKey("Accept"));
    assertTrue(template.headers().get("Accept").contains("application/json"));
  }
}
