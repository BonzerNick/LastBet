package com.example.casino.controller;

import com.example.casino.model.UnconfirmedUser;
import com.example.casino.model.User;
import com.example.casino.repository.UnconfirmedUserRepository;
import com.example.casino.service.EmailService;
import com.example.casino.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    // 1. Отправка письма со ссылкой на регистрацию
    @PostMapping("/email_registration")
    public String sendRegistrationEmail(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String language,
            @RequestParam(required = false) String phoneNumber
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


    // 2. Ссылка в письме для подтверждения email
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

            // Используем UserService для создания пользователя
            userService.createUser(newUser);

            // Удаляем запись из таблицы 'unconfirmed_users'
            unconfirmedUserRepository.delete(unconfirmedUser);

            return "Email confirmed successfully!";
        } else {
            // Если пользователь не найден
            return "Invalid or expired confirmation token!";
        }

    }

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

        // Возвращаем успешный статус
        return ResponseEntity.ok().build();
    }



    // 5. Включение 2FA
    @PostMapping("/2fa_enable")
    public String enable2FA(@RequestParam String username) {
        // Логика включения двухфакторной аутентификации
        return "2FA enabled for user " + username;
    }

    // 6. Вход по 2FA
    @PostMapping("/2fa_login")
    public String loginWith2FA(@RequestParam String username, @RequestParam String code) {
        // Логика входа с 2FA
        return "2FA login successful for user " + username;
    }

    // 7. Выключение 2FA
    @PostMapping("/2fa_disenable")
    public String disable2FA(@RequestParam String username) {
        // Логика выключения двухфакторной аутентификации
        return "2FA disabled for user " + username;
    }
}