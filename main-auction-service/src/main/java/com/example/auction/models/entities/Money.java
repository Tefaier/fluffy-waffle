package com.example.auction.models.entities;

import com.example.auction.models.enums.Currency;
import com.example.auction.models.exceptions.NegativeMoneyException;
import com.example.auction.models.services.CurrencyConversionService;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.context.SpringContextUtils;

@Embeddable
public class Money implements Comparable<Money> {
  @Column
  @PositiveOrZero
  @NotNull
  private Long integerPart;

  @Column(name = "value_decimal")
  @PositiveOrZero
  @NotNull
  private Long decimalPart; // exactly reversed decimal part

  @Column
  @Enumerated(EnumType.STRING)
  @NotNull
  private Currency currency;

  protected Money() {
  }

  public Money(Long integerPart, Long decimalPart, Currency currency) {
    this.integerPart = integerPart;
    this.decimalPart = decimalPart;
    this.currency = currency;
  }

  public Money plus(Money money) {
    double value1 = Money.getDoubleValue(this);
    double value2 = Money.getDoubleValue(money.convertToCurrency(currency));
    var parts = Double.toString(value1 + value2).split("\\.");
    long newIntPart = Long.parseLong(parts[0]);
    long newDecPart = Long.parseLong(new StringBuilder(parts[1]).reverse().toString());
    return new Money(newIntPart, newDecPart, currency);
  }

  // can throw exception
  public Money minus(Money money) throws NegativeMoneyException {
    double value1 = Money.getDoubleValue(this);
    double value2 = Money.getDoubleValue(money.convertToCurrency(currency));
    if (value1 < value2) {
      throw new NegativeMoneyException("Tried to subtract to high value: " + value1 + " - " + value2);
    }
    var parts = Double.toString(value1 - value2).split("\\.");
    long newIntPart = Long.parseLong(parts[0]);
    long newDecPart = Long.parseLong(new StringBuilder(parts[1]).reverse().toString());
    return new Money(newIntPart, newDecPart, currency);
  }

  public Long getIntegerPart() {
    return integerPart;
  }

  public void setIntegerPart(Long integerPart) {
    this.integerPart = integerPart;
  }

  public Long getDecimalPart() {
    return decimalPart;
  }

  public void setDecimalPart(Long decimalPart) {
    this.decimalPart = decimalPart;
  }

  public Currency getCurrency() {
    return currency;
  }

  protected void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public Money convertToCurrency(Currency to) {
    if (to == currency) return this;
    double value = Money.getDoubleValue(this);
    value *= CurrencyConversionService.getCurrencyRatio(currency, to);
    var parts = Double.toString(value).split("\\.");
    long newIntPart = Long.parseLong(parts[0]);
    long newDecPart = Long.parseLong(new StringBuilder(parts[1]).reverse().toString());
    return new Money(newIntPart, newDecPart, to);
  }

  private static double getDoubleValue(Money from) {
    return Double.parseDouble(from.getIntegerPart() + "." + new StringBuilder(from.getDecimalPart().toString()).reverse());
  }

  @Override
  public int compareTo(Money o) {
    if (o == null) return 0;
    double value1 = Money.getDoubleValue(this);
    double value2 = Money.getDoubleValue(o.convertToCurrency(currency));
    return Double.compare(value1, value2);
  }
}
