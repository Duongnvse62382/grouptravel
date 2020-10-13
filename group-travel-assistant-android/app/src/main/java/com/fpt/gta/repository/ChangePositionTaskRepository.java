package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface ChangePositionTaskRepository {
    void changePositionTask(Context context, Integer idTask, Integer order, CallBackData callBackData);
}
