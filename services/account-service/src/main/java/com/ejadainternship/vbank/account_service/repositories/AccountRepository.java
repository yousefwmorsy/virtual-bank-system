package com.ejadainternship.vbank.account_service.repositories;

import com.ejadainternship.vbank.account_service.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    public List<Account> findAllByUserId(String userId);
}
