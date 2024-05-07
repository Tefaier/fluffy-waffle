package com.example.auction.models.services;

import com.example.auction.models.enums.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = {CurrencyConversionService.class}
)
class CurrencyConversionServiceTest {
  @Autowired
  private CurrencyConversionService currencyConversionService;

  @Test
  void simpleTest() {
    assertEquals(
        Currency.USD.getComparativeValue() / Currency.RUB.getComparativeValue(),
        currencyConversionService.getCurrencyRatio(Currency.RUB, Currency.USD));
  }
}