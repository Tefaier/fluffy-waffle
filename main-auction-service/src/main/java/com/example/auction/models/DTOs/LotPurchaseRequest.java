package com.example.auction.models.DTOs;

import com.example.auction.models.entities.Money;

public record LotPurchaseRequest(String requestId, Long userId, Money value) {
}
