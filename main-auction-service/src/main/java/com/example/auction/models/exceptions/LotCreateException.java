package com.example.auction.models.exceptions;

public class LotCreateException extends RuntimeException {
    public LotCreateException(String message) {
        super(message);
    }
}
