package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeletePlanInTripRepository {
    void deletePlanIntrip(Context context, Integer idPlan, CallBackData callBackData);
}
