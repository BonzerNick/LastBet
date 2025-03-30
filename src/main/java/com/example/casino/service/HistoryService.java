package com.example.casino.service;

import com.example.casino.dto.BetHistoryResponseDto;
import com.example.casino.model.BetHistory;
import com.example.casino.model.Game;
import com.example.casino.model.Transaction;
import com.example.casino.model.User;
import com.example.casino.repository.BetHistoryRepository;
import com.example.casino.repository.GameRepository;
import com.example.casino.repository.TransactionRepository;
import com.example.casino.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final BetHistoryRepository betHistoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;


    public HistoryService(
            BetHistoryRepository betHistoryRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            GameRepository gameRepository
    ) {
        this.betHistoryRepository = betHistoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    // Получить историю ставок пользователя по userId
    public List<BetHistoryResponseDto> getUserBetHistory(Integer userId) {
        // Получаем историю ставок
        List<BetHistory> betHistories = betHistoryRepository.findByUserId(userId);

        // Преобразуем каждую запись в DTO с использованием GameRepository для получения названия игры
        return betHistories.stream().map(bet -> {
            Optional<Game> game = gameRepository.findById(bet.getGameId()); // Ищем игру по gameId
            String gameName = gameRepository.findById(bet.getGameId())
                    .map(Game::getName) // Извлекаем название игры, если запись найдена
                    .orElse("Unknown Game"); // Если игру не нашли, вернуть "Unknown Game"

            return new BetHistoryResponseDto(
                    Math.toIntExact(bet.getId()),
                    gameName,
                    bet.getBet(),
                    bet.getWin(),
                    bet.getDate()
            );
        }).collect(Collectors.toList());
    }


    // Получить историю транзакций пользователя по userId
    public List<Transaction> getUserTransactionHistory(Integer userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Integer getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }


}