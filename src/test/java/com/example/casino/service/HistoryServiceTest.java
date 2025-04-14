package com.example.casino.service;

import com.example.casino.dto.BetHistoryResponseDto;
import com.example.casino.model.*;
import com.example.casino.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoryServiceTest {

    @Mock
    private BetHistoryRepository betHistoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private GameHistoryRepository gameHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private HistoryService historyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserBetHistory() {
        User user = new User();
        user.setId(1);

        Game game = new Game();
        game.setId(1);
        game.setName("Слоты 3x3");

        BetHistory betHistory1 = new BetHistory();
        betHistory1.setId(1L);
        betHistory1.setUserId(1);
        betHistory1.setGameId(1);
        betHistory1.setBet(100.0);
        betHistory1.setWin(50.0);
        betHistory1.setDate(LocalDateTime.now());

        BetHistory betHistory2 = new BetHistory();
        betHistory2.setId(2L);
        betHistory2.setUserId(1);
        betHistory2.setGameId(1);
        betHistory2.setBet(200.0);
        betHistory2.setWin(100.0);
        betHistory2.setDate(LocalDateTime.now());

        List<BetHistory> betHistories = Arrays.asList(betHistory1, betHistory2);

        when(betHistoryRepository.findByUserId(1)).thenReturn(betHistories);

        List<BetHistoryResponseDto> result = historyService.getUserBetHistory(1);

        assertEquals(2, result.size());

        BetHistoryResponseDto dto1 = result.get(0);
        assertEquals(1, dto1.getId());
        assertEquals(100.0, dto1.getBet());
        assertEquals(50.0, dto1.getWin());
        assertNotNull(dto1.getDate());

        BetHistoryResponseDto dto2 = result.get(1);
        assertEquals(2, dto2.getId());
        assertEquals(200.0, dto2.getBet());
        assertEquals(100.0, dto2.getWin());
        assertNotNull(dto2.getDate());
    }

    @Test
    public void testGetUserTransactionHistory() {
        User user = new User();
        user.setId(1);

        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setUserId(1L);
        transaction1.setUsdt(100.0);
        transaction1.setDate(LocalDateTime.now());

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setUserId(1L);
        transaction2.setUsdt(200.0);
        transaction2.setDate(LocalDateTime.now());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findByUserId(1)).thenReturn(transactions);

        List<Transaction> result = historyService.getUserTransactionHistory(1);

        assertEquals(2, result.size());

        Transaction t1 = result.get(0);
        assertEquals(1L, t1.getId());
        assertEquals(1L, t1.getUserId());
        assertEquals(100.0, t1.getUsdt());
        assertNotNull(t1.getDate());

        Transaction t2 = result.get(1);
        assertEquals(2L, t2.getId());
        assertEquals(1L, t2.getUserId());
        assertEquals(200.0, t2.getUsdt());
        assertNotNull(t2.getDate());
    }

    @Test
    public void testGetUserIdByEmail() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Integer userId = historyService.getUserIdByEmail("test@example.com");

        assertEquals(1, userId.intValue());
    }

    @Test
    public void testGetUserIdByEmail_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            historyService.getUserIdByEmail("nonexistent@example.com");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetAllGameHistory() {
        GameHistory gameHistory1 = new GameHistory();
        gameHistory1.setId(1L);
        gameHistory1.setGameId(1L);
        gameHistory1.setBalance(100);
        gameHistory1.setResult("Win");
        gameHistory1.setParameters("{}");
        gameHistory1.setDate(LocalDateTime.now());

        GameHistory gameHistory2 = new GameHistory();
        gameHistory2.setId(2L);
        gameHistory2.setGameId(2L);
        gameHistory2.setBalance(200);
        gameHistory2.setResult("Lose");
        gameHistory2.setParameters("{}");
        gameHistory2.setDate(LocalDateTime.now());

        List<GameHistory> gameHistories = Arrays.asList(gameHistory1, gameHistory2);

        when(gameHistoryRepository.findAll()).thenReturn(gameHistories);

        List<GameHistory> result = historyService.getAllGameHistory();

        assertEquals(2, result.size());

        GameHistory gh1 = result.get(0);
        assertEquals(1L, gh1.getId());
        assertEquals(1L, gh1.getGameId());
        assertEquals(100, gh1.getBalance());
        assertEquals("Win", gh1.getResult());
        assertNotNull(gh1.getDate());

        GameHistory gh2 = result.get(1);
        assertEquals(2L, gh2.getId());
        assertEquals(2L, gh2.getGameId());
        assertEquals(200, gh2.getBalance());
        assertEquals("Lose", gh2.getResult());
        assertNotNull(gh2.getDate());
    }
}
