package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.ObjectMapperTestConfig;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.Money;
import com.example.auction.models.enums.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    ObjectMapperTestConfig.class //, BetService.class, LotService.class, UserService.class
})
class LotServiceTest extends DBSuite {
  @Autowired
  private UserService userService;
  @Autowired
  private LotService lotService;
  @Autowired
  private BetService betService;

  @Test
  void arrayCheck() {
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
}