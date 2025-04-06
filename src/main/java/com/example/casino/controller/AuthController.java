package com.example.casino.controller;

import com.example.casino.model.UnconfirmedUser;
import com.example.casino.model.User;
import com.example.casino.repository.UnconfirmedUserRepository;
import com.example.casino.service.EmailService;
import com.example.casino.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UnconfirmedUserRepository unconfirmedUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${app.url}")
    private String appUrl; // URL приложения

    @Operation(
            summary = "Регистрация через email",
            description = "Метод отправляет письмо со ссылкой для завершения регистрации. Требуется указать email, имя пользователя, пароль и язык."
    )
    @PostMapping("/email_registration")
    public String sendRegistrationEmail(
            @RequestParam @Email(message = "Email should be valid") String email,
            @RequestParam @NotBlank(message = "Username is mandatory") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username,
            @RequestParam @NotBlank(message = "Password is mandatory") @Size(min = 6, message = "Password must be at least 6 characters") String password,
            @RequestParam @NotBlank(message = "Language is mandatory") String language,
            @RequestParam(required = false) @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid") String phoneNumber
    ) {
        // Генерация уникального хэша
        String confirmationHash = UUID.randomUUID().toString();

        // Шифрование пароля
        String hashedPassword = passwordEncoder.encode(password);

        // Создание нового пользователя
        UnconfirmedUser user = new UnconfirmedUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setLanguage(language);
        user.setPhoneNumber(phoneNumber);
        user.setConfirmationHash(confirmationHash);

        // Сохранение пользователя в базу данных
        unconfirmedUserRepository.save(user);

        // Формирование ссылки для подтверждения
        String confirmationLink = appUrl + "/auth/email_confirm?token=" + confirmationHash;

        // Отправка письма
        emailService.sendEmail(email, "Confirm your registration",
                "Click the following link to confirm your registration: " + confirmationLink);

        return "Registration email sent to " + email;
    }


    @Operation(
            summary = "Подтверждение email",
            description = "Запрос используется для подтверждения email-адреса с использованием токена"
    )
    @GetMapping("/email_confirm")
    public String confirmEmail(@RequestParam String token) {
        // Ищем пользователя по токену в таблице unconfirmed_users
        Optional<UnconfirmedUser> unconfirmedUserOpt = unconfirmedUserRepository.findByConfirmationHash(token);

        if (unconfirmedUserOpt.isPresent()) {
            // Если пользователь найден
            UnconfirmedUser unconfirmedUser = unconfirmedUserOpt.get();

            // Создаем нового пользователя для таблицы 'users'
            User newUser = new User();
            newUser.setUsername(unconfirmedUser.getUsername());
            newUser.setPassword(unconfirmedUser.getPassword());
            newUser.setEmail(unconfirmedUser.getEmail());
            newUser.setLanguage(unconfirmedUser.getLanguage());
            newUser.setPhoneNumber(unconfirmedUser.getPhoneNumber());
            newUser.setDate(unconfirmedUser.getDate());
            newUser.setRole("USER");

            // Используем UserService для создания пользователя
            userService.createUser(newUser);

            // Удаляем запись из таблицы 'unconfirmed_users'
            unconfirmedUserRepository.delete(unconfirmedUser);

            return "Email confirmed successfully!";
        } else {
            return "Invalid or expired confirmation token!";
        }

    }

    @Operation(
            summary = "Выход из системы",
            description = "Метод завершает сессию текущего пользователя"
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // Завершение текущей сессии
        request.getSession(false).invalidate();

        // Удаление cookie JSESSIONID
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Срок жизни cookie - немедленно истекает
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}