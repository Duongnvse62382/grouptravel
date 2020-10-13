package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;

import java.math.BigDecimal;

public interface SetBudgetPlanRepository {
    void setBudgetPlan(Context context, Integer idPlan, BigDecimal activityBudget, BigDecimal accommodationBudget, BigDecimal transportationBudget, BigDecimal foodBudget, CallBackData callBackData);

}
