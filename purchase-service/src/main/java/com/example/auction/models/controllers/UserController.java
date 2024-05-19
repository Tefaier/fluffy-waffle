package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.AddMoneyRequest;
import com.example.auction.models.DTOs.DTOMoney;
import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.User;
import com.example.auction.models.services.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PutMapping
  @CrossOrigin(origins = "http://localhost:8080")
  public void addMoney(@RequestBody AddMoneyRequest request) {
    var value = request.value();
    User user = userService.getUser(request.userId());
    userService.addMoney(
            user.getId(),
            new Money(
                    value.integerPart(),
                    value.decimalPart(),
                    value.currency()
            )
    );
  }

  @GetMapping("/{id}")
  @CrossOrigin(origins = "http://localhost:8080")
  public DTOMoney getBalance(@NotNull @PathVariable("id") UUID userId) {
    User user = userService.getUser(userId);
    Money money = userService.getBalance(user.getId());
    return new DTOMoney(
            money.getIntegerPart(),
            money.getDecimalPart(),
            money.getCurrency()
    );
  }
}
