package com.example.auction.models.services;

import com.example.auction.models.enums.Currency;
import org.springframework.stereotype.Service;

public class CurrencyConversionService {
  public static float getCurrencyRatio(Currency from, Currency to) {
    return from.getComparativeValue() / to.getComparativeValue();
  }
}
