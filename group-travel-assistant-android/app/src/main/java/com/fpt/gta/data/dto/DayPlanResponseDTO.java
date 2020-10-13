package com.fpt.gta.data.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class DayPlanResponseDTO implements Serializable {
    private int dateId;
    private String dayName;
    private Date dayStart;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();


    public DayPlanResponseDTO(int dateId, String dayName, Date dayStart, List<ActivityDTO> activityDTOList) {
        this.dateId = dateId;
        this.dayName = dayName;
        this.dayStart = dayStart;
        this.activityDTOList = activityDTOList;
    }

    public DayPlanResponseDTO() {
    }
}
