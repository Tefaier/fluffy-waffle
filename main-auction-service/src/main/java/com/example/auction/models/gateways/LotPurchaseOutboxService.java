package com.example.auction.models.gateways;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.LotPurchaseResponse;
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
public class LotPurchaseOutboxService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LotPurchaseOutboxService.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topicToSend;
  private final ObjectMapper objectMapper;

  @Autowired
  public LotPurchaseOutboxService(KafkaTemplate<String, String> kafkaTemplate,
                                  @Value("${topic-lot-purchase-request}") String topicToSend,
                                  ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicToSend = topicToSend;
    this.objectMapper = objectMapper;
  }

  public void pushRequestToKafka(LotPurchaseRequest request) throws JsonProcessingException {
    String message = objectMapper.writeValueAsString(request);
    CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topicToSend, String.valueOf(request.usedId()), message);
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
  protected void consumePurchaseResponse(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
    var parsedValue = objectMapper.readValue(message, LotPurchaseResponse.class);
    LOGGER.info("Consumed message from Kafka: " + message);
    acknowledgment.acknowledge();
  }
}
