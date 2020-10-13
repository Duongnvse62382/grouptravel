package com.fpt.gta.model.service;

public interface ChatMessageService {
    void chatInGroup(Integer idGroup, String firebaseUid, String text, String imageUrl);
}
