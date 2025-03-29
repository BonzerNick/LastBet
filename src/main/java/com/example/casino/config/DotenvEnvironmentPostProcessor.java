package com.example.casino.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .load();

        // Загружаем переменные из .env
        Map<String, Object> dotenvProperties = new HashMap<>();
        dotenvProperties.put("DB_URL", dotenv.get("DB_URL"));
        dotenvProperties.put("DB_USERNAME", dotenv.get("DB_USERNAME"));
        dotenvProperties.put("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        // Добавляем их в Spring Environment
        environment.getPropertySources().addFirst(
                new MapPropertySource("dotenvProperties", dotenvProperties)
        );

        // Выводим для проверки
        System.out.println("Loaded environment variables during EnvironmentPostProcessor:");
        System.out.println("DB_URL: " + dotenv.get("DB_URL"));
        System.out.println("DB_USERNAME: " + dotenv.get("DB_USERNAME"));
        System.out.println("DB_PASSWORD: " + dotenv.get("DB_PASSWORD"));
    }
}