package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.UserRegistryRequest;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.services.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
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

  @DeleteMapping("/{id}")
  @PreAuthorize("@accessHandler.isAdmin(authentication)")
  public void deleteUser(@NotNull @PathVariable("id") UUID userId) {
    userService.deleteUser(userId);
  }

  @GetMapping("/{id}")
  @PreAuthorize("@accessHandler.checkInfoAccess(authentication, #userId)")
  public Map<UserService.LotUserStatus, List<Lot>> getLotsByUser(@NotNull @PathVariable("id") UUID userId){
    return userService.getRelatedLots(userId);
  }
}
