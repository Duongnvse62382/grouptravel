package com.fpt.gta.model.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

public interface MessagingService {
    @Async
    void messageAllInGroupAsync(Integer idGroup, Map<String, String> data);
}
