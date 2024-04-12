package com.example.auction.models.repositories;

import com.example.auction.models.entities.Request;
import com.example.auction.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository  extends JpaRepository<Request, Long> {

  Optional<Request> findById(UUID id);
}
