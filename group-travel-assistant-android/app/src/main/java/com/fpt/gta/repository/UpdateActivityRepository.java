package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.webService.CallBackData;

public interface UpdateActivityRepository {
    void UpdateActivityDay(Context context, int idPlan, ActivityDTO activityDTO, CallBackData<String> callBackData);
}
