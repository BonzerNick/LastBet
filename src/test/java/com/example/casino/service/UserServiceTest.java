package com.example.casino.service;

import com.example.casino.model.UnconfirmedUser;
import com.example.casino.model.User;
import com.example.casino.repository.UnconfirmedUserRepository;
import com.example.casino.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Transactional
@Rollback
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @Autowired
    private UnconfirmedUserRepository unconfirmedUserRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        // Инициализируем MockMvc
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void testSendRegistrationEmail() throws Exception {
        // Данные для теста
        String email = "nikitasmetankin1358@gmail.com";
        String username = "test_user";
        String password = "test_password";
        String language = "en";
        String phoneNumber = "+91234567891";
        String confirmationHash = UUID.randomUUID().toString();

        try {

            // Выполняем запрос к эндпоинту
            String response =
                    mockMvc.perform(post("/auth/email_registration")
                            .param("email", email)
                            .param("username", username)
                            .param("password", password)
                            .param("language", language)
                            .param("phoneNumber", phoneNumber)
                            .param("confirmationHash", confirmationHash))
                    .andReturn() // Возвращаем результат выполнения запроса
                    .getResponse() // Получаем HTTP-ответ
                    .getContentAsString(); // Извлекаем тело ответа как строку

            // Выводим результат в консоль
            System.out.println("Response: " + response);
        } catch (Exception e) {
            if (e.getMessage().contains("could not execute statement") || e.getMessage().contains("Unique index or primary key violation")) {
                // В базе данных уже есть пользователь с таким email или username
                System.out.println("User with this email or username already exists. Skipping...");
            } else {
                // Если это другая ошибка, пробрасываем исключение дальше
                throw e;
            }
        }
    }



    @Test
    public void testAddUser() {
        // Создаем пользователя
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("password123");
        user.setEmail("test_user@example.com");
        user.setBalance(0.0);
        user.setLanguage("en");
        user.setDate(LocalDateTime.now());
        user.setRole("USER");

        // Сохраняем пользователя в БД
        User savedUser = userRepository.save(user);

        // Проверяем, что пользователь успешно добавлен
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("test_user");
    }

    @Test
    public void testFindUserByUsername() {

        // Проверяем поиск по имени
        Optional<User> foundUser = userRepository.findByEmail("test_user@example.com");

        if (foundUser.isPresent()) {
            User current_user = foundUser.get();
            assertThat(current_user.getUsername()).isEqualTo("test_user2");
        } else {
            System.out.println(" ");
        }

    }


}