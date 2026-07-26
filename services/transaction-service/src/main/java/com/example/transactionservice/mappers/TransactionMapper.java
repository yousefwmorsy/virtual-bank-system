package com.example.transactionservice.mappers;

import com.example.transactionservice.dtos.InitiateTransactionRequest;
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
}
