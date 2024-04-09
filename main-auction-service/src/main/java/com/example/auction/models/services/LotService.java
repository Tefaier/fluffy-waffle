package com.example.auction.models.services;

import com.example.auction.models.DTOs.LotPurchaseRequest;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.User;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.exceptions.LotCreateException;
import com.example.auction.models.gateways.LotPurchaseOutboxService;
import com.example.auction.models.repositories.LotRepository;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.UUID;

@Service
public class LotService {
    private final LotRepository lotRepository;
    private final UserService userService;
    private final TransactionOutbox outbox;

    private final Duration LOT_FINISH_TIME_OFFSET = Duration.ofDays(7);

    @Autowired
    public LotService(LotRepository lotRepository, UserService userService, TransactionOutbox outbox) {
        this.lotRepository = lotRepository;
        this.userService = userService;
        this.outbox = outbox;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Lot getLot(Long lotId) {
        return lotRepository.findById(lotId).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long createLot(Long userId, Money initialPrice, Money minimumIncrease, Timestamp startTime, Timestamp finishTime, String description, String[] images) {
        User user = userService.getUser(userId);
        images = Arrays.stream(images).filter(this::validateImage).toArray(String[]::new);
        Lot lot = new Lot(user, initialPrice, minimumIncrease, startTime, finishTime, description, images);
        if (validateLot(lot)) {
            user.addLot(lot);
            lotRepository.save(lot);
            return lot.getId();
        } else {
            // exception doesn't provide explicit information and should probably be thrown from somewhere else
            throw new LotCreateException("Can't create lot with provided parameters");
        }
    }

    private boolean validateLot(Lot lot) {
        if (lot.getFinishTime().before(lot.getStartTime())) {
            return false;
        }
        if (lot.getFinishTime().before(Timestamp.from(Instant.now().plus(LOT_FINISH_TIME_OFFSET)))) {
            return false;
        }
        return true;
    }

    private boolean validateImage(String imageUrl) {
        // may not work and be updated
        return Pattern.matches("https?://.+\\.(jpg|png)", imageUrl);
    }

    @Transactional
    @Scheduled(fixedDelayString = "${finished-lots-check-delay}")
    protected void processFinishedLots() {
        var lotsToProcess = lotRepository.findByFinishTimeLessThanAndLotState(Timestamp.from(Instant.now()), LotState.NOT_SOLD);
        for (var lot : lotsToProcess) {
            lot.setLotState(LotState.IN_PROGRESS);
            lotRepository.save(lot);
            outbox.with().ordered("justonetopic").schedule(LotPurchaseOutboxService.class).pushRequestToKafka(new LotPurchaseRequest(UUID.randomUUID().toString(), lot.getUser().getId(), ));
        }
    }
}
