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
@Table(name = "unconfirmed_users")
public class UnconfirmedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_2fa", nullable = false)
    private boolean isTwoFactorEnabled = false;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(nullable = false)
    private String language;

    @Column(name = "confirmation_hash", nullable = false, unique = true)
    private String confirmationHash;

}