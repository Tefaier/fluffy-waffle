package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.BetMakingRequest;
import com.example.auction.models.entities.Bet;
import com.example.auction.models.entities.Money;
import com.example.auction.models.services.BetService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bet")
@PreAuthorize("isAuthenticated()")
public class BetController {
  private final BetService betService;

  @Autowired
  public BetController(BetService betService) {
    this.betService = betService;
  }

  @PostMapping("/make")
  public long createBet(@RequestBody BetMakingRequest request) {
    var value = request.value();
    return betService.makeBet(
            request.userId(),
            request.lotId(),
            new Money(value.integerPart(), value.decimalPart(), value.currency()));
  }

  @GetMapping("/{id}")
  public Bet getBetById(@NotNull @PathVariable("id") Long betId) {
    return betService.getBet(betId);
  }
}
