package com.example.auction.models.gateways;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.UserTransitionRequest;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.services.LotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.TransactionOutboxEntry;
import com.gruelbox.transactionoutbox.TransactionOutboxListener;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OutboxControlService implements TransactionOutboxListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxControlService.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topicToSend;
  private final ObjectMapper objectMapper;
  private final LotService lotService;
  private final TransactionOutbox outbox;

  @Autowired
  public OutboxControlService(KafkaTemplate<String, String> kafkaTemplate,
                              @Value("${topic-lot-purchase-request}") String topicToSend,
                              ObjectMapper objectMapper,
                              LotService lotService,
                              @Lazy TransactionOutbox outbox) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicToSend = topicToSend;
    this.objectMapper = objectMapper;
    this.lotService = lotService;
    this.outbox = outbox;
  }

  @Override
  @Transactional
  public void blocked(TransactionOutboxEntry entry, Throwable cause) {
    TransactionOutboxListener.super.blocked(entry, cause);
    var methodName = entry.getInvocation().getMethodName();
    var arguments = entry.getInvocation().getArgs();
    switch (methodName) {
      case ("pushPurchaseRequestToKafka") -> {
        lotService.getLot(((LotPurchaseRequest) arguments[0]).lotId()).setLotState(LotState.REJECTED);
      }
      case ("pushUserCreationRequestToKafka") -> {
        LOGGER.warn("Failed to push creation of user to sub services: " + ((UserTransitionRequest) arguments[0]).userId() + ", will unblock entry and try again");
        entry.setBlocked(false);
      }
    }
  }

  @Scheduled(fixedDelayString = "${outbox-flush-frequency}")
  protected void flushOutbox() {
    outbox.flush();
  }
}