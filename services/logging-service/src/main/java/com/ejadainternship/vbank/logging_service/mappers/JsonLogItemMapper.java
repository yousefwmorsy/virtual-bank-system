package com.ejadainternship.vbank.logging_service.mappers;

import com.ejadainternship.vbank.logging_service.dtos.LogItemDTO;
import com.ejadainternship.vbank.logging_service.models.JsonLogItem;
import com.ejadainternship.vbank.logging_service.models.MessageType;

import java.time.LocalDateTime;

public class JsonLogItemMapper {
    public static JsonLogItem toJsonLogItem(LogItemDTO logItemDTO){
        return JsonLogItem
                .builder()
                .message(logItemDTO.message())
                .messageType(MessageType.valueOf(logItemDTO.messageType()))
                .dateTime(LocalDateTime.parse(logItemDTO.dateTime()))
                .build();
    }
}
