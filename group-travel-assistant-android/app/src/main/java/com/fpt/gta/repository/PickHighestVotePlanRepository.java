package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface PickHighestVotePlanRepository {
    void resolveConflictPlan(Context context, Integer idPlan, CallBackData callBackData);
}
