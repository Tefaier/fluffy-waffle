package com.example.auction.models.repositories;

import com.example.auction.models.entities.Lot;
import com.example.auction.models.enums.LotState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
  List<Lot> findByFinishTimeLessThanAndLotState(Timestamp finishTime, LotState lotState);
}
