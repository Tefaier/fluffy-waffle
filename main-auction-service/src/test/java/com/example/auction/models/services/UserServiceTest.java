package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.ObjectMapperTestConfig;
import com.example.auction.models.repositories.UserRepository;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(
    classes = {UserService.class},
    properties = {
        "resilience4j.retry.instances.user-generation.max-attempts=5",
        "outbox-flush-frequency=500"
    }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    ObjectMapperTestConfig.class, TransactionOutbox.class
})
public class UserServiceTest extends DBSuite {
  @Autowired
  private UserService userService;
  @MockBean
  private UserRepository userRepository;

  @Test
  void userCreateRetryTest() {
    UUID fixedUUID = UUID.randomUUID();
    try (MockedStatic<UUID> mockedStatic = Mockito.mockStatic(UUID.class)) {
      mockedStatic.when(UUID::randomUUID).thenReturn(fixedUUID);

      when(userRepository.save(any())).thenCallRealMethod();
      userService.createUser("1", "1", "1", "1", "myemail1@org.com");
      await().atMost(Duration.ofSeconds(3))
          .pollDelay(Duration.ofSeconds(1))
          .untilAsserted(() -> Mockito.verify(
                  userRepository, times(5))
              .save(any()));
    }
  }
}