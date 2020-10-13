package com.fpt.gta.data.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class BudgetOverViewDTO implements Serializable {
    private String budgetCityName;
    private int idTrip;
    private BigDecimal activityBudget = BigDecimal.ZERO;
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    private BigDecimal foodBudget = BigDecimal.ZERO;
    private BigDecimal actualCost = BigDecimal.ZERO;

    public BudgetOverViewDTO(String budgetCityName, BigDecimal activityBudget, BigDecimal accommodationBudget, BigDecimal transportationBudget, BigDecimal actualCost) {
        this.budgetCityName = budgetCityName;
        this.activityBudget = activityBudget;
        this.accommodationBudget = accommodationBudget;
        this.transportationBudget = transportationBudget;
        this.actualCost = actualCost;
    }

    public BudgetOverViewDTO(String budgetCityName, BigDecimal activityBudget, BigDecimal accommodationBudget, BigDecimal transportationBudget, BigDecimal foodBudget, BigDecimal actualCost) {
        this.budgetCityName = budgetCityName;
        this.activityBudget = activityBudget;
        this.accommodationBudget = accommodationBudget;
        this.transportationBudget = transportationBudget;
        this.foodBudget = foodBudget;
        this.actualCost = actualCost;
    }

    public BudgetOverViewDTO() {
    }

    public BigDecimal getActivityBudget() {
        return activityBudget;
    }

    public void setActivityBudget(BigDecimal activityBudget) {
        this.activityBudget = activityBudget;
    }


    public BigDecimal getFoodBudget() {
        return foodBudget;
    }

    public void setFoodBudget(BigDecimal foodBudget) {
        this.foodBudget = foodBudget;
    }

    public BigDecimal getAccommodationBudget() {
        return accommodationBudget;
    }

    public void setAccommodationBudget(BigDecimal accommodationBudget) {
        this.accommodationBudget = accommodationBudget;
    }

    public BigDecimal getTransportationBudget() {
        return transportationBudget;
    }

    public void setTransportationBudget(BigDecimal transportationBudget) {
        this.transportationBudget = transportationBudget;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }
}
