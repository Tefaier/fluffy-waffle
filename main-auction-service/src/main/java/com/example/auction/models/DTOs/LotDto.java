package com.example.auction.models.DTOs;

import com.example.auction.models.enums.LotState;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record LotDto(Long id,
                     UUID userId,
                     DTOMoney initialPrice,
                     DTOMoney minimumIncrease,
                     Timestamp startTime,
                     Timestamp finishTime,
                     String name,
                     String description,
                     String[] images,
                     LotState lotState,
                     List<BetDto> lotBets
) {
}
