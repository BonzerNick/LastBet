package com.example.casino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bets_history")
public class BetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "bet", nullable = false)
    private double bet;

    @Column(nullable = false)
    private Double win;

    @Column(nullable = false)
    private LocalDateTime date;

}