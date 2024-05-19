package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.LotDto;
import com.example.auction.models.DTOs.UserDto;
import com.example.auction.models.DTOs.UserRegistryRequest;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.User;
import com.example.auction.models.security.AccessHandler;
import com.example.auction.models.services.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AccessHandler accessHandler;

  @Autowired
  public UserController(UserService userService, PasswordEncoder passwordEncoder, AccessHandler accessHandler) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.accessHandler = accessHandler;
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

  @GetMapping("/{id}/lots")
  @PreAuthorize("@accessHandler.checkInfoAccess(authentication, #userId)")
  public Map<UserService.LotUserStatus, List<LotDto>> getLotsByUser(@NotNull @PathVariable("id") UUID userId){
    Map<UserService.LotUserStatus, List<Lot>> lots = userService.getRelatedLots(userId);
    return lots.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                            .map(Lot::toLotDto)
                            .collect(Collectors.toList())
            ));
  }

  @GetMapping("/{id}")
  @PreAuthorize("@accessHandler.checkInfoAccess(authentication, #userId)")
  public UserDto getUser(@NotNull @PathVariable("id") UUID userId) {
    return userService.getUser(userId).toUserDto();
  }

  @GetMapping("/name/{id}")
  public String getUserNameById(@NotNull @PathVariable("id") UUID userId) {
    User user = userService.getUser(userId);
    return user.getLogin();
  }

  @GetMapping("/userid")
  @PreAuthorize("isAuthenticated()")
  public UUID getUserId(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    return accessHandler.getRelatedUser(authentication).getId();
  }
}
