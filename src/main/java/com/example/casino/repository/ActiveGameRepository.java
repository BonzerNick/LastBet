package com.example.casino.repository;

import com.example.casino.model.ActiveGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveGameRepository extends JpaRepository<ActiveGame, Long> {
}