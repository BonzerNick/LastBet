package com.example.casino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games_history")
public class GameHistory {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(name = "game_id", nullable = false)
     private Long gameId;

     @Column(name = "result")
     private String result;

     @Column(name = "balance", nullable = false)
     private double balance;

     @Column(name = "parameters", columnDefinition = "jsonb")
     private String parameters;


}