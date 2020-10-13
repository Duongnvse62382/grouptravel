package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Document;
import com.fpt.gta.model.entity.Transaction;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface DocumentService {
    Document addNewDocumentGroup(String firebaseUid, Integer idGroup, String contentType, String uri);

    void addNewDocumentActivity(String firebaseUid, Integer idActivity, String contentType, String uri);

    void addGroupDocumentToTransaction(String firebaseUid, Integer idTransaction, Integer idDocument);

    void addGroupDocumentToTransaction(String firebaseUid, Transaction transaction, Integer idDocument);

    void addNewDocumentTransaction(String firebaseUid, Integer idTransaction, String contentType, String uri);

    void addNewDocumentTransaction(String firebaseUid, Transaction transaction, String contentType, String uri);

    void deleteDocumentTransaction(String firebaseUid, Integer idDocument);

    void addGroupDocumentToActivity(String firebaseUid, Integer idActivity, Integer idDocument);

    void deleteDocumentActivity(String firebaseUid, Integer idDocument);

    Document addNewDocumentGroup(Integer idGroup, String contentType, byte[] data);

    void deleteGroupDocument(String firebaseUid, Integer idDocument);

    List<Document> findAllDocumentGroup(String firebaseUid, Integer idGroup);

    List<Document> findAllDocumentTransaction(String firebaseUid, Integer idTransaction);

    List<Document> findAllDocumentActivity(String firebaseUid, Integer idActivity);

    void prepareDocumentDownloadLink(List<Document> documentList);

}
