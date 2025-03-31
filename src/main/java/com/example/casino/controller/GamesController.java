package com.example.casino.controller;

import com.example.casino.model.ActiveGame;
import com.example.casino.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Получение списка всех игр",
            description = "Возвращает полный список доступных игр."
    )
    @GetMapping("/list")
    public List<String> getGameList() {
        return gameService.getAllGames();
    }


    @Operation(
            summary = "Получение списка активных игр",
            description = "Возвращает список игр, которые в данный момент активны."
    )
    @GetMapping("/active")
    public List<ActiveGame> getActiveGames() {
        return gameService.getActiveGames();
    }
}