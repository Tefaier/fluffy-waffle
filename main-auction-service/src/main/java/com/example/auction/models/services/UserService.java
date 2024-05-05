package com.example.auction.models.services;

import com.example.auction.models.entities.User;
import com.example.auction.models.enums.Role;
import com.example.auction.models.repositories.UserRepository;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User getUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User getUser(String login) {
        return userRepository.findByLogin(login).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Retry(name = "user-generation")
    public UUID createUser(String login, String firstName, String lastName, String passwordHash, String email) {
        User user = new User(login, firstName, lastName, passwordHash, Set.of(Role.USER), email);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByLogin(username)
            .map(user ->
                org.springframework.security.core.userdetails.User.withUsername(username)
                    .authorities(user.getRoles().stream()
                        .map(Enum::name)
                        .map(SimpleGrantedAuthority::new)
                        .toList()
                    )
                    .password(user.getPasswordHash())
                    .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
