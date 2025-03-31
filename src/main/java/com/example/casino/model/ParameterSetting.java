package com.example.casino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parameters_settings")
public class ParameterSetting {

    @Id
    @Column(name = "parameter_name", nullable = false)
    private String parameterName;

    @Column(name = "description")
    private String description;

    @Column(name = "value", nullable = false, columnDefinition = "jsonb")
    private String value;

}