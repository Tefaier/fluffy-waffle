package com.example.auction.models.gateways;

import com.example.auction.models.DBSuite;
import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.LotPurchaseResponse;
import com.example.auction.models.KafkaTestConsumer;
import com.example.auction.models.ObjectMapperTestConfig;
import com.example.auction.models.entities.Money;
import com.example.auction.models.enums.Currency;
import com.example.auction.models.repositories.UserRepository;
import com.example.auction.models.services.CurrencyConversionService;
import com.example.auction.models.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(
    properties = {
        "topic-lot-purchase-request=some-test-topic",
        "topic-lot-purchase-result=some-test-topic-response2",
        "spring.kafka.consumer.group-id=some-consumer-group",
        "outbox-flush-frequency=500",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    }
)
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class, UserService.class, LotPurchaseOutboxService.class})
@Testcontainers
class LotPurchaseOutboxServiceTest extends DBSuite {
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

  private static UUID userId1;
  private static UUID userId2;
  private long userLocalId1;
  private long userLocalId2;

  @BeforeAll
  static void setup() {
    setupKafkaConsumer();
    setUpContent();
  }

  static void setupKafkaConsumer() {
    consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id2");
    consumer.subscribe(List.of("some-test-topic-response2"));
  }

  @Transactional
  static void setUpContent() {
    userId1 = UUID.randomUUID();
    userId2 = UUID.randomUUID();
    while (userId2 == userId1) {
      userId2 = UUID.randomUUID();
    }
  }

  @BeforeEach
  @Transactional
  void cleanInfo() {
    try {
      userService.getUser(userId1);
    } catch (NoSuchElementException e) {
      userLocalId1 = userService.createUser(userId1);
      userLocalId2 = userService.createUser(userId2);
    }
    var user1 = userService.getUser(userId1);
    user1.setMoney(new Money(0L, 0L, Currency.EUR));
    userRepository.save(user1);
    var user2 = userService.getUser(userId2);
    user2.setMoney(new Money(100L, 0L, Currency.RUB));
    userRepository.save(user2);
  }

  @Test
  void messageProcessTest() throws JsonProcessingException, InterruptedException {
    UUID requestId1 = UUID.randomUUID();
    UUID requestId2 = UUID.randomUUID();
    List<LotPurchaseRequest> requests = List.of(
        new LotPurchaseRequest(requestId1, userId2, 10L, userId1, new Money(45L, 5L, Currency.RUB)),
        new LotPurchaseRequest(requestId2, userId2, 20L, userId1, new Money(50L, 0L, Currency.EUR)),
        new LotPurchaseRequest(requestId2, userId2, 12L, userId1, new Money(50L, 0L, Currency.RUB)));
    kafkaTemplate.send("some-test-topic", objectMapper.writeValueAsString(requests.get(0)));
    kafkaTemplate.send("some-test-topic", objectMapper.writeValueAsString(requests.get(1)));
    kafkaTemplate.send("some-test-topic", objectMapper.writeValueAsString(requests.get(2)));

    Thread.sleep(5000);

    var user1 = userService.getUser(userLocalId1);
    var user2 = userService.getUser(userLocalId2);

    var moneyToCheck = user1.getMoney();
    assertEquals(Currency.EUR, moneyToCheck.getCurrency());
    assertEquals(0, moneyToCheck.compareTo(new Money(45L, 5L, Currency.RUB)));
    moneyToCheck = user2.getMoney();
    assertEquals(0, moneyToCheck.compareTo(new Money(54L, 5L, Currency.RUB)));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(2, records.count());
    var metBuyResults = new boolean[2];
    records.iterator().forEachRemaining(
        record -> {
          try {
            var value = objectMapper.readValue(record.value(), LotPurchaseResponse.class);
            if (value.isApproved() && value.lotId() == 10L) metBuyResults[0] = true;
            if (!value.isApproved() && value.lotId() == 20L) metBuyResults[1] = true;
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );
    for (boolean metBuyResult : metBuyResults) {
      assertTrue(metBuyResult);
    }
  }
}