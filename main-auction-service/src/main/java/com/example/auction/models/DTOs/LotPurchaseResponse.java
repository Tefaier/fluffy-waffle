package com.example.auction.models.DTOs;

import java.util.UUID;

public record LotPurchaseResponse(UUID userId, Long lotId, Boolean isApproved) {
}
