package com.ejadainternship.vbank.logging_service.repositories;

import com.ejadainternship.vbank.logging_service.models.JsonLogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends JpaRepository<JsonLogItem, Long> {
}
