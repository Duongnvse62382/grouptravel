package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface MakePlanningRepository {
    void makePlanning(Context context, Integer idGroup, CallBackData callBackData);

}
