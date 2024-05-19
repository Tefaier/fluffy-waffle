package com.example.auction.models.services;

import com.example.auction.models.entities.Bet;
import com.example.auction.models.entities.Lot;
import com.example.auction.models.entities.User;
import com.example.auction.models.enums.LotState;
import com.example.auction.models.enums.Role;
import com.example.auction.models.gateways.UserCreationOutboxService;
import com.example.auction.models.repositories.UserRepository;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TransactionOutbox outbox;
    private final UserCreationOutboxService userOutbox;
    private final BetService betService;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy TransactionOutbox outbox, UserCreationOutboxService userOutbox, @Lazy BetService betService) {
        this.userRepository = userRepository;
        this.outbox = outbox;
        this.userOutbox = userOutbox;
        this.betService = betService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User getUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User getUser(String login) {
        return userRepository.findByLogin(login).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Retry(name = "user-generation")
    public UUID createUser(String login, String firstName, String lastName, String passwordHash, String email) {
        User user = new User(login, firstName, lastName, passwordHash, Set.of(Role.USER), email);
        userRepository.save(user);
        outbox
            .with()
            .schedule(userOutbox.getClass())
            .pushUserCreationRequestToKafka(user.getId());
        return user.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByLogin(username)
            .map(user ->
                org.springframework.security.core.userdetails.User.withUsername(username)
                    .authorities(user.getRoles().stream()
                        .map(Enum::name)
                        .map(SimpleGrantedAuthority::new)
                        .toList()
                    )
                    .password(user.getPasswordHash())
                    .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public enum LotUserStatus{
        participates, // все лоты в торгах которых пользователь сейчас участвует
        participatesLoses, // все лоты в торгах которых пользователь участвует, но не выигрывает
        participatesWins, // все лоты в торгах которых пользователь участвует и выигрывает сейчас
        lost, // проигранные лоты
        awaitsPayment, // в процессе оплаты
        bought, // купленные
        failedToBuy, // выигранные но не купленные из-за недостатка средств
        owns, // все лоты юзера
        ownsAwaits, // все лоты юзера которые ждут начала торгов
        ownsInProgress, // все лоты юзера которые в процессе торгов
        ownsAwaitsPayment, // все лоты юзера по которым оплата идет
        ownsSold, // все лоты юзера которые проданы
        ownsUnsold // все лоты юзера которые закончились некупленными
    }
    @Transactional(readOnly = true)
    public Map<LotUserStatus, List<Lot>> getRelatedLots(UUID userId) {
        var currentTime = Timestamp.from(Instant.now());
        Map<LotUserStatus, List<Lot>> result = new HashMap<>();
        var user = getUser(userId);
        var userLots = user.getLots();
        var userBets = user.getBets();
        var userBettedLots = userBets.stream().map(Bet::getLot).distinct().toList();
        var userBettedLotsTopIsUser =
            userBettedLots.stream()
                .collect(
                    Collectors.toMap(
                        lot -> lot,
                        lot ->
                            betService
                                .getHighestValueBet(lot.getLotBets())
                                .getUser().getId().equals(userId)
                    ));
        var userBettedLotsFinished = userBettedLots.stream().filter(lot -> lot.getFinishTime().before(currentTime)).toList();
        var userBettedLotsWon = userBettedLotsFinished.stream().filter(lot -> userBettedLotsTopIsUser.get(lot)).toList();

        result.put(LotUserStatus.owns, userLots);
        result.put(LotUserStatus.ownsAwaits, userLots.stream().filter(lot -> lot.getStartTime().after(currentTime)).toList());
        result.put(LotUserStatus.ownsInProgress, userLots.stream().filter(lot -> lot.getStartTime().before(currentTime) && lot.getFinishTime().after(currentTime)).toList());
        result.put(LotUserStatus.ownsAwaitsPayment, userLots.stream().filter(lot -> lot.getLotState().equals(LotState.IN_PROGRESS)).toList());
        result.put(LotUserStatus.ownsSold, userLots.stream().filter(lot -> lot.getLotState().equals(LotState.SOLD)).toList());
        result.put(LotUserStatus.ownsUnsold, userLots.stream().filter(lot -> lot.getLotState().equals(LotState.UNSOLD)).toList());

        result.put(LotUserStatus.participates, userBettedLots.stream().filter(lot -> lot.getFinishTime().after(currentTime)).toList());
        result.put(LotUserStatus.participatesLoses, result.get(LotUserStatus.participates).stream().filter(lot -> !userBettedLotsTopIsUser.get(lot)).toList());
        result.put(LotUserStatus.participatesWins, result.get(LotUserStatus.participates).stream().filter(lot -> userBettedLotsTopIsUser.get(lot)).toList());


        result.put(LotUserStatus.lost, userBettedLotsFinished.stream().filter(lot -> !userBettedLotsTopIsUser.get(lot)).toList());
        result.put(LotUserStatus.awaitsPayment, userBettedLotsWon.stream().filter(lot -> lot.getLotState().equals(LotState.IN_PROGRESS)).toList());
        result.put(LotUserStatus.bought, userBettedLotsWon.stream().filter(lot -> lot.getLotState().equals(LotState.SOLD)).toList());
        result.put(LotUserStatus.failedToBuy, userBettedLotsWon.stream().filter(lot -> lot.getLotState().equals(LotState.REJECTED)).toList());

        return result;
    }
}
