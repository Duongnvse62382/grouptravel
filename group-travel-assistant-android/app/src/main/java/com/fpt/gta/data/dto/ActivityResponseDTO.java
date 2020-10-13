package com.fpt.gta.data.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ActivityResponseDTO implements Serializable {
    private String activityName;
    private String activityDistance;
    private String activityTime;
    private String timeActivityOpen;
    private String timeActivityClose;
    private String activityTimeZone;
    private String timeActivityStart;
    private String timeSActivityEnd;

    public ActivityResponseDTO(String activityName, String timeActivityStart, String timeSActivityEnd) {
        this.activityName = activityName;
        this.timeActivityStart = timeActivityStart;
        this.timeSActivityEnd = timeSActivityEnd;
    }

    public ActivityResponseDTO(String activityName, String timeActivityOpen, String timeActivityClose, String activityTimeZone) {
        this.activityName = activityName;
        this.timeActivityOpen = timeActivityOpen;
        this.timeActivityClose = timeActivityClose;
        this.activityTimeZone = activityTimeZone;
    }

    public ActivityResponseDTO(String activityName, String activityDistance, String activityTime, String timeActivityOpen, String timeActivityClose, String activityTimeZone, String timeActivityStart, String timeSActivityEnd) {
        this.activityName = activityName;
        this.activityDistance = activityDistance;
        this.activityTime = activityTime;
        this.timeActivityOpen = timeActivityOpen;
        this.timeActivityClose = timeActivityClose;
        this.activityTimeZone = activityTimeZone;
        this.timeActivityStart = timeActivityStart;
        this.timeSActivityEnd = timeSActivityEnd;
    }

    public ActivityResponseDTO() {
    }
}
