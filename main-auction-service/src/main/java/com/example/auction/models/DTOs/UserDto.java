package com.example.auction.models.DTOs;

import java.util.List;
import java.util.UUID;

public record UserDto(
        UUID id,
        String login,
        String firstName,
        String lastName,
        String email,
        List<LotDto> lots
) {
}
