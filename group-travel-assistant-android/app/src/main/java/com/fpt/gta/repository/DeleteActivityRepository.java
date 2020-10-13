package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteActivityRepository {
    void deleteActivity(Context context, Integer idActivity, CallBackData callBackData);

}
