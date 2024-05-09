package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.LotPurchaseResponse;
import com.example.auction.models.DTOs.UserTransitionRequest;
import com.example.auction.models.KafkaTestConsumer;
import com.example.auction.models.ObjectMapperTestConfig;
import com.example.auction.models.entities.Bet;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.User;
import com.example.auction.models.enums.Currency;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.gateways.LotPurchaseOutboxService;
import com.example.auction.models.repositories.BetRepository;
import com.example.auction.models.repositories.LotRepository;
import com.example.auction.models.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(
    properties = {
        "topic-lot-purchase-request=some-test-topic",
        "topic-lot-purchase-result=some-test-topic-response",
        "spring.kafka.consumer.group-id=some-consumer-group",
        "outbox-flush-frequency=500",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "finished-lots-check-delay=500"
    }
)
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class, UserService.class, LotPurchaseOutboxService.class})
@Testcontainers
class LotBuyKafkaTest extends DBSuite {
  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private LotPurchaseOutboxService lotPurchaseOutboxService;
  @Autowired
  private UserService userService;
  @Autowired
  private LotService lotService;
  @Autowired
  private LotRepository lotRepository;
  @Autowired
  private BetRepository betRepository;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  private static KafkaTestConsumer consumer;

  private static Money buyMoney = new Money(50L, 0L, Currency.RUB);

  @BeforeAll
  static void setup() {
    setupKafkaConsumer();
  }

  static void setupKafkaConsumer() {
    consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));
  }

  @Transactional
  private long createExpiredLot(UUID owner, UUID buyer) {
    Money start = buyMoney;
    Money add = new Money(10L, 0L, Currency.RUB);
    Timestamp begin = Timestamp.from(Instant.now().minus(Duration.ofDays(3)));
    Timestamp end = Timestamp.from(Instant.now().minus(Duration.ofDays(1)));

    User user = userService.getUser(owner);
    Lot lot = new Lot(user, start, add, begin, end, "Some lot", new String[0]);
    lotRepository.save(lot);

    if (buyer != null) {
      User betMaker = userService.getUser(buyer);
      Bet bet = new Bet(betMaker, lot, buyMoney);
      betRepository.save(bet);
    }

    return lot.getId();
  }

  @Test
  void sendTest() throws InterruptedException, JsonProcessingException {
    var userUUID1 = userService.createUser("1", "1", "1", "1", "myemail1@company.com");
    var userUUID2 = userService.createUser("2", "2", "2", "2", "myemail2@company.com");

    long unboughtLotId = createExpiredLot(userUUID1, null);
    long boughtLotId = createExpiredLot(userUUID2, userUUID1);

    Thread.sleep(5000);

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          try {
            var value = objectMapper.readValue(record.value(), LotPurchaseRequest.class);
            assertEquals(userUUID1, value.userId());
            assertEquals(userUUID2, value.lotOwnerId());
            assertEquals(boughtLotId, value.lotId());
            assertEquals(0, value.value().compareTo(buyMoney));
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );

    assertEquals(LotState.UNSOLD, lotService.getLot(unboughtLotId).getLotState());

    kafkaTemplate.send("some-test-topic-response", objectMapper.writeValueAsString(new LotPurchaseResponse(userUUID1, boughtLotId, Boolean.TRUE)));

    Thread.sleep(5000);

    assertEquals(LotState.SOLD, lotService.getLot(boughtLotId).getLotState());
  }
}