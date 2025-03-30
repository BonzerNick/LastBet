package com.example.casino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games_list")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "rules")
    private String rules;

    @Column(name = "parameters", columnDefinition = "jsonb")
    private String parameters;

    @Column(name = "bet_time_seconds")
    private Integer betTimeSeconds;

    @Column(name = "min_bet", nullable = false)
    private Double minBet;

    @Column(name = "max_bet", nullable = false)
    private Double maxBet;

}