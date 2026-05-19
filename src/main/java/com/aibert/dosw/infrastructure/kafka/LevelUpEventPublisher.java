package com.aibert.dosw.infrastructure.kafka;

import com.aibert.dosw.domain.model.user.Level;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LevelUpEventPublisher {

  private final KafkaTemplate<String, LevelUpEvent> kafkaTemplate;

  @Value("${app.kafka.topics.level-up:gamification.level-up}")
  private String levelUpTopic;

  public void publish(UUID userId, Level previousLevel, Level newLevel) {
    if (userId == null || previousLevel == null || newLevel == null) {
      return;
    }
    if (newLevel.ordinal() <= previousLevel.ordinal()) {
      return;
    }

    LevelUpEvent event =
        new LevelUpEvent(
            userId.toString(), newLevel.ordinal() + 1, previousLevel.ordinal() + 1, newLevel.name());

    kafkaTemplate
        .send(levelUpTopic, event.userId(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.warn("Failed to publish level-up event for user {}", userId, ex);
              } else {
                log.debug("Published level-up event for user {} to topic {}", userId, levelUpTopic);
              }
            });
  }
}
