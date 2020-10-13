package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("select t from Transaction t where t.owner.joinedGroup.id=?1")
    List<Transaction> findAllTransactionByIdGroup(Integer idGroup);
}
