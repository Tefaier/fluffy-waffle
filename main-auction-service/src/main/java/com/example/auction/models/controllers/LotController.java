package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.DTOMoney;
import com.example.auction.models.DTOs.LotCreateRequest;
import com.example.auction.models.DTOs.LotDto;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.Money;
import com.example.auction.models.services.BetService;
import com.example.auction.models.services.LotService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lot")
//@PreAuthorize("isAuthenticated()")
public class LotController {
  private final LotService lotService;
  private final BetService betService;

  @Autowired
  public LotController(LotService lotService, BetService betService) {
    this.lotService = lotService;
    this.betService = betService;
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
            request.name(),
            request.description(),
            request.images()
    );
  }

  @GetMapping
  public List<LotDto> getAllLots() {
    List<Lot> lots = lotService.getAllLots();
    return lots.stream().map(Lot::toLotDto).collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public LotDto getLotById(@NotNull @PathVariable("id") Long lotId) {
    Lot lot = lotService.getLot(lotId);
    return lot.toLotDto();
  }

  @GetMapping("/{id}/top")
  @Transactional
  public DTOMoney getTopBet(@NotNull @PathVariable("id") Long lotId) {
    var bet = betService.getHighestValueBet(lotService.getLot(lotId).getLotBets());
    return bet == null ? null : new DTOMoney(
        bet.getValue().getIntegerPart(),
        bet.getValue().getDecimalPart(),
        bet.getValue().getCurrency()
    );
  }
}
