package com.example.casino.service;

import com.example.casino.model.Transaction;
import com.example.casino.model.User;
import com.example.casino.repository.TransactionRepository;
import com.example.casino.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BalanceServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BalanceService balanceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateBalance_SuccessfulDeposit() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);

        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));

        balanceService.updateBalance(1, 50.0, "Card");

        assertEquals(150.0, user.getBalance());
        verify(userRepository, times(1)).save(user);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        Transaction transaction = transactionCaptor.getValue();
        assertEquals(1L, transaction.getUserId());
        assertEquals(50.0, transaction.getUsdt());
        assertNotNull(transaction.getDate());
    }

    @Test
    public void testUpdateBalance_SuccessfulWithdrawal() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);

        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));

        balanceService.updateBalance(1, -30.0, "Bank Transfer");

        assertEquals(70.0, user.getBalance());
        verify(userRepository, times(1)).save(user);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        Transaction transaction = transactionCaptor.getValue();
        assertEquals(1L, transaction.getUserId());
        assertEquals(-30.0, transaction.getUsdt());
        assertNotNull(transaction.getDate());
    }

    @Test
    public void testUpdateBalance_InsufficientFunds() {
        User user = new User();
        user.setId(1);
        user.setBalance(50.0);

        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            balanceService.updateBalance(1, -60.0, "Card");
        });

        assertEquals("Insufficient funds for transaction", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    public void testUpdateBalance_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            balanceService.updateBalance(1, 50.0, "Card");
        });

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }
}
