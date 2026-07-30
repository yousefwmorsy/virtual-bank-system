package com.example.transactionservice.mappers;

import com.example.transactionservice.dtos.InitiateTransactionRequest;
import com.example.transactionservice.dtos.TransactionHistoryResponseDTO;
import com.example.transactionservice.dtos.initiateTransactionResponse;
import com.example.transactionservice.entities.Transaction;
import com.example.transactionservice.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiatedAt", ignore = true)
    Transaction toEntity(InitiateTransactionRequest request);

    @Mapping(source = "transaction.id", target = "transactionId")
    @Mapping(source = "status", target = "status")
    initiateTransactionResponse toResponse(Transaction transaction, Status status);

    @Mapping(source = "id", target = "transactionId")
    @Mapping(source = "initiatedAt", target = "timestamp")
    @Mapping(source = "status", target = "deliveryStatus", qualifiedByName = "statusToDeliveryStatus")
    TransactionHistoryResponseDTO toHistoryResponse(Transaction transaction);

    @org.mapstruct.Named("statusToDeliveryStatus")
    default String statusToDeliveryStatus(Status status) {
        return switch (status) {
            case INITIATED -> "SENT";
            case COMPLETED -> "DELIVERED";
            case FAILED -> "FAILED";
            default -> "PENDING";
        };
    }
}
