package com.example.auction.models.DTOs;

import java.util.UUID;

public record AddMoneyRequest(UUID userId, DTOMoney value) {
}
