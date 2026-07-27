package com.example.transactionservice.services;

import com.example.transactionservice.dtos.*;
import com.example.transactionservice.entities.Transaction;
import com.example.transactionservice.enums.Status;
import com.example.transactionservice.exception.*;
import com.example.transactionservice.mappers.TransactionMapper;
import com.example.transactionservice.repositories.TransactionRepository;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


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
        String url = BASE_URL + "/accounts/transfer";

        Transaction transaction = transactionRepository.findById(executionRequest.getTransactionId())
                .orElseThrow(() -> new TransactionDoesNotExistException(executionRequest.getTransactionId().toString()));

        if(transaction.getStatus()==Status.COMPLETED){
            throw new TransactionAlreadyCompletedException(
                    "Transaction with id " + transaction.getId() + " has already been completed");
        }
             TransferRequestDTO transferRequestDTO = new TransferRequestDTO(transaction.getFromAccountId().toString() , transaction.getToAccountId().toString() ,
                     transaction.getAmount());

              HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequestDTO);
              try {
                  ResponseEntity<MessageDTO> messageDTOResponseEntity = restTemplate.exchange(
                          url,
                          HttpMethod.PUT,
                          requestEntity,
                          MessageDTO.class

                  );

                  transaction.setStatus(Status.COMPLETED);
                  transactionRepository.save(transaction);
                  return new ExecutionResponse(transaction.getId() , transaction.getStatus() , transaction.getInitiatedAt());

              } catch (HttpClientErrorException e){
                  if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {

                      transaction.setStatus(Status.FAILED);
                      transactionRepository.save(transaction);


                      throw new InsufficientBalanceException("Transaction execution failed: Insufficient balance in source account.");
                  }

                  System.err.println("API call failed with status: " + e.getStatusCode());
                  throw e;
              }

    }

    public AccountDetailsDTO fetchAccountDetails(String accountId) {
        String url = BASE_URL + "/accounts/" + accountId;

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

    public List<TransactionHistoryResponseDTO> getTransactionHistory(UUID accountId) {
        List<Transaction> transactions = transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByInitiatedAtDesc(accountId, accountId);

        if (transactions.isEmpty()) {
            throw new NoTransactionsFoundException(
                    "No transactions found for account ID " + accountId);
        }

        return transactions.stream()
                .map(t -> {
                    TransactionHistoryResponseDTO dto = transactionMapper.toHistoryResponse(t);
                    BigDecimal signedAmount = t.getFromAccountId().equals(accountId)
                            ? t.getAmount().negate()
                            : t.getAmount();
                    return new TransactionHistoryResponseDTO(
                            dto.transactionId(), dto.fromAccountId(), dto.toAccountId(),
                            signedAmount, dto.description(), dto.timestamp(), dto.deliveryStatus());
                })
                .toList();
    }


}
