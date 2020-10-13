package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintHighestVotePlanRepository {
    void getHighestVotePlan (Context context, Integer idTrip , CallBackData<List<PlanDTO>> callBackData);
}
