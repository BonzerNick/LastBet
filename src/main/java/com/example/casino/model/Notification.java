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
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false) // Поле user_id из таблицы
    private Long userId;

    @Column(name = "datetime", nullable = false) // Поле datetime из таблицы
    private LocalDateTime datetime;

    @Column(name = "body", nullable = false) // Поле body из таблицы
    private String body;

    @Column(name = "subject") // Поле subject из таблицы
    private String subject;

}