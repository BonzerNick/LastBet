package com.example.casino.controller;

import com.example.casino.model.User;
import com.example.casino.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Эндпоинт для добавления пользователя
    @PostMapping
    @Operation(
            summary = "Add a new user",
            description = "Creates and saves a new user with the provided details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully created."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input data.")
    })
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // Эндпоинт для поиска пользователя по имени
    @GetMapping("/{username}")
    @Operation(
            summary = "Get user by username",
            description = "Fetches a user based on the given username parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "User not found for the given username.")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "The unique username of the user to fetch.") @PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Эндпоинт для получения списка всех пользователей
    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users available in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all users returned successfully."),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving users.")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // Эндпоинт для обновления пользователя
    @PutMapping("/{id}")
    @Operation(
            summary = "Update user by ID",
            description = "Updates the details of an existing user based on the provided ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully updated."),
            @ApiResponse(responseCode = "404", description = "User not found for the given ID."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input data.")
    })
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

    // Эндпоинт для удаления пользователя по ID
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user by ID",
            description = "Deletes an existing user based on the provided ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully deleted."),
            @ApiResponse(responseCode = "404", description = "User not found for the given ID.")
    })
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