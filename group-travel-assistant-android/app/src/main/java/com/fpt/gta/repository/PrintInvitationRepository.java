package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface PrintInvitationRepository {
    void printInvitation(Context context, int idGroup, CallBackData<String> callBackData);
}
