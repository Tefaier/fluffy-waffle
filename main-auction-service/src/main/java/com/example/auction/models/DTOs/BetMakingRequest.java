package com.example.auction.models.DTOs;

import com.example.auction.models.entities.Money;

import java.util.UUID;

public record BetMakingRequest(UUID userId, Long lotId, DTOMoney value) {
}
