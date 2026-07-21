package com.example.userservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false,unique = true)
    private String name;


    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String email;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", updatable = false)
    private LocalDateTime updatedAt;
}
