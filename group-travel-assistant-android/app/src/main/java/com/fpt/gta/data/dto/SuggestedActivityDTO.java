package com.fpt.gta.data.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SuggestedActivityDTO implements Serializable {
    private String suggestedActivityName;
    private String suggestedActivityDistance;
    private String suggestedActivityTime;
    private String timeSuggestedActivityOpen;
    private String timeSuggestedActivityClose;
    private String suggestedActivityTimeZone;
    private String timeSuggestedActivityStart;
    private String timeSuggestedActivityEnd;

    public SuggestedActivityDTO() {
    }

    public SuggestedActivityDTO(String suggestedActivityName, String timeSuggestedActivityOpen, String timeSuggestedActivityClose, String suggestedActivityTimeZone) {
        this.suggestedActivityName = suggestedActivityName;
        this.timeSuggestedActivityOpen = timeSuggestedActivityOpen;
        this.timeSuggestedActivityClose = timeSuggestedActivityClose;
        this.suggestedActivityTimeZone = suggestedActivityTimeZone;
    }

    public SuggestedActivityDTO(String suggestedActivityName, String suggestedActivityDistance, String suggestedActivityTime, String timeSuggestedActivityOpen, String timeSuggestedActivityClose, String suggestedActivityTimeZone, String timeSuggestedActivityStart, String timeSuggestedActivityEnd) {
        this.suggestedActivityName = suggestedActivityName;
        this.suggestedActivityDistance = suggestedActivityDistance;
        this.suggestedActivityTime = suggestedActivityTime;
        this.timeSuggestedActivityOpen = timeSuggestedActivityOpen;
        this.timeSuggestedActivityClose = timeSuggestedActivityClose;
        this.suggestedActivityTimeZone = suggestedActivityTimeZone;
        this.timeSuggestedActivityStart = timeSuggestedActivityStart;
        this.timeSuggestedActivityEnd = timeSuggestedActivityEnd;
    }
}
