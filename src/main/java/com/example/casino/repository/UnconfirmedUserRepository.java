package com.example.casino.repository;

import com.example.casino.model.UnconfirmedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnconfirmedUserRepository extends JpaRepository<UnconfirmedUser, Long> {
    Optional<UnconfirmedUser> findByConfirmationHash(String confirmationHash);
}