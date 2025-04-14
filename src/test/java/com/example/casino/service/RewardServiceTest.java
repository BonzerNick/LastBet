package com.example.casino.service;

import com.example.casino.model.User;
import com.example.casino.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RewardServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RewardService rewardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClaimDailyReward_Success() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);
        user.setLastDailyReward(LocalDateTime.now().minusHours(25));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = rewardService.claimDailyReward(user);

        assertEquals("Получена дневная награда: +10 баллов", result);
        assertEquals(110.0, user.getBalance());
        assertNotNull(user.getLastDailyReward());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testClaimDailyReward_TooSoon() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);
        user.setLastDailyReward(LocalDateTime.now().minusHours(23));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = rewardService.claimDailyReward(user);

        assertTrue(result.contains("Следующая награда доступна через"));
        assertEquals(100.0, user.getBalance());
        assertNotNull(user.getLastDailyReward());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testClaimStreakReward_Success() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);
        user.setLastStreakReward(LocalDateTime.now().minusDays(1));
        user.setStreak(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = rewardService.claimStreakReward(user);

        assertEquals("Получена стрик награда: +30 баллов (Стрик: 3)", result);
        assertEquals(130.0, user.getBalance());
        assertNotNull(user.getLastStreakReward());
        assertEquals(3, user.getStreak());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testClaimStreakReward_AlreadyClaimedToday() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);
        user.setLastStreakReward(LocalDateTime.now());
        user.setStreak(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = rewardService.claimStreakReward(user);

        assertEquals("Стрик награда уже получена сегодня.", result);
        assertEquals(100.0, user.getBalance());
        assertNotNull(user.getLastStreakReward());
        assertEquals(2, user.getStreak());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testClaimStreakReward_ResetStreak() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);
        user.setLastStreakReward(LocalDateTime.now().minusDays(2));
        user.setStreak(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = rewardService.claimStreakReward(user);

        assertEquals("Получена стрик награда: +10 баллов (Стрик: 1)", result);
        assertEquals(110.0, user.getBalance());
        assertNotNull(user.getLastStreakReward());
        assertEquals(1, user.getStreak());
        verify(userRepository, times(1)).save(user);
    }
}
