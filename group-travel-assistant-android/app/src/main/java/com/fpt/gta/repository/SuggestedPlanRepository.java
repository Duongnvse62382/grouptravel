package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface SuggestedPlanRepository {
    void suggestedPlan(Context context, Integer idTrip, CallBackData<List<ActivityDTO>> callBackData);
}
