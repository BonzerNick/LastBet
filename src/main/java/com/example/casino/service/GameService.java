package com.example.casino.service;

import com.example.casino.model.ActiveGame;
import com.example.casino.model.Game;
import com.example.casino.repository.ActiveGameRepository;
import com.example.casino.repository.GameRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ActiveGameRepository activeGameRepository;

    public GameService(GameRepository gameRepository, ActiveGameRepository activeGameRepository) {
        this.gameRepository = gameRepository;
        this.activeGameRepository = activeGameRepository;
    }

    // Получение всех игр
    public List<String> getAllGames() {
        List<Game> games = gameRepository.findAll();
        return games.stream()
                .map(game -> {
                    StringBuilder rulesBuilder = new StringBuilder();
                    rulesBuilder.append("Название игры: ").append(game.getName()).append("\n")
                            .append("Описание: ").append(game.getRules()).append("\n");

                    // Извлечение параметров, если они есть
                    String parameters = game.getParameters();
                    if (parameters != null && !parameters.isEmpty()) {
                        // Парсинг JSON
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode parametersNode = objectMapper.readTree(parameters);

                            // Добавление минимальной и максимальной ставки
                            if (parametersNode.has("min_bet") && parametersNode.has("max_bet")) {
                                rulesBuilder.append("Мин. ставка: ")
                                        .append(parametersNode.get("min_bet").asInt())
                                        .append(", Макс. ставка: ")
                                        .append(parametersNode.get("max_bet").asInt())
                                        .append("\n");
                            }

                            // Добавление времени одной игры
                            if (parametersNode.has("bet_time_seconds")) {
                                rulesBuilder.append("Время одной игры: ")
                                        .append(parametersNode.get("bet_time_seconds").asInt())
                                        .append(" секунд\n");
                            }

                            // Добавление шанса выпадения и коэффициентов выигрыша
                            if (parametersNode.has("chance_and_coefficient")) {
                                rulesBuilder.append("Шансы выпадения и коэффициенты выигрыша:\n");

                                JsonNode chanceNode = parametersNode.get("chance_and_coefficient");
                                chanceNode.fields().forEachRemaining(field -> {
                                    rulesBuilder.append("  Комбинация: ")
                                            .append(field.getKey())
                                            .append(" -> Шанс: ")
                                            .append(field.getValue().get(0).asInt())
                                            .append(", Коэффициент выигрыша: ")
                                            .append(field.getValue().get(1).asDouble())
                                            .append("\n");
                                });
                            }
                        } catch (Exception e) {
                            rulesBuilder.append("[Ошибка парсинга параметров игры]\n");
                        }
                    }
                    return rulesBuilder.toString();
                })
                .collect(Collectors.toList());
    }


    // Получение только активных игр
    public List<ActiveGame> getActiveGames() {
        return activeGameRepository.findAll();
    }
}