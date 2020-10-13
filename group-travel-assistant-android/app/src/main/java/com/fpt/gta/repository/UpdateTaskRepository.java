package com.fpt.gta.repository;

import android.content.Context;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.webService.CallBackData;

public interface UpdateTaskRepository {
    void updateTask(Context context, TaskDTO taskDTO, CallBackData callBackData);
}
