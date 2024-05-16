package com.example.auction.models.DTOs;

import java.sql.Timestamp;
import java.util.UUID;

public record LotCreateRequest(UUID userId,
                               DTOMoney initialPrice,
                               DTOMoney minimumIncrease,
                               Timestamp startTime,
                               Timestamp finishTime,
                               String description,
                               String[] images) {
}
