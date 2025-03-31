package com.example.casino.controller;

import com.example.casino.model.User;
import com.example.casino.service.BalanceService;
import com.example.casino.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;
    private final UserService userService;

    public BalanceController(BalanceService balanceService, UserService userService) {
        this.balanceService = balanceService;
        this.userService = userService;
    }

    @Operation(
            summary = "Пополнение баланса",
            description = "Позволяет авторизованному пользователю пополнить баланс. Доступен выбор метода пополнения (например, карта, электронный кошелек)."
    )
    @PostMapping("/deposit")
    public String deposit(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam double amount,
                          @RequestParam String paymentMethod) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found or invalid credentials"));

        // Проводим операцию пополнения
        balanceService.updateBalance(user.getId(), amount, paymentMethod);
        return "Deposit successful";
    }

    @Operation(
            summary = "Вывод средств",
            description = "Позволяет авторизованному пользователю вывести средства с указанием метода вывода (например, карта, электронный кошелек)."
    )
    @PostMapping("/withdraw")
    public String withdraw(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam double amount,
                           @RequestParam String paymentMethod) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found or invalid credentials"));

        // Проводим операцию снятия. Для вывода сумма будет отрицательной.
        balanceService.updateBalance(user.getId(), -amount, paymentMethod);
        return "Withdrawal successful";
    }
}