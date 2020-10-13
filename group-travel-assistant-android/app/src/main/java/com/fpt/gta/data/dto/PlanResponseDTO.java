package com.fpt.gta.data.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PlanResponseDTO implements Serializable {
    private int imagePlan;
    private List<DayPlanResponseDTO> dayPlanResponseDTOS;
    private String planName;

    public PlanResponseDTO(int imagePlan, List<DayPlanResponseDTO> dayPlanResponseDTOS, String planName) {
        this.imagePlan = imagePlan;
        this.dayPlanResponseDTOS = dayPlanResponseDTOS;
        this.planName = planName;
    }

    public PlanResponseDTO() {
    }
}



