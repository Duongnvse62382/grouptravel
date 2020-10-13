package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteTaskRepository {
    void deleteTask(Context context, Integer idTask , CallBackData callBackData);
}
