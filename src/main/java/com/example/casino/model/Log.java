package com.example.casino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "parameters", columnDefinition = "jsonb")
    private String parameters;

}