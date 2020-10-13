package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintAllTaskInGroupRepository {
    void printAllTaskInGroup(Context context, Integer idGroup, CallBackData<List<TaskDTO>> callBackData);
}
