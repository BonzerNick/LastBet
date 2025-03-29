package com.example.casino.service;

import com.example.casino.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    User getUserById(Integer id);
    User findByUsername(String username);
    User createUser(User user);
    List<User> findAllUsers();
    boolean deleteUserById(Integer id);

    @Transactional
    User updateUser(Integer id, User updatedUser);
}
