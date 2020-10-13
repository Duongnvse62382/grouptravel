package com.fpt.gta.data.dto;

public class FriendlyMessage {
    private String id;
    private String text;
    private Long messageTime;
    private String firebaseUid;
    private String imageUrl;


    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, Long messageTime, String firebaseUid, String imageUrl) {
        this.text = text;
        this.messageTime = messageTime;
        this.firebaseUid = firebaseUid;
        this.imageUrl = imageUrl;
    }

    public FriendlyMessage(Long messageTime, String firebaseUid, String imageUrl) {
        this.messageTime = messageTime;
        this.firebaseUid = firebaseUid;
        this.imageUrl = imageUrl;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }


    public String getText() {
        return text;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
