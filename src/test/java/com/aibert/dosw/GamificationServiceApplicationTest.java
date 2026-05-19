package com.aibert.dosw;

import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class GamificationServiceApplicationTest {

  @Test
  void main_invokesSpringApplicationRun() {
    String[] args = {"--spring.profiles.active=test"};

    try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
      GamificationServiceApplication.main(args);

      springApplication.verify(
          () -> SpringApplication.run(GamificationServiceApplication.class, args), times(1));
    }
  }
}
