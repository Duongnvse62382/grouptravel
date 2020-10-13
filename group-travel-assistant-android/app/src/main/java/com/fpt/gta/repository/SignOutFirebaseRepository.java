package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.AppInstanceDTO;
import com.fpt.gta.webService.CallBackData;

public interface SignOutFirebaseRepository {
    void signoutcFirebase(Context context, AppInstanceDTO appInstanceDTO, CallBackData callBackData);
}
