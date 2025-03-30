package com.example.casino.service;

import com.example.casino.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User getUserById(Integer id);
    Optional<User> findByEmail(String email);
    User createUser(User user);
    List<User> findAllUsers();
    boolean deleteUserById(Integer id);

    @Transactional
    User updateUser(Integer id, User updatedUser);

    void increaseBalance(User user, double amount);
    boolean decreaseBalance(User user, double amount);

    public double getUserBalanceByEmail(String email);
}
