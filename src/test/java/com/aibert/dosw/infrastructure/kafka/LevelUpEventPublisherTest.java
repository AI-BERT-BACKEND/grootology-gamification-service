package com.aibert.dosw.infrastructure.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aibert.dosw.domain.model.user.Level;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LevelUpEventPublisherTest {

  @Mock private KafkaTemplate<String, LevelUpEvent> kafkaTemplate;

  @InjectMocks private LevelUpEventPublisher publisher;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(publisher, "levelUpTopic", "gamification.level-up");
  }

  @Test
  void publish_levelUp_sendsMessage() {
    UUID userId = UUID.randomUUID();
    when(kafkaTemplate.send(anyString(), anyString(), any(LevelUpEvent.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    publisher.publish(userId, Level.NOVATO, Level.CONSTANTE);

    ArgumentCaptor<LevelUpEvent> eventCaptor = ArgumentCaptor.forClass(LevelUpEvent.class);
    verify(kafkaTemplate).send(anyString(), anyString(), eventCaptor.capture());
    LevelUpEvent event = eventCaptor.getValue();
    assertEquals(userId.toString(), event.userId());
    assertEquals(2, event.newLevelNumber());
    assertEquals(1, event.previousLevelNumber());
    assertEquals("CONSTANTE", event.levelName());
  }

  @Test
  void publish_sameLevel_doesNotSendMessage() {
    publisher.publish(UUID.randomUUID(), Level.CONSTANTE, Level.CONSTANTE);
    verify(kafkaTemplate, never()).send(anyString(), anyString(), any(LevelUpEvent.class));
  }
}
