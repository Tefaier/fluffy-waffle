package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.LotCreateRequest;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.Money;
import com.example.auction.models.services.LotService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lot")
@PreAuthorize("isAuthenticated()")
public class LotController {
  private final LotService lotService;

  @Autowired
  public LotController(LotService lotService) {
    this.lotService = lotService;
  }

  @PostMapping()
  public long createLot(@RequestBody LotCreateRequest request) {
    var initialPrice = request.initialPrice();
    var minimumIncrease = request.minimumIncrease();
    return lotService.createLot(
            request.userId(),
            new Money(initialPrice.integerPart(), initialPrice.decimalPart(), initialPrice.currency()),
            new Money(minimumIncrease.integerPart(), minimumIncrease.decimalPart(), minimumIncrease.currency()),
            request.startTime(),
            request.finishTime(),
            request.description(),
            request.images()
    );
  }

  @GetMapping
  public List<Lot> getAllLots() {
    return lotService.getAllLots();
  }

  @GetMapping("/{id}")
  public Lot getLotById(@NotNull @PathVariable("id") Long lotId) {
    return lotService.getLot(lotId);
  }
}
