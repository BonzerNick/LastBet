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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private double balance;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_2fa", nullable = false)
    private boolean isTwoFactorAuthEnabled;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "last_visit")
    private LocalDateTime lastVisit;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) default 'USER'")
    private String role;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "last_daily_reward")
    private LocalDateTime lastDailyReward;

    @Column(name = "last_streak_reward")
    private LocalDateTime lastStreakReward;

    @Column(name = "streak")
    private int streak;

    public boolean twoFactorEnabled() {
        return isTwoFactorAuthEnabled;
    }

    public String twoFactorSecret() {
        return secretKey;
    }

}