package com.example.casino.service;

import com.example.casino.model.ActiveGame;
import com.example.casino.model.BetHistory;
import com.example.casino.model.Game;
import com.example.casino.model.User;
import com.example.casino.repository.ActiveGameRepository;
import com.example.casino.repository.GameRepository;
import com.example.casino.repository.BetHistoryRepository;
import com.example.casino.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ActiveGameService {

   private static final String ROCKET = "Ракетка";
   private static final String ROULETTE = "Рулетка";

   private final ActiveGameRepository activeGameRepository;
   private final BetHistoryRepository betHistoryRepository;
   private final GameHistoryService gameHistoryService;
   private final UserRepository userRepository;
   private final UserService userService;
   private final GameRepository gameRepository;
   private final ObjectMapper objectMapper = new ObjectMapper();
   private static final Logger log = LoggerFactory.getLogger(ActiveGameService.class);


   public ActiveGameService(
           ActiveGameRepository activeGameRepository,
           BetHistoryRepository betHistoryRepository,
           GameHistoryService gameHistoryService,
           GameRepository gameRepository,
           UserService userService,
           UserRepository userRepository
   ) {
       this.activeGameRepository = activeGameRepository;
       this.betHistoryRepository = betHistoryRepository;
       this.gameHistoryService = gameHistoryService;
       this.gameRepository = gameRepository;
       this.userService = userService;
       this.userRepository = userRepository;
   }

   // Шедуллер: запускается каждые 2 секунды
   @Scheduled(fixedRate = 2000)
   @SchedulerLock(name = "manageControlGames", lockAtMostFor = "2s")
   public void manageActiveGames() {
       // Удалить все игры с истекшим end_date
       cleanEndedGames();

       // Убедиться, что есть ровно одна Ракетка
       ensureRacketGame();

       // Убедиться, что есть ровно 5 Рулеток
       ensureRouletteGames();
   }

   private void cleanEndedGames() {
       // Удаляем игры, где end = true
       List<ActiveGame> endedGames = activeGameRepository.findByFinished(true);
       if (!endedGames.isEmpty()) {
           activeGameRepository.deleteAll(endedGames);
       }
   }

   private void ensureRacketGame() {
       List<ActiveGame> racketGames = activeGameRepository.findActiveGamesByGameType(ROCKET);

       if (racketGames.size() > 1) {
           // Если больше одной Ракетки, удалить лишние
           activeGameRepository.deleteAll(racketGames.subList(1, racketGames.size()));
       } else if (racketGames.isEmpty()) {
           // Если нет активной Ракетки, добавить её
           Optional<Game> racketGame = gameRepository.findByName(ROCKET);
           racketGame.ifPresent(this::createActiveGame);
       }
   }

   private void ensureRouletteGames() {
       List<ActiveGame> rouletteGames = activeGameRepository.findActiveGamesByGameType(ROULETTE);

       if (rouletteGames.size() > 5) {
           // Если больше 5 Рулеток, удалить лишние
           activeGameRepository.deleteAll(rouletteGames.subList(5, rouletteGames.size()));
       } else if (rouletteGames.size() < 5) {
           // Если меньше 5 Рулеток, добавить недостающие
           Optional<Game> rouletteGame = gameRepository.findByName(ROULETTE);
           for (int i = rouletteGames.size(); i < 5; i++) {
               rouletteGame.ifPresent(this::createActiveGame);
           }
       }
   }

   private void createActiveGame(Game game) {
       ActiveGame newGame = new ActiveGame();
       newGame.setGameId(game.getId());
       newGame.setStartDate(LocalDateTime.now());
       newGame.setEndDate(LocalDateTime.now().plusSeconds(game.getBetTimeSeconds()));
       newGame.setFinished(false);
       var parameters = objectMapper.createObjectNode();
       parameters.set("bets", objectMapper.createObjectNode());
       newGame.setParameters(parameters.toString());
       activeGameRepository.save(newGame);
   }

   // Метод для постановки ставки
   public void placeBet(Integer gameId, Double bet, Double coef, String email) {
       ActiveGame activeGame = activeGameRepository.findById(Long.valueOf(gameId))
               .orElseThrow(() -> new IllegalArgumentException("Активная игра не найдена"));

       Game game = gameRepository.findById(activeGame.getGameId())
               .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

       // Проверка min_bet и max_bet
       if (bet < game.getMinBet() || bet > game.getMaxBet()) {
           throw new IllegalArgumentException(String.format(
                   "Ставка должна быть в пределах от %.2f до %.2f",
                   game.getMinBet(), game.getMaxBet()
           ));
       }
       // Проверяем параметры из JSON
       try {
           // Получаем параметры игры как ObjectNode
           var parameters = (ObjectNode) objectMapper.readTree(activeGame.getParameters());

            // Проверяем время завершения ставок
           LocalDateTime betStop = activeGame.getEndDate();
           if (LocalDateTime.now().isAfter(betStop)) {
               throw new IllegalArgumentException("Прием ставок завершен");
           }

            // Проверка типа игры, коэффициент только для "Ракетка"
           if (!game.getName().equals(ROCKET) && coef != null) {
               throw new IllegalArgumentException("Коэффициент доступен только для Ракетки");
           }

           // Получаем объект пользователя
           User user = userService.findByEmail(email)
                   .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

           // Проверяем и получаем текущую ставку пользователя, если существует
           ObjectNode bets = (ObjectNode) parameters.get("bets");
           if (bets == null) {
               bets = objectMapper.createObjectNode();
               parameters.set("bets", bets);
           }

           double currentBet = 0.0;
           if (bets.has(email)) {
               // Текущая ставка из объекта JSON
               currentBet = bets.get(email).get(0).asDouble();
           }

           // Рассчитываем разницу между новой ставкой и текущей
           double betDifference = bet - currentBet;

           // Проверяем доступный баланс только если разница положительная (увеличение ставки)
           if (betDifference > 0 && user.getBalance() < betDifference) {
               throw new IllegalArgumentException("Недостаточно средств для увеличения ставки на " + betDifference);
           }

           // Изменяем баланс
           if (betDifference > 0) {
               userService.decreaseBalance(user, betDifference);
           } else if (betDifference < 0) {
               userService.increaseBalance(user, -betDifference);
           }

           // Обновляем или добавляем новую ставку в параметры игры
           bets.set(email, objectMapper.createArrayNode().add(bet).add(coef != null ? coef : 0));

            // Сохраняем обновленные параметры игры
           activeGame.setParameters(parameters.toString());
           activeGameRepository.save(activeGame);

       } catch (Exception e) {
           throw new IllegalArgumentException("Ошибка обработки: " + e.getMessage());
       }
   }

   // Метод для отмены ставки
   public void cancelBet(Integer gameId, String email) {
       ActiveGame activeGame = activeGameRepository.findById(Long.valueOf(gameId))
               .orElseThrow(() -> new IllegalArgumentException("Активная игра не найдена"));

       try {
           // Получаем параметры игры как ObjectNode
           var parameters = (ObjectNode) objectMapper.readTree(activeGame.getParameters());

           // Проверяем время завершения ставок
           if (LocalDateTime.now().isAfter(activeGame.getEndDate())) {
               throw new IllegalArgumentException("Ставки уже закрыты, отмена невозможна");
           }

           // Получаем список ставок
           ObjectNode bets = (ObjectNode) parameters.get("bets");
           if (bets == null || !bets.has(email)) {
               throw new IllegalArgumentException("Ставка не найдена для этого пользователя");
           }

           // Извлекаем сумму ставки
           // Первая часть массива — сумма ставки
           double currentBet = bets.get(email).get(0).asDouble();

           // Увеличиваем баланс пользователя
           User user = userService.findByEmail(email)
                   .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
           userService.increaseBalance(user, currentBet);

           // Удаляем ставку для текущего пользователя
           bets.remove(email);


           // Обновляем параметры игры и сохраняем
           activeGame.setParameters(parameters.toString());
           activeGameRepository.save(activeGame);

       } catch (Exception e) {
           throw new IllegalArgumentException("Ошибка обработки: " + e.getMessage());
       }
   }


   // Шедуллер, который запускается каждые 5 секунд и проверяет активные игры
   @Scheduled(fixedDelay = 5000)
   @SchedulerLock(name = "manageFinishGames")
   public void processActiveGames() {
       // Находим все незавершённые игры
       List<ActiveGame> activeGames = activeGameRepository.findByFinished(false);

       for (ActiveGame activeGame : activeGames) {
           // Если игра устарела (время истекло)
           if (activeGame.getEndDate().isBefore(LocalDateTime.now())) {
               // Обработка игры по типу (game_id указывает на тип игры)
               if (activeGame.getGameId() == 2) { // Рулетка
                   processRouletteGame(activeGame);
               } else if (activeGame.getGameId() == 3) { // Краш
                   processCrashGame(activeGame);
               }

               // Помечаем игру как завершённую
               activeGame.setFinished(true);
               activeGameRepository.save(activeGame);
           }
       }
   }

   // Обработка рулетки
   private void processRouletteGame(ActiveGame activeGame) {
       // Считываем ставки пользователей из параметров игры
       try {
           JsonNode parameters = objectMapper.readTree(activeGame.getParameters());
           JsonNode bets = parameters.get("bets");

           // Собираем все ставки и общую сумму
           Map<String, Double> userBets = new HashMap<>();
           final double[] totalBetSumHolder = {0};

           bets.fields().forEachRemaining(entry -> {
               String userEmail = entry.getKey(); // email пользователя (ключ)
               double userBet = entry.getValue().get(0).asDouble(); // Первая цифра в массиве — ставка

               userBets.put(userEmail, userBet);
               totalBetSumHolder[0] += userBet; // Суммируем общую сумму ставок
           });
           double totalBetSum = totalBetSumHolder[0];


           // Выбираем случайного победителя, пропорционально ставке
           Random random = new Random();
           double randomValue = random.nextDouble() * totalBetSum;

           double cumulativeSum = 0;
           String winnerEmail = null;

           for (Map.Entry<String, Double> entry : userBets.entrySet()) {
               cumulativeSum += entry.getValue();
               if (randomValue <= cumulativeSum) {
                   winnerEmail = entry.getKey();
                   break;
               }
           }

           // Расчёт выигрыша и запись в bets_history
           Integer winnerId = null;
           for (Map.Entry<String, Double> entry : userBets.entrySet()) {
               String userEmail = entry.getKey();
               double userBet = entry.getValue();

               // Поиск userId через email
               User user = userRepository.findByEmail(userEmail)
                       .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

               int userId = user.getId();

               // Вычисляем сумму выигрыша
               double winAmount = (winnerEmail != null && winnerEmail.equals(userEmail)) ? totalBetSum : 0;

               betHistoryRepository.save(new BetHistory(
                       null,
                       activeGame.getGameId(),
                       userId,
                       userBet,
                       winAmount,
                       LocalDateTime.now()
               ));

               // Увеличиваем баланс победителя
               if (winAmount > 0) {
                   User winner = userRepository.findById(userId).orElseThrow();
                   winnerId = userId;
                   winner.setBalance(winner.getBalance() + winAmount);
                   userRepository.save(winner);
               }
           }

           // Сохранение информации в games_history
           ObjectNode history_parameters = objectMapper.createObjectNode();

           // Добавляем вложенные данные
           history_parameters.put("winner_id", winnerId); // Пример: добавляем user_id
           history_parameters.put("bets", bets.toString());

           gameHistoryService.saveGameHistory(2, String.valueOf(totalBetSum), (int) totalBetSum, history_parameters.toString());


       } catch (Exception e) {
           log.error("Ошибка при выполнении задачи: {}", e.getMessage(), e);
       }
   }

   // Обработка игры типа "Краш"
   private void processCrashGame(ActiveGame activeGame) {
       try {
           JsonNode parameters = objectMapper.readTree(activeGame.getParameters());
           JsonNode bets = parameters.get("bets");

           // Генерация случайного коэффициента
           double crashPoint = generateCrashPoint();

           // Переменные для итоговых значений
           double[] totalBetSum = {0.0};
           double[] totalWinSum = {0.0};
           Map<Integer, Double> winners = new HashMap<>();

           // Обрабатываем каждую ставку
           bets.fields().forEachRemaining(entry -> {
               String userEmail = entry.getKey(); // Ключ - email пользователя
               JsonNode betDetails = entry.getValue(); // Значение - массив [ставка, коэффициент]

               double userBet = betDetails.get(0).asDouble(); // Ставка пользователя
               double userCoef = betDetails.get(1).asDouble(); // Коэффициент ставки

               // Поиск пользователя через email
               User user = userRepository.findByEmail(userEmail)
                       .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));
               int userId = user.getId();

               // Определение суммы выигрыша
               double winAmount = userCoef < crashPoint ? userBet * userCoef : 0;

               // Обновляем итоговые суммы
               totalBetSum[0] += userBet;
               totalWinSum[0] += winAmount;

               // Записываем результат в bets_history
               betHistoryRepository.save(new BetHistory(
                       null,
                       activeGame.getGameId(),
                       userId,
                       userBet,
                       winAmount,
                       LocalDateTime.now()
               ));

               // Увеличиваем баланс, если есть выигрыш
               if (winAmount > 0) {
                   winners.put(userId, winAmount);
                   user.setBalance(user.getBalance() + winAmount);
                   userRepository.save(user);
               }
           });


           // Сохранение информации в games_history
           ObjectNode history_parameters = objectMapper.createObjectNode();

           // Добавляем вложенные данные
           history_parameters.put("winners", winners.toString());
           history_parameters.put("crashPoint", crashPoint);
           history_parameters.put("bets", bets.toString());

           gameHistoryService.saveGameHistory(3, String.valueOf(totalWinSum[0]), (int) totalBetSum[0], history_parameters.toString());

       } catch (Exception e) {
           log.error("Ошибка при выполнении задачи: {}", e.getMessage(), e);
       }
   }

   // Метод генерации случайного коэффициента для краша
   private double generateCrashPoint() {
       Random random = new Random();
       double randomValue = Math.pow(random.nextDouble(), 2); // Генерация случайного значения
       double crashPoint = 1 / (1 - randomValue); // Масштабирование
       return Math.round(crashPoint * 100.0) / 100.0; // Округление до двух знаков
   }
}