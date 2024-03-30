package com.example.auction.models.exceptions;

public class NegativeMoneyException extends RuntimeException {
  public NegativeMoneyException(String message) {
    super(message);
  }
}
