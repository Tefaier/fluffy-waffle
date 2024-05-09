package com.example.auction.models.gateways;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.LotPurchaseResponse;
import com.example.auction.models.entities.Money;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.services.LotService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class LotPurchaseOutboxService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LotPurchaseOutboxService.class);

  private static LotPurchaseOutboxService createdComponent;

  private KafkaTemplate<String, String> kafkaTemplate;
  private String topicToSend;
  private ObjectMapper objectMapper;
  private LotService lotService;

  @Autowired
  public LotPurchaseOutboxService(KafkaTemplate<String, String> kafkaTemplate,
                                  @Value("${topic-lot-purchase-request}") String topicToSend,
                                  ObjectMapper objectMapper,
                                  @Lazy LotService lotService) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicToSend = topicToSend;
    this.objectMapper = objectMapper;
    this.lotService = lotService;
    LotPurchaseOutboxService.createdComponent = this;
  }

  public void pushPurchaseRequestToKafka(UUID requestId, UUID userId, Long lotId, UUID lotOwnerId, Money value) throws JsonProcessingException {
    if (this != createdComponent) {
      createdComponent.pushPurchaseRequestToKafka(requestId, userId, lotId, lotOwnerId, value);
      return;
    }
    String message = objectMapper.writeValueAsString(new LotPurchaseRequest(requestId, userId, lotId, lotOwnerId, value));
    CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topicToSend, String.valueOf(userId), message);
    try {
      sendResult.get(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.warn("Thread was interrupted while message being sent to Kafka");
      throw new IllegalStateException("Unexpected thread interruption", e);
    } catch (ExecutionException | TimeoutException e) {
      LOGGER.warn("Failed to send message to Kafka with message: " + message);
      throw new IllegalStateException("Failed to send to Kafka", e);
    }
  }

  @KafkaListener(topics = {"${topic-lot-purchase-result}"})
  @Transactional
  protected void consumePurchaseResponse(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
    var parsedValue = objectMapper.readValue(message, LotPurchaseResponse.class);
    LOGGER.info("Consumed message from Kafka: " + message);
    try {
      var lot = lotService.getLot(parsedValue.lotId());
      lot.setLotState(parsedValue.isApproved() ? LotState.SOLD : LotState.REJECTED);
      acknowledgment.acknowledge();
    } catch (NoSuchElementException e) {
      acknowledgment.acknowledge();
    }
  }
}
