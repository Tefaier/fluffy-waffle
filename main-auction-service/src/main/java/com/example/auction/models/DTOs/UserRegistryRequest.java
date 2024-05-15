package com.example.auction.models.DTOs;

public record UserRegistryRequest (String login, String firstName, String lastName, String password, String email) {
}
