package com.example.auction.models.configurations;

import com.example.auction.models.gateways.LotPurchaseOutboxService;
import com.example.auction.models.gateways.OutboxControlService;
import com.gruelbox.transactionoutbox.Dialect;
import com.gruelbox.transactionoutbox.Persistor;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.spring.SpringInstantiator;
import com.gruelbox.transactionoutbox.spring.SpringTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

@Configuration
@ComponentScan(value = "com.example.auction.models.gateways")
@Import({SpringTransactionManager.class, SpringInstantiator.class})
public class TransactionOutboxConfig {
  @Autowired
  private OutboxControlService outboxControlService;

  @Bean
  public TransactionOutbox transactionOutbox(SpringTransactionManager springTransactionManager,
                                             SpringInstantiator springInstantiator,
                                             @Value("${outbox-attempt-frequency}") Duration attemptFrequency) {
    return TransactionOutbox.builder()
            .instantiator(springInstantiator)
            .transactionManager(springTransactionManager)
            .persistor(Persistor.forDialect(Dialect.POSTGRESQL_9))
            .attemptFrequency(attemptFrequency)
            .blockAfterAttempts(5)
            .listener(outboxControlService)
            .build();
  }
}