package com.example.casino.repository;

import com.example.casino.model.ParameterSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterSettingRepository extends JpaRepository<ParameterSetting, String> {
}