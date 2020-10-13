package com.fpt.gta.rest.managebudget;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
public class MemberDTO {
    private Integer id;
    private BigDecimal expectedPrice;
    private Integer idRole;
    private Integer idStatus;
    private PersonDTO person;
    private Boolean isReady;

    private BigDecimal activityBudget = BigDecimal.ZERO;
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    private BigDecimal foodBudget = BigDecimal.ZERO;

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
