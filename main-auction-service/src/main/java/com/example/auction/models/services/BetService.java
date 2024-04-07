package com.example.auction.models.services;

import com.example.auction.models.entities.Bet;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.Money;
import com.example.auction.models.entities.User;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.exceptions.InvalidBetException;
import com.example.auction.models.repositories.BetRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class BetService {
    private final BetRepository betRepository;
    private final UserService userService;
    private final LotService lotService;
    private final EntityManager entityManager;

    @Autowired
    public BetService(BetRepository betRepository, UserService userService, LotService lotService, EntityManager entityManager) {
        this.betRepository = betRepository;
        this.userService = userService;
        this.lotService = lotService;
        this.entityManager = entityManager;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Bet getBet(long betId) {
        return betRepository.findById(betId).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long makeBet(long userId, long lotId, Money value) {
        User user = userService.getUser(userId);
        Lot lot = lotService.getLot(lotId);
        entityManager.lock(user, LockModeType.PESSIMISTIC_WRITE);
        entityManager.lock(lot, LockModeType.PESSIMISTIC_WRITE);
        if (validateLot(user, lot, value)) {
            Bet bet = new Bet(user, lot, value);
            user.addBet(bet);
            lot.addLotBet(bet);
            betRepository.save(bet);
            return bet.getId();
        } else {
            // exception doesn't provide explicit information and should probably be thrown from somewhere else
            throw new InvalidBetException("Can't create bet with provided parameters");
        }
    }

    private boolean validateLot(User user, Lot lot, Money value) {
        if (user.getMoney().compareTo(value) < 0) {
            return false;
        }
        if (lot.getMinimumIncrease().compareTo(value) > 0) {
            return false;
        }
        if (!(lot.getLotState() == LotState.NOT_SOLD && lot.getFinishTime().toInstant().isBefore(Instant.now()))) {
            return false;
        }
        return true;
    }
}
