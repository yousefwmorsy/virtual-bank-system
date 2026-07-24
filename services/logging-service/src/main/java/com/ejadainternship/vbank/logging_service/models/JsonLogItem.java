package com.ejadainternship.vbank.logging_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class JsonLogItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "text")
    String message;

    @Enumerated(EnumType.STRING)
    MessageType messageType;

    LocalDateTime dateTime;
}
