package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.DTOs.LotPurchaseResponse;
import com.example.auction.models.DTOs.UserTransitionRequest;
import com.example.auction.models.KafkaTestConsumer;
import com.example.auction.models.ObjectMapperTestConfig;
import com.example.auction.models.gateways.LotPurchaseOutboxService;
import com.example.auction.models.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(
    properties = {
        "topic-user-creation-request=some-test-topic",
        "outbox-flush-frequency=500",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    }
)
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class, UserService.class})
@Testcontainers
class UserServiceKafkaTest extends DBSuite {
  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private LotPurchaseOutboxService lotPurchaseOutboxService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  private static KafkaTestConsumer consumer;

  @BeforeAll
  static void setup() {
    setupKafkaConsumer();
  }

  static void setupKafkaConsumer() {
    consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));
  }

  @Test
  void userCreationPassTest() throws InterruptedException {
    var userUUID = userService.createUser("1", "1", "1", "1", "myemail1@company.com");

    Thread.sleep(5000);

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          try {
            var value = objectMapper.readValue(record.value(), UserTransitionRequest.class);
            assertEquals(userUUID, value.userId());
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );
  }
}
