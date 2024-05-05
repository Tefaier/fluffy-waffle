package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.UserRegistryRequest;
import com.example.auction.models.services.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/login")
public class UserController {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  @Autowired
  public UserController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping
  public UUID registry(@RequestBody UserRegistryRequest request) {
    return userService.createUser(request.login(), request.firstName(), request.lastName(),
            passwordEncoder.encode(request.password()), request.email());
  }

  @DeleteMapping("/delete/{id}")
  public void deleteUser(@NotNull @PathVariable("id") UUID userId) {
    userService.deleteUser(userId);
  }
}
