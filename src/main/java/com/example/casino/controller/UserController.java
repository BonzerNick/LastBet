package com.example.casino.controller;

import com.example.casino.model.User;
import com.example.casino.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Получить баланс авторизованного пользователя",
            description = "Возвращает текущий баланс пользователя, который авторизован в системе."
    )
    @GetMapping("/balance")
    public ResponseEntity<Double> getUserBalance(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        double balance = userService.getUserBalanceByEmail(email);
        return ResponseEntity.ok(balance);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Добавить нового пользователя",
            description = "Создать и сохранить нового пользователя на основе предоставленных данных. Доступно только администратору."
    )
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Получить пользователя по email",
            description = "Возвращает информацию о пользователе, соответствующем указанному email. Доступно только администратору."
    )
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "The unique email of the user to fetch.") @PathVariable String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found or invalid credentials"));
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает список всех пользователей, зарегистрированных в системе. Доступно только администратору."
    )
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Обновить пользователя по ID",
            description = "Обновляет информацию о существующем пользователе по указанному ID. Доступно только администратору."
    )
    public ResponseEntity<User> updateUser(
            @Parameter(description = "The ID of the user to update.", required = true) @PathVariable Integer id,
            @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Удалить пользователя по ID",
            description = "Удаляет пользователя из системы по указанному ID. Доступно только администратору."
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "The ID of the user to delete.", required = true) @PathVariable Integer id) {
        boolean isDeleted = userService.deleteUserById(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}