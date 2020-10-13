package com.fpt.gta.model.constant;

import lombok.Data;

@Data
public class ChatMessage {
    private String firebaseUid;
    private Long messageTime;
    private String text;
    private String ImageUrl;

}
