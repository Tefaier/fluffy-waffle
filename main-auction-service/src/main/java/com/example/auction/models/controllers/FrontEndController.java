package com.example.auction.models.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontEndController {
  @GetMapping("/login")
  String login() {
    return "login";
  }

  @GetMapping("/")
  String index() {
    return "index";
  }

  @GetMapping("/account")
  String account() {
    return "account";
  }

  @GetMapping("/data")
  String data() {
    return "data";
  }

  @GetMapping("/lot")
  String lot() {
    return "lot";
  }

  @GetMapping("/new-lot")
  String newLot() {
    return "new-lot";
  }

  @GetMapping("/register")
  String register() {
    return "register";
  }

  @GetMapping("/personal-lots")
  String personalLots() {
    return "personal-lots";
  }
}
