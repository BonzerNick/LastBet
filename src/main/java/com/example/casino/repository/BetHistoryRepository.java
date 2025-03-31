package com.example.casino.repository;

import com.example.casino.model.BetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetHistoryRepository extends JpaRepository<BetHistory, Long> {
    List<BetHistory> findByUserId(Integer userId);
}