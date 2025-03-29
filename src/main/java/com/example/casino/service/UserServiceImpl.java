package com.example.casino.service;

import com.example.casino.model.User;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import com.example.casino.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        return Optional.ofNullable(userRepository.findByEmail(email).orElse(null));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public boolean deleteUserById(Integer id) {
        userRepository.deleteById(id);
        return true;
    }


    @Transactional
    @Override
    public User updateUser(Integer id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPassword(updatedUser.getPassword());
                    existingUser.setBalance(updatedUser.getBalance());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    existingUser.setTwoFactorAuthEnabled(updatedUser.isTwoFactorAuthEnabled());
                    existingUser.setDate(updatedUser.getDate());
                    existingUser.setLastVisit(updatedUser.getLastVisit());
                    existingUser.setLanguage(updatedUser.getLanguage());
                    return userRepository.save(existingUser);
                })
                .orElse(null);
    }
}