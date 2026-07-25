package com.example.transactionservice.services;

import com.example.transactionservice.dtos.*;
import com.example.transactionservice.entities.Transaction;
import com.example.transactionservice.enums.Status;
import com.example.transactionservice.exception.AccountDoesNotExistException;
import com.example.transactionservice.exception.InsufficientBalanceException;
import com.example.transactionservice.mappers.TransactionMapper;
import com.example.transactionservice.repositories.TransactionRepository;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


@Service
@Data
public class TransactionService {

    final private TransactionRepository transactionRepository;
    final private TransactionMapper transactionMapper;

    final private RestTemplate restTemplate;
    private final String BASE_URL = "http://account-service";

    public initiateTransactionResponse initiateTransaction(InitiateTransactionRequest initiateTransactionRequest){

        AccountDetailsDTO fromAccountDetailsDTO = fetchAccountDetails(initiateTransactionRequest.getFromAccountId().toString());

        AccountDetailsDTO toAccountDetailsDTO = fetchAccountDetails(initiateTransactionRequest.getToAccountId().toString());

        BigDecimal fromBalance = fromAccountDetailsDTO.balance();

        if(fromBalance.compareTo(initiateTransactionRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException("the balance IS insufficient");
        }

        Transaction transaction = transactionMapper.toEntity(initiateTransactionRequest);

        transactionRepository.save(transaction);

        return  transactionMapper.toResponse(transaction, Status.INITIATED);
    }

    public ExecutionResponse executeTransaction(ExecutionRequest executionRequest){


    }

    public AccountDetailsDTO fetchAccountDetails(String accountId) {
        String url = BASE_URL + "/" + accountId;

        try {
            ResponseEntity<AccountDetailsDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    AccountDetailsDTO.class
            );

            // successful response
            return response.getBody();

        } catch (HttpClientErrorException e) {
            // 4. Catch the exception triggered by the other service's @ExceptionHandler

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new AccountDoesNotExistException("this account is not found");
            } else {
                System.err.println("API call failed with status: " + e.getStatusCode());
            }

            throw e;
        }
    }


}
