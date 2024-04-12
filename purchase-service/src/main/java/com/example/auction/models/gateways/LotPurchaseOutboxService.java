package com.example.auction.models.gateways;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.LotPurchaseResponse;
import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.Request;
import com.example.auction.models.entities.User;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.exceptions.RequestAlreadyProcessedException;
import com.example.auction.models.repositories.RequestRepository;
import com.example.auction.models.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import jakarta.persistence.LockModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class LotPurchaseOutboxService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LotPurchaseOutboxService.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topicToSend;
  private final ObjectMapper objectMapper;
  private final UserService userService;
  private final TransactionOutbox outbox;
  private final RequestRepository requestRepository;

  @Autowired
  public LotPurchaseOutboxService(KafkaTemplate<String, String> kafkaTemplate,
                                  @Value("${topic-lot-purchase-result}") String topicToSend,
                                  ObjectMapper objectMapper,
                                  UserService userService,
                                  TransactionOutbox outbox,
                                  RequestRepository requestRepository) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicToSend = topicToSend;
    this.objectMapper = objectMapper;
    this.userService = userService;
    this.outbox = outbox;
    this.requestRepository = requestRepository;
  }

  public void pushPurchaseResponseToKafka(LotPurchaseResponse request) throws JsonProcessingException {
    String message = objectMapper.writeValueAsString(request);
    CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topicToSend, message);
    try {
      sendResult.get(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.warn("Thread was interrupted while message being sent to Kafka");
      throw new IllegalStateException("Unexpected thread interruption", e);
    } catch (ExecutionException | TimeoutException e) {
      LOGGER.warn("Failed to send message to Kafka with message: " + message);
      throw new IllegalStateException("Failed to send to Kafka", e);
    }
  }

  @KafkaListener(topics = {"${topic-lot-purchase-request}"})
  @Transactional
  protected void consumePurchaseRequest(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
    var parsedValue = objectMapper.readValue(message, LotPurchaseRequest.class);
    LOGGER.info("Consumed message from Kafka: " + message);

    if (requestRepository.findById(parsedValue.requestId()).isPresent()) {
      acknowledgment.acknowledge();
      return;
    }

    try {
      makePurchase(parsedValue);
    } catch (RuntimeException e) {
      requestRepository.save(new Request(parsedValue.requestId()));
      outbox
          .with()
          .schedule(LotPurchaseOutboxService.class)
          .pushPurchaseResponseToKafka(new LotPurchaseResponse(parsedValue.userId(), parsedValue.lotId(), Boolean.FALSE));
    }
    acknowledgment.acknowledge();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  private void makePurchase(LotPurchaseRequest request) throws JsonProcessingException {
    requestRepository.save(new Request(request.requestId()));
    userService.subtractMoney(userService.getUser(request.userId()).getId(), request.value());
    outbox
        .with()
        .schedule(LotPurchaseOutboxService.class)
        .pushPurchaseResponseToKafka(new LotPurchaseResponse(request.userId(), request.lotId(), Boolean.TRUE));
  }
}
