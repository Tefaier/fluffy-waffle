package com.example.auction.models.gateways;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.DTOs.LotPurchaseResponse;
import com.example.auction.models.DTOs.UserTransitionRequest;
import com.example.auction.models.enums.LotState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.TransactionOutboxEntry;
import com.gruelbox.transactionoutbox.TransactionOutboxListener;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

public class OutboxControlService implements TransactionOutboxListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxControlService.class);

  private final TransactionOutbox outbox;

  @Autowired
  public OutboxControlService(TransactionOutbox outbox) {
    this.outbox = outbox;
  }

  @Override
  @Transactional
  public void blocked(TransactionOutboxEntry entry, Throwable cause) {
    TransactionOutboxListener.super.blocked(entry, cause);
    var methodName = entry.getInvocation().getMethodName();
    var arguments = entry.getInvocation().getArgs();
    switch (methodName) {
      case ("pushPurchaseResponseToKafka") -> {
        LOGGER.error("Failed to push payment result: " + ((LotPurchaseResponse) arguments[0]).lotId() + ", will unblock entry and try again");
        entry.setBlocked(false);
      }
    }
  }

  @Scheduled(fixedDelayString = "${outbox-flush-frequency}")
  protected void flushOutbox() {
    outbox.flush();
  }
}
