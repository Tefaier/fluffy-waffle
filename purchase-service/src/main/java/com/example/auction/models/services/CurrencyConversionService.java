package com.example.auction.models.services;

import com.example.auction.models.enums.Currency;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionService {
  public float getCurrencyRatio(Currency from, Currency to) {
    return to.getComparativeValue() / from.getComparativeValue();
  }
}
