package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.webService.CallBackData;

public interface CreateTaskRepository {
    void createTask(Context context, Integer idActivity, TaskDTO taskDTO, CallBackData callBackData);
}
