package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteGroupRepository {
    void deleteGroup(Context context,int idGroup ,CallBackData callBackData);
}
