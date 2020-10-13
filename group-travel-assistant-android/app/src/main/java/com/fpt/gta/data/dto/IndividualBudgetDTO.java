package com.fpt.gta.data.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class IndividualBudgetDTO {
    private Integer id;
    private BigDecimal activityBudget = BigDecimal.ZERO;
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    private BigDecimal foodBudget = BigDecimal.ZERO;
    private TripReponseDTO trip;
    private MemberDTO member;
}