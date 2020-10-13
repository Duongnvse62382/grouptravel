package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Document;
import com.fpt.gta.model.entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction getTransactionById(Integer idTransaction);

    Transaction createTransaction(String firebaseUid, Integer idGroup, Transaction transaction);

    Transaction createTransaction(String firebaseUid, Integer idGroup, Transaction transaction, List<Document> newDocumentList);

    Transaction updateTransaction(String firebaseUid, Transaction transaction);

    Transaction updateTransaction(String firebaseUid, Transaction transaction, List<Document> newDocumentList);

    List<Transaction> findAllTransactionInGroup(String firebaseUid, Integer idGroup);

    Transaction findTransactionById(String firebaseUid, Integer idTransaction);

    void deleteTransaction(String firebaseUid, Integer idTransaction);
}
