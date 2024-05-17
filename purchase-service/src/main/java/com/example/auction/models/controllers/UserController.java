package com.example.auction.models.controllers;

import com.example.auction.models.DTOs.AddMoneyRequest;
import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.User;
import com.example.auction.models.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PutMapping
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
}
