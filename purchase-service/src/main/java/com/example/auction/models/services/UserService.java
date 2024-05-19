package com.example.auction.models.services;

import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.User;
import com.example.auction.models.repositories.UserRepository;
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
    public User getUser(UUID mainServiceId) {
        return userRepository.findByMainServiceId(mainServiceId).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long createUser(UUID mainServiceId) {
        User user = new User(mainServiceId);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addMoney(Long userId, Money money) {
        User user = userRepository.findById(userId).orElseThrow();
        entityManager.lock(user, LockModeType.PESSIMISTIC_WRITE);
        user.setMoney(user.getMoney().plus(money));
        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void subtractMoney(Long userId, Money money) {
        User user = userRepository.findById(userId).orElseThrow();
        entityManager.lock(user, LockModeType.PESSIMISTIC_WRITE);
        user.setMoney(user.getMoney().minus(money));
        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Money getBalance(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getMoney();
    }
}
