package com.fpt.gta.model.service.impl;

import com.fpt.gta.model.constant.ChatMessage;
import com.fpt.gta.model.constant.FirebaseDatabaseConstant;
import com.fpt.gta.model.service.ChatMessageService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Override
    public void chatInGroup(Integer idGroup, String firebaseUid, String text, String imageUrl) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFirebaseUid(firebaseUid);
        chatMessage.setImageUrl(imageUrl);
        Long nowUtc = Instant.now().toEpochMilli();
        chatMessage.setMessageTime(nowUtc);
        chatMessage.setText(text);
        DatabaseReference reloadTransactionUtcRef = database.getReference(FirebaseDatabaseConstant.getChatMessagePath(idGroup, UUID.randomUUID().toString()));
        reloadTransactionUtcRef.setValueAsync(chatMessage);
    }
}
