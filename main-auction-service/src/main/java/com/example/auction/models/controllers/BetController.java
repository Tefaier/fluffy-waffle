package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.BetMakingRequest;
import com.example.auction.models.services.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BetController {
  private final BetService betService;

  @Autowired
  public BetController(BetService betService) {
    this.betService = betService;
  }

  @PostMapping("/bet/make")
  public long createBet (@RequestBody BetMakingRequest request) {
    return betService.makeBet(request.userId(), request.lotId(), request.value());
  }
}
