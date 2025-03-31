package com.example.casino.service;

import com.example.casino.dto.SlotsResponseDto;
import com.example.casino.model.User;
import com.example.casino.model.Game;
import com.example.casino.model.BetHistory;
import com.example.casino.repository.GameRepository;
import com.example.casino.repository.BetHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SlotsService {

    private final UserService userService;
    private final GameRepository gameRepository;
    private final BetHistoryRepository betHistoryRepository;
    private final GameHistoryService gameHistoryService;

    public SlotsService(UserService userService,
                        GameRepository gameRepository,
                        BetHistoryRepository betHistoryRepository,
                        GameHistoryService gameHistoryService
                        ) {
        this.userService = userService;
        this.gameRepository = gameRepository;
        this.betHistoryRepository = betHistoryRepository;
        this.gameHistoryService = gameHistoryService;
    }


    public SlotsResponseDto playSlots(int bet, String email) {
        // Получаем пользователя и баланс
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким именем не найден"));

        // Получаем данные по игре "Слоты"
        Game slotsGame = gameRepository.findByName("Слоты 3x3") // предположим, что имя игры - "Слоты 3x3"
                .orElseThrow(() -> new IllegalArgumentException("Игра Слоты не найдена"));

        // Проверка параметров JSON (извлечение min_bet, max_bet, и шанс/коэффициент)
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode parametersNode;
        try {
            parametersNode = objectMapper.readTree(slotsGame.getParameters());
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка парсинга параметров игры");
        }

        JsonNode chanceAndCoefficientNode = parametersNode.get("chance_and_coefficient");

        // Проверяем, что ставка соответствует лимитам
        if (bet < slotsGame.getMinBet() || bet > slotsGame.getMaxBet()) {
            throw new IllegalArgumentException(String.format(
                    "Ставка должна быть в пределах от %.2f до %.2f",
                    slotsGame.getMinBet(), slotsGame.getMaxBet()
            ));
        }


        // Проверяем, хватает ли баланса у пользователя
        if (user.getBalance() < bet) {
            throw new IllegalArgumentException("Недостаточно баланса для совершения ставки");
        }

        // Списываем баланс
        userService.decreaseBalance(user, bet);

        // Генерируем 3 ряда по 3 случайных числа
        List<List<Integer>> results = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 3; i++) { // Генерируем 3 ряда
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) { // Генерируем 3 числа в ряду
                int generatedNumber = generateNumberWithChance(chanceAndCoefficientNode, random);
                row.add(generatedNumber);
            }
            results.add(row);
        }

        // Проверяем на наличие трех одинаковых чисел в одном ряду
        double winnings = 0.0;
        for (List<Integer> row : results) {
            if (row.get(0).equals(row.get(1)) && row.get(1).equals(row.get(2))) {
                int winningNumber = row.get(0);
                double coefficient = chanceAndCoefficientNode.get(String.valueOf(winningNumber)).get(1).asDouble();
                winnings += (bet / 3.0) * coefficient;
            }
        }

        // Сохранение результата в таблице bets_history
        BetHistory betHistory = new BetHistory();
        betHistory.setGameId(slotsGame.getId());
        betHistory.setUserId(user.getId());
        betHistory.setBet((double) bet);
        betHistory.setWin(winnings);
        betHistory.setDate(LocalDateTime.now()); // Время игры — текущий момент

        betHistoryRepository.save(betHistory);

        // Сохранение информации в games_history
        // Создаём корневой JSON-объект
        ObjectNode parameters = objectMapper.createObjectNode();

        // Добавляем вложенные данные
        parameters.put("user_id", user.getId()); // Пример: добавляем user_id
        parameters.put("results", results.toString());

        gameHistoryService.saveGameHistory(1, String.valueOf(winnings), bet, parameters.toString());


        // Если есть выигрыш, добавляем его пользователю
        if (winnings > 0) {
            userService.increaseBalance(user, winnings);
        }

        // Возвращаем результаты и выигрыш
        return new SlotsResponseDto(results, winnings);
    }

    // Метод генерации числа на основе вероятностей
    private int generateNumberWithChance(JsonNode chanceAndCoefficientNode, Random random) {
        Map<Integer, Integer> chances = new HashMap<>();

        // Собираем сгруппированные вероятности
        chanceAndCoefficientNode.fieldNames().forEachRemaining(key -> {
            int chance = chanceAndCoefficientNode.get(key).get(0).asInt();
            chances.put(Integer.parseInt(key), chance);
        });

        // Генерируем случайное число от 1 до totalChance
        int randomChance = random.nextInt(100) + 1;

        // Определяем число на основе распределения
        int cumulative = 0;
        for (Map.Entry<Integer, Integer> entry : chances.entrySet()) {
            cumulative += entry.getValue();
            if (randomChance <= cumulative) {
                return entry.getKey();
            }
        }

        // На случай, если ничего не сработало (теоретически не должен доходить сюда)
        throw new IllegalStateException("Ошибка при генерации случайного числа");
    }
}