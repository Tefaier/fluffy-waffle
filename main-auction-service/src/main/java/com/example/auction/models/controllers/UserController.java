package com.example.auction.models.controllers;

import com.example.auction.models.controllers.response.ApiError;
import com.example.auction.models.entities.User;
import com.example.auction.models.services.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(@NotNull @PathVariable UUID id) {
        return userService.getUser(id);
    }

    @GetMapping("/{login}")
    public User getUserByLogin(@NotNull String login) {
        return userService.getUser(login);
    }

    @PostMapping
    public User createUser() {
        return null;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> noSuchElementExceptionHandler(NoSuchElementException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
