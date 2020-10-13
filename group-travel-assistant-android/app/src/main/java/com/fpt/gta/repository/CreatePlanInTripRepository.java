package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface CreatePlanInTripRepository {
    void createPlanIntrip(Context context, Integer idTrip, List<ActivityDTO> activityDTOList, CallBackData<PlanDTO> planDTOCallBackData);
}
