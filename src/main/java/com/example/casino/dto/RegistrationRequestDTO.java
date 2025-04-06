package com.example.casino.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequestDTO {
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "Имя пользователя должно содержать от 3 до 20 символов, включая буквы, цифры и нижние подчеркивания")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Пароль должен содержать минимум 8 символов, включая хотя бы одну заглавную букву, одну строчную букву и одну цифру")
    private String password;

    @NotBlank(message = "Язык не может быть пустым")
    private String language;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Номер телефона должен содержать от 10 до 15 цифр, включая необязательный знак плюс в начале")
    private String phoneNumber;
}
