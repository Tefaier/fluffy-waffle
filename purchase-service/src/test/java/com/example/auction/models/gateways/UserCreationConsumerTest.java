package com.example.auction.models.gateways;

import com.example.auction.models.DBSuite;
import com.example.auction.models.DTOs.UserTransitionRequest;
import com.example.auction.models.ObjectMapperTestConfig;
import com.example.auction.models.repositories.UserRepository;
import com.example.auction.models.services.CurrencyConversionService;
import com.example.auction.models.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(
    properties = {
        "topic-user-creation-request=some-test-topic",
        "spring.kafka.consumer.group-id=some-consumer-group",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    }
)
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class, UserRepository.class, UserService.class})
@Testcontainers
class UserCreationConsumerTest extends DBSuite {
  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private UserService userService;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void messageReceiveTest() throws JsonProcessingException, InterruptedException {
    UUID userId = UUID.randomUUID();
    kafkaTemplate.send("some-test-topic", objectMapper.writeValueAsString(new UserTransitionRequest(userId)));

    Thread.sleep(5000);

    var user = userService.getUser(userId);
    assertNotNull(user);
    assertEquals(userId, user.getMainServiceId());
    assertEquals(0L, user.getMoney().getIntegerPart());
    assertEquals(0L, user.getMoney().getDecimalPart());
  }
}