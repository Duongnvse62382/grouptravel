package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.AppInstanceDTO;
import com.fpt.gta.webService.CallBackData;

public interface LoginReponsitory {
    void syncFirebase(Context context, AppInstanceDTO appInstanceDTO, CallBackData callBackData);
}
