package com.example.casino.repository;

import com.example.casino.model.ClientToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientTokenRepository extends JpaRepository<ClientToken, Long> {
}