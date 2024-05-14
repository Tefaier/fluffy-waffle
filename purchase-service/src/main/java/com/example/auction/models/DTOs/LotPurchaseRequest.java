package com.example.auction.models.DTOs;

import com.example.auction.models.entities.Money;

import java.util.UUID;

public record LotPurchaseRequest(UUID requestId, UUID userId, Long lotId, UUID lotOwnerId, Money value) {
}
