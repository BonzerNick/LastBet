package com.example.casino.controller;

import com.example.casino.model.ActiveGame;
import com.example.casino.service.GameService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ActiveGamesSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    // Отправка обновлений об активных играх каждую 1 секунду
    @Scheduled(fixedRate = 1000)
    @SendTo("/topic/active-games")
    public Object sendActiveGamesUpdate() {
        List<ActiveGame> activeGames = gameService.getActiveGames();
        try {
            messagingTemplate.convertAndSend("/topic/active-games", activeGames);
        } catch (Exception e) {
            log.error("Ошибка сериализации JSON: ", e);
        }
        return activeGames;
    }
}