package com.example.casino.service;

import com.example.casino.model.Transaction;
import com.example.casino.model.User;
import com.example.casino.repository.TransactionRepository;
import com.example.casino.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BalanceService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BalanceService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void updateBalance(Integer userId, double amount, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Рассчитываем новый баланс на основе операции
        double newBalance = user.getBalance() + amount;

        if (newBalance < 0) {
            throw new IllegalArgumentException("Insufficient funds for transaction");
        }

        // Обновляем баланс пользователя
        user.setBalance(newBalance);
        userRepository.save(user);

        // Создаем запись о транзакции
        Transaction transaction = new Transaction();
        transaction.setUserId((long) userId);
        transaction.setUsdt(amount);
        transaction.setDate(LocalDateTime.now());

        // Сохраняем транзакцию в базе данных
        transactionRepository.save(transaction);
    }
}