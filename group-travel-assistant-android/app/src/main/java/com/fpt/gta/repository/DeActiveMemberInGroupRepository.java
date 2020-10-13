package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeActiveMemberInGroupRepository {
    void deActiveMemberInGroup(Context context, Integer idGroup, Integer idMember, CallBackData callBackData);

}
