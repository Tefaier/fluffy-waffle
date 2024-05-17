package com.example.auction.models.controllers;

import com.example.auction.models.entities.User;
import com.example.auction.models.security.AccessHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontEndController {
  private final AccessHandler accessHandler;

  public FrontEndController(AccessHandler accessHandler) {
    this.accessHandler = accessHandler;
  }

  @GetMapping("/login")
  String login() {
    return "login";
  }

  @GetMapping("/")
  String index(Authentication authentication, Model model) {
    // example of storing userId in html tag
    setUserId(authentication, model);
    return "index";
  }

  @GetMapping("/account")
  @PreAuthorize("isAuthenticated()")
  String account() {
    return "account";
  }

  @GetMapping("/data")
  @PreAuthorize("isAuthenticated()")
  String data() {
    return "data";
  }

  @GetMapping("/lot")
  String lot() {
    return "lot";
  }

  @GetMapping("/new-lot")
  @PreAuthorize("isAuthenticated()")
  String newLot() {
    return "new-lot";
  }

  @GetMapping("/register")
  String register() {
    return "register";
  }

  @GetMapping("/personal-lots")
  @PreAuthorize("isAuthenticated()")
  String personalLots() {
    return "personal-lots";
  }

  private void setUserId(Authentication authentication, Model model) {
    if (authentication != null) {
      User user = accessHandler.getRelatedUser(authentication);
      model.addAttribute("id", user.getId());
    }
    else {
      model.addAttribute("id", "null");
    }
  }
}
