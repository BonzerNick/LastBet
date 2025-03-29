package com.example.casino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bets_history")
public class BetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "games_id", nullable = false)
    private Long gamesId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bet", nullable = false)
    private double bet;

}