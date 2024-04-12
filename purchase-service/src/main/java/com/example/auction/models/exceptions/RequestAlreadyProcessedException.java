package com.example.auction.models.exceptions;

public class RequestAlreadyProcessedException extends RuntimeException {
  public RequestAlreadyProcessedException(String message) {
    super(message);
  }
}
