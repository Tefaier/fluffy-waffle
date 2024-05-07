package com.example.auction.models.services;

import com.example.auction.models.DBSuite;
import com.example.auction.models.entities.Money;
import com.example.auction.models.enums.Currency;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserService.class})
@EnableAspectJAutoProxy
class UserServiceTest extends DBSuite {
  @Autowired
  private UserService userService;
  @MockBean
  private CurrencyConversionService currencyConversionService;

  @Test
  void workTest() {
    try (MockedStatic<CurrencyConversionService> mockedStatic = Mockito.mockStatic(CurrencyConversionService.class)) {
      mockedStatic.when(() -> CurrencyConversionService.getCurrencyRatio(Currency.RUB, Currency.EUR)).thenReturn(0.1f);
      mockedStatic.when(() -> CurrencyConversionService.getCurrencyRatio(Currency.EUR, Currency.RUB)).thenReturn(10f);

      UUID mainServiceId = UUID.randomUUID();
      var newId = userService.createUser(mainServiceId);
      var user = userService.getUser(newId);
      var userCopy = userService.getUser(mainServiceId);
      assertEquals(user.getId(), userCopy.getId());

      userService.addMoney(newId, new Money(10L, 1L, Currency.EUR));
      userService.subtractMoney(newId, new Money(1L, 0L, Currency.RUB));

      user = userService.getUser(newId);
      assertEquals(100, user.getMoney().getIntegerPart());
    }
  }
}