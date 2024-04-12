package com.example.auction.models.DTOs;

import com.example.auction.models.entities.Money;

import java.util.UUID;

public record LotPurchaseRequest(String requestId, UUID userId, Long lotId, Money value) {
}
