package com.example.casino.service;

import com.example.casino.model.User;
import com.example.casino.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class RewardService {

    @Autowired
    private UserRepository userRepository;

    private static final int DAILY_REWARD = 10;
    private static final int STREAK_REWARD_MULTIPLIER = 10;

    public String claimDailyReward(User user) {
        LocalDateTime now = LocalDateTime.now();
        if (user.getLastDailyReward() == null || Duration.between(user.getLastDailyReward(), now).toHours() >= 24) {
            user.setBalance(user.getBalance() + DAILY_REWARD);
            user.setLastDailyReward(now);
            userRepository.save(user);
            return "Получена дневная награда: +" + DAILY_REWARD + " баллов";
        } else {
            Duration remainingTime = Duration.between(now, user.getLastDailyReward().plusHours(24));
            long hours = remainingTime.toHours();
            long minutes = remainingTime.minusHours(hours).toMinutes();
            return "Дневная награда уже получена. Следующая награда доступна через " + hours + " часов " + minutes + " минут.";
        }
    }

    public String claimStreakReward(User user) {
        LocalDateTime now = LocalDateTime.now();
        if (user.getLastStreakReward() != null && user.getLastStreakReward().toLocalDate().isEqual(now.toLocalDate())) {
            return "Стрик награда уже получена сегодня.";
        } else if (user.getLastStreakReward() != null && user.getLastStreakReward().toLocalDate().isEqual(now.minusDays(1).toLocalDate())) {
            user.setStreak(user.getStreak() + 1);
        } else {
            user.setStreak(1);
        }

        int reward = user.getStreak() * STREAK_REWARD_MULTIPLIER;
        user.setBalance(user.getBalance() + reward);
        user.setLastStreakReward(now);
        userRepository.save(user);

        return "Получена стрик награда: +" + reward + " баллов (Стрик: " + user.getStreak() + ")";
    }
}
