package com.fpt.gta.rest.manageplan;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PlanDTO {
    private Integer id;
    private List<ActivityDTO> activityList;
    private List<VotedPlanDTO> votedPlanList;
    private MemberDTO owner;
    private Integer idStatus;
    private BigDecimal activityBudget;
    private BigDecimal accommodationBudget;
    private BigDecimal transportationBudget;
    private BigDecimal foodBudget;

    @Data
    public static class VotedPlanDTO {
        private Integer id;
        private MemberDTO member;
    }

    @Data
    public static class MemberDTO {
        private Integer id;
        private BigDecimal expectedPrice;
        private Integer idRole;
        private Integer idStatus;
        private Boolean isReady;
        private PersonDTO person;
    }

    @Data
    public static class PersonDTO {
        private Integer id;
        private String name;
        private String email;
        private String phoneNumber;
        private String photoUri;
        private String firebaseUid;
    }
}
