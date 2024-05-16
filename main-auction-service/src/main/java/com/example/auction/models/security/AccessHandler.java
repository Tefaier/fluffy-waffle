package com.example.auction.models.security;

import com.example.auction.models.entities.User;
import com.example.auction.models.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AccessHandler {
  @Autowired
  private UserService userService;

  private User getRelatedUser(Authentication authentication) {
    String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .map(user -> (UserDetails) user)
        .map(UserDetails::getUsername).orElseThrow();
    return userService.getUser(username);
  }

  public boolean isAdmin(Authentication authentication) {
    var authorities = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .map(user -> (UserDetails) user)
        .map(UserDetails::getAuthorities).orElseThrow();
    return authorities.stream().anyMatch(auth -> "ADMIN".equals(auth.getAuthority()));
  }

  public boolean checkInfoAccess(Authentication authentication, UUID userId) {
    return isAdmin(authentication) || getRelatedUser(authentication).getId().equals(userId);
  }
}
