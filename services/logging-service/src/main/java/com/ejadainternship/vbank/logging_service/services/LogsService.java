package com.ejadainternship.vbank.logging_service.services;

import com.ejadainternship.vbank.logging_service.dtos.LogItemDTO;
import com.ejadainternship.vbank.logging_service.mappers.JsonLogItemMapper;
import com.ejadainternship.vbank.logging_service.models.JsonLogItem;
import com.ejadainternship.vbank.logging_service.repositories.LogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LogsService {
    private final LogsRepository logsRepository;

    @KafkaListener(topics = "microservices-logs", groupId = "microservices-logs")
    public void consume(LogItemDTO logItemDTO) {
        System.out.println(logItemDTO.toString());
        JsonLogItem logItem = JsonLogItemMapper.toJsonLogItem(logItemDTO);
        logsRepository.save(logItem);
    }
}
