package com.example.casino.repository;

import com.example.casino.model.ActiveGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActiveGameRepository extends JpaRepository<ActiveGame, Long> {
    // Найти все игры по типу (рулетка, ракетка и т.д.)
    @Query("SELECT ag FROM ActiveGame ag JOIN Game g ON ag.gameId = g.id WHERE g.name = :gameType")
    List<ActiveGame> findActiveGamesByGameType(String gameType);

    // Найти просроченные игры
    List<ActiveGame> findByFinished(boolean finished);

}