package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintBudgetElectedPlanRepository {
    void printBudgetElectedPlan(Context context, int groupId, CallBackData<List<TripReponseDTO>> callBackData);

}
