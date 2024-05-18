package com.example.auction.models.DTOs;

import java.util.UUID;

public record BetDto(Long id,
                     UUID userId,
                     Long lotId,
                     DTOMoney value
) {
}
