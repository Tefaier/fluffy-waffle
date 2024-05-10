package com.example.auction.models.controllers.request;

import jakarta.validation.constraints.NotEmpty;

public record UserCreateRequest(@NotEmpty String login, @NotEmpty String firstName, @NotEmpty String lastName, @NotEmpty String password, @NotEmpty String email) {
}
