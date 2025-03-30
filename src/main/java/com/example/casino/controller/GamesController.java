package com.example.casino.controller;

import com.example.casino.model.ActiveGame;
import com.example.casino.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GamesController {

    private final GameService gameService;

    public GamesController(GameService gameService) {
        this.gameService = gameService;
    }

    // Получение полного списка игр
    @GetMapping("/list")
    public List<String> getGameList() {
        return gameService.getAllGames();
    }


    // Получение активных игр
    @GetMapping("/active")
    public List<ActiveGame> getActiveGames() {
        return gameService.getActiveGames();
    }
}