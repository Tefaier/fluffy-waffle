package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.ObjectMapperTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(
    properties = {
        "resilience4j.retry.instances.user-generation.max-attempts=5",
        "outbox-flush-frequency=500"
    }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    ObjectMapperTestConfig.class, KafkaAutoConfiguration.class
})
class UserServiceRetryTest extends DBSuite {
  @Autowired
  private UserService userService;

  @Test
  void userCreateRetryTest() throws InterruptedException {
    UUID fixedUUID = UUID.randomUUID();
    try (MockedStatic<UUID> mockedStatic = Mockito.mockStatic(UUID.class)) {
      mockedStatic.when(UUID::randomUUID).thenReturn(fixedUUID);

      userService.createUser("1", "1", "1", "1", "myemail1@org.com");
      try {
        userService.createUser("2", "2", "2", "2", "myemail2@org.com");
      } catch (RuntimeException ignored) {

      }

      Thread.sleep(1000);

      mockedStatic.verify(UUID::randomUUID, times(7));
    }
  }
}