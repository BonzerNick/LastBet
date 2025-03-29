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
@Table(name = "clients_token")
public class ClientToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false) // Поле user_id из таблицы
    private Long userId;

    @Column(name = "token", nullable = false) // Поле token из таблицы
    private String token;

    @Column(name = "iat", nullable = false) // Поле iat из таблицы
    private LocalDateTime issuedAt;

    @Column(name = "exp", nullable = false) // Поле exp из таблицы
    private LocalDateTime expirationDate;

}