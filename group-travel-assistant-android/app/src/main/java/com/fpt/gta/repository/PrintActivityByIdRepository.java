package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.webService.CallBackData;

public interface PrintActivityByIdRepository {
    void getActivityById(Context context, Integer idActivity, CallBackData<ActivityDTO> callBackData);
}
