package com.example.auction.models.exceptions;

public class InvalidBetException extends RuntimeException {
    public InvalidBetException(String message) {
        super(message);
    }
}
