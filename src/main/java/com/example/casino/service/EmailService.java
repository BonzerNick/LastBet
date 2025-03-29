package com.example.casino.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /**
     * Отправка письма асинхронно. Если возникнет ошибка, она логируется.
     *
     * @param to      Адрес получателя.
     * @param subject Тема письма.
     * @param text    Текст сообщения.
     */
    @Async
    public void sendEmail(String to, String subject, String text) {
        if (!isValidEmail(to)) {
            System.err.println("Invalid email address: " + to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    /**
     * Проверка валидности email-адреса.
     *
     * @param email Адрес электронной почты.
     * @return true, если адрес валиден; false в противном случае.
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}