package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.DocumentWithTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentWithTransactionRepository extends JpaRepository<DocumentWithTransaction, Integer> {
}
