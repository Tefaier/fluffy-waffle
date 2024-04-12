package com.example.auction.models.services;

import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.User;
import com.example.auction.models.repositories.UserRepository;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Retry(name = "user-generation")
    public UUID createUser(String login, String firstName, String lastName, String passwordHash, String email) {
        User user = new User(login, firstName, lastName, passwordHash, email);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }
}
