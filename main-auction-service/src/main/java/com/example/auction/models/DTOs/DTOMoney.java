package com.example.auction.models.DTOs;

import com.example.auction.models.enums.Currency;

public record DTOMoney(Long integerPart, Long decimalPart, Currency currency) {
}
