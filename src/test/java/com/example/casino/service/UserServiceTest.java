package com.example.casino.service;

import com.example.casino.model.User;
import com.example.casino.repository.UserRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testAddUser() {
        // Создаем пользователя
        User user = new User();
        user.setUsername("test_user2");
        user.setPassword("password123");
        user.setEmail("test_user2@example.com");
        user.setBalance(0.0);
        user.setLanguage("en");
        user.setDate(LocalDateTime.now());

        // Сохраняем пользователя в БД
        User savedUser = userRepository.save(user);

        // Проверяем, что пользователь успешно добавлен
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("test_user2");
    }

    @Test
    public void testFindUserByUsername() {

        // Проверяем поиск по имени
        Optional<User> foundUser = userRepository.findByUsername("test_user");

        if (foundUser.isPresent()) {
            User current_user = foundUser.get();
            assertThat(current_user.getUsername()).isEqualTo("test_user");
        } else {
            System.out.println("User with username 'find_user' was not found");
        }

    }


}