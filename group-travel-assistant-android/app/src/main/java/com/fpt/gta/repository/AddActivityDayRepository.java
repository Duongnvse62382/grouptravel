package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.webService.CallBackData;

public interface AddActivityDayRepository {
    void AddActivityDay(Context context, int id, ActivityDTO activityDTO,  CallBackData<String> callBackData);
}
