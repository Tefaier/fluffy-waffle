package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.ObjectMapperTestConfig;
import com.example.auction.models.configurations.TransactionOutboxConfig;
import com.example.auction.models.entities.Money;
import com.example.auction.models.enums.Currency;
import com.example.auction.models.exceptions.InvalidBetException;
import com.example.auction.models.gateways.LotPurchaseOutboxService;
import com.example.auction.models.gateways.OutboxControlService;
import com.example.auction.models.gateways.UserCreationOutboxService;
import com.example.auction.models.repositories.BetRepository;
import com.example.auction.models.repositories.LotRepository;
import com.example.auction.models.repositories.UserRepository;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(
    properties = {
        "resilience4j.retry.instances.user-generation.max-attempts=1"
    }
)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    KafkaAutoConfiguration.class, ObjectMapperTestConfig.class, UserService.class, LotService.class, BetService.class, LotPurchaseOutboxService.class, UserCreationOutboxService.class, TransactionOutboxConfig.class, OutboxControlService.class
})
class DBServicesTest extends DBSuite {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LotRepository lotRepository;
  @Autowired
  private BetRepository betRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private LotService lotService;
  @Autowired
  private BetService betService;

  @BeforeEach
  @Transactional
  void clearInfo() {
    userRepository.deleteAll();
    lotRepository.deleteAll();
    betRepository.deleteAll();
  }

  @Test
  void lotArrayCheck() {
    var uuid = userService.createUser("1", "1", "1", "hash", "myemail@company.com");
    var user = userService.getUser(uuid);
    var lotId = lotService.createLot(
        uuid,
        new Money(10L, 0L, Currency.RUB),
        new Money(10L, 0L, Currency.RUB),
        Timestamp.from(Instant.now().plus(Duration.ofDays(2))),
        Timestamp.from(Instant.now().plus(Duration.ofDays(20))),
        "Super thing",
        List.of(
            "https://i.pinimg.com/564x/dc/04/f7/dc04f738148df66a6b8faed5d0789f50.jpg",
            "https://i.pinimg.com/564x/1e/97/96/1e97963d5d706a80155ddb5d3e45dd6a.jpg").toArray(String[]::new)
    );
    var lot = lotService.getLot(lotId);
    System.out.println("success");
  }

  @Test
  void simpleMethodsTest() {
    Money start = new Money(50L, 0L, Currency.RUB);
    Money add = new Money(10L, 0L, Currency.RUB);
    Timestamp beginNeg = Timestamp.from(Instant.now().minus(Duration.ofDays(1)));
    Timestamp beginPos = Timestamp.from(Instant.now().plus(Duration.ofDays(1)));
    Timestamp end = Timestamp.from(Instant.now().plus(Duration.ofDays(10)));
    var userUUID1 = userService.createUser("1", "1", "1", "1", "myemail1@company.com");
    // same login exception
    assertThrows(Exception.class, () -> {
      userService.createUser("1", "2", "2", "2", "myemail@company.com");
    });
    var userUUID2 = userService.createUser("2", "2", "2", "2", "myemail2@company.com");
    var userUUID3 = userService.createUser("3", "3", "3", "3", "myemail3@company.com");
    var lotId1 = lotService.createLot(userUUID1, start, add, beginNeg, end, "Some lot", new String[0]);
    var lotId2 = lotService.createLot(userUUID1, start, add, beginPos, end, "Other lot", new String[0]);
    // lot time exception
    assertThrows(InvalidBetException.class, () -> {
      betService.makeBet(userUUID2, lotId2, new Money(50L, 0L, Currency.RUB));
    });
    // minimum price exception
    assertThrows(InvalidBetException.class, () -> {
      betService.makeBet(userUUID2, lotId1, new Money(40L, 0L, Currency.RUB));
    });
    var betId1 = betService.makeBet(userUUID2, lotId1, new Money(50L, 0L, Currency.RUB));
    var betId2 = betService.makeBet(userUUID3, lotId1, new Money(65L, 0L, Currency.RUB));
    // minimum price increase exception
    assertThrows(InvalidBetException.class, () -> {
      betService.makeBet(userUUID2, lotId1, new Money(70L, 0L, Currency.RUB));
    });
    var betId3 = betService.makeBet(userUUID2, lotId1, new Money(75L, 5L, Currency.RUB).convertToCurrency(Currency.USD));
  }
}