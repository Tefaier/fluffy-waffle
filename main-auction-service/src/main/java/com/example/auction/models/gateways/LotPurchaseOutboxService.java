package com.example.auction.models.gateways;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.LotPurchaseResponse;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.services.LotService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.TransactionOutboxEntry;
import com.gruelbox.transactionoutbox.TransactionOutboxListener;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class LotPurchaseOutboxService implements TransactionOutboxListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(LotPurchaseOutboxService.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topicToSend;
  private final ObjectMapper objectMapper;
  private final LotService lotService;
  private final TransactionOutbox outbox;

  @Autowired
  public LotPurchaseOutboxService(KafkaTemplate<String, String> kafkaTemplate,
                                  @Value("${topic-lot-purchase-request}") String topicToSend,
                                  ObjectMapper objectMapper,
                                  LotService lotService,
                                  TransactionOutbox outbox) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicToSend = topicToSend;
    this.objectMapper = objectMapper;
    this.lotService = lotService;
    this.outbox = outbox;
  }

  public void pushRequestToKafka(LotPurchaseRequest request) throws JsonProcessingException {
    String message = objectMapper.writeValueAsString(request);
    CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topicToSend, String.valueOf(request.userId()), message);
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
      var lot = lotService.getLot(parsedValue.userId());
      lot.setLotState(parsedValue.isApproved() ? LotState.SOLD : LotState.REJECTED);
      acknowledgment.acknowledge();
    } catch (NoSuchElementException e) {
      acknowledgment.acknowledge();
    }
  }

  @Override
  @Transactional
  public void blocked(TransactionOutboxEntry entry, Throwable cause) {
    TransactionOutboxListener.super.blocked(entry, cause);
    lotService.getLot(((LotPurchaseRequest) entry.getInvocation().getArgs()[0]).userId()).setLotState(LotState.REJECTED);
  }

  @Scheduled(fixedDelayString = "${outbox-flush-frequency}")
  protected void flushOutbox() {
    outbox.flush();
  }
}
