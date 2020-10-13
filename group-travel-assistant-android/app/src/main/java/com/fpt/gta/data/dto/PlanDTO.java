package com.fpt.gta.data.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PlanDTO implements Serializable {
    private Integer id;
    private MemberDTO owner;
    private Integer idStatus;
    private List<ActivityDTO> activityList = new ArrayList<>();
    private List<VotedPlanDTO> votedPlanList = new ArrayList<>();
    private int imagePlan;
    private List<DayPlanResponseDTO> dayPlanResponseDTOS = new ArrayList<>();
    private String planName;
    private BigDecimal activityBudget = BigDecimal.ZERO;
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    private BigDecimal foodBudget = BigDecimal.ZERO;

    @Data
    public static class VotedPlanDTO implements Serializable {
        private Integer id;
        private MemberDTO member;
    }

    @Data
    public static class MemberDTO implements Serializable {
        private Integer id;
        private BigDecimal expectedPrice;
        private Integer idRole;
        private Integer idStatus;
        private PersonDTO person;
    }

    @Data
    public static class PersonDTO implements Serializable {
        private Integer id;
        private String name;
        private String email;
        private String phoneNumber;
        private String photoUri;
        private String firebaseUid;
    }
}
