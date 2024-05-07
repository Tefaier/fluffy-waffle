package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.enums.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest extends DBSuite {
  @Autowired
  private UserService userService;
  @MockBean
  private CurrencyConversionService currencyConversionService;

  @Test
  void workTest() {
    when(currencyConversionService.getCurrencyRatio(Currency.RUB, Currency.EUR)).thenReturn(10f);

    UUID mainServiceId = UUID.randomUUID();
    var newId = userService.createUser(mainServiceId);
    var user = userService.getUser(newId);
    var userCopy = userService.getUser(mainServiceId);
    assertEquals(user.getId(), userCopy.getId());
  }
}