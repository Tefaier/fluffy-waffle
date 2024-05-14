package com.example.auction.models.gateways;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.UserTransitionRequest;
import com.example.auction.models.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class UserCreationConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationConsumer.class);

  private final UserService userService;
  private final ObjectMapper objectMapper;

  @Autowired
  public UserCreationConsumer(UserService userService,
                              ObjectMapper objectMapper) {
    this.userService = userService;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = {"${topic-user-creation-request}"})
  public void consumeUserCreation(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
    var parsedValue = objectMapper.readValue(message, UserTransitionRequest.class);
    LOGGER.info("Consumed message from Kafka: " + message);
    userService.createUser(parsedValue.userId());
    acknowledgment.acknowledge();
  }
}
