package com.example.casino.service;

import com.example.casino.model.User;
import java.util.List;
import java.util.Optional;

import com.example.casino.repository.UserRepository;
import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public boolean deleteUserById(Integer id) {
        userRepository.deleteById(id);
        return true;
    }

    // Метод для получения баланса пользователя по email
    @Override
    public double getUserBalanceByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с email " + email + " не найден"));

        return user.getBalance();
    }


    @Transactional
    @Override
    public User updateUser(Integer id, User updatedUser) {

        // Шифрование пароля
        String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());

        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPassword(hashedPassword);
                    existingUser.setBalance(updatedUser.getBalance());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    existingUser.setTwoFactorAuthEnabled(updatedUser.isTwoFactorAuthEnabled());
                    existingUser.setDate(updatedUser.getDate());
                    existingUser.setLastVisit(updatedUser.getLastVisit());
                    existingUser.setLanguage(updatedUser.getLanguage());
                    existingUser.setSecretKey(TimeBasedOneTimePasswordUtil.generateBase32Secret());
                    return userRepository.save(existingUser);
                })
                .orElse(null);
    }

    @Transactional
    @Override
    public void increaseBalance(User user, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для увеличения баланса должна быть положительной.");
        }
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public boolean decreaseBalance(User user, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для уменьшения баланса должна быть положительной.");
        }
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public User enable2Fa(User user) {
        user.setTwoFactorAuthEnabled(true);
        return userRepository.save(user);
    }
}