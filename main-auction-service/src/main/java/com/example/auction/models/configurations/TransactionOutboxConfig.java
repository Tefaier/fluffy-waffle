package com.example.auction.models.configurations;

import com.gruelbox.transactionoutbox.Dialect;
import com.gruelbox.transactionoutbox.Persistor;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.spring.SpringInstantiator;
import com.gruelbox.transactionoutbox.spring.SpringTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionOutboxConfig {
  @Bean
  public TransactionOutbox transactionOutbox(SpringTransactionManager springTransactionManager,
                                             SpringInstantiator springInstantiator) {
    return TransactionOutbox.builder()
        .instantiator(springInstantiator)
        .transactionManager(springTransactionManager)
        .persistor(Persistor.forDialect(Dialect.H2))
        .build();
  }
}
