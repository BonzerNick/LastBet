package com.example.casino.service;

import com.example.casino.model.ActiveGame;
import com.example.casino.model.Game;
import com.example.casino.repository.ActiveGameRepository;
import com.example.casino.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ActiveGameRepository activeGameRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllGames() {
        Game game1 = new Game();
        game1.setName("Слоты 3x3");
        game1.setRules("Правила игры в слоты");
        game1.setParameters("{\"min_bet\":10,\"max_bet\":1000,\"chance_and_coefficient\":{\"1\":[10,2.0],\"2\":[20,1.5]}}");

        Game game2 = new Game();
        game2.setName("Рулетка");
        game2.setRules("Правила игры в рулетку");
        game2.setParameters("{\"min_bet\":5,\"max_bet\":500,\"chance_and_coefficient\":{\"1\":[10,3.0],\"2\":[20,2.5]}}");

        List<Game> games = Arrays.asList(game1, game2);
        when(gameRepository.findAll()).thenReturn(games);

        List<String> result = gameService.getAllGames();

        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("Слоты 3x3"));
        assertTrue(result.get(0).contains("Правила игры в слоты"));
        assertTrue(result.get(0).contains("Мин. ставка: 10, Макс. ставка: 1000"));
        assertTrue(result.get(0).contains("Шансы выпадения и коэффициенты выигрыша:"));
        assertTrue(result.get(0).contains("  Комбинация: 1 -> Шанс: 10, Коэффициент выигрыша: 2.0"));
        assertTrue(result.get(0).contains("  Комбинация: 2 -> Шанс: 20, Коэффициент выигрыша: 1.5"));

        assertTrue(result.get(1).contains("Рулетка"));
        assertTrue(result.get(1).contains("Правила игры в рулетку"));
        assertTrue(result.get(1).contains("Мин. ставка: 5, Макс. ставка: 500"));
        assertTrue(result.get(1).contains("Шансы выпадения и коэффициенты выигрыша:"));
        assertTrue(result.get(1).contains("  Комбинация: 1 -> Шанс: 10, Коэффициент выигрыша: 3.0"));
        assertTrue(result.get(1).contains("  Комбинация: 2 -> Шанс: 20, Коэффициент выигрыша: 2.5"));
    }

    @Test
    public void testGetActiveGames() {
        ActiveGame activeGame1 = new ActiveGame();
        activeGame1.setGameId(1);
        activeGame1.setStartDate(LocalDateTime.now());
        activeGame1.setEndDate(LocalDateTime.now().plusSeconds(60));
        activeGame1.setFinished(false);

        ActiveGame activeGame2 = new ActiveGame();
        activeGame2.setGameId(2);
        activeGame2.setStartDate(LocalDateTime.now());
        activeGame2.setEndDate(LocalDateTime.now().plusSeconds(60));
        activeGame2.setFinished(false);

        List<ActiveGame> activeGames = Arrays.asList(activeGame1, activeGame2);
        when(activeGameRepository.findAll()).thenReturn(activeGames);

        List<ActiveGame> result = gameService.getActiveGames();

        assertEquals(2, result.size());
    }
}

