package com.fpt.gta.view;

import com.fpt.gta.data.dto.TaskDTO;

import java.util.List;

public interface PrintAllTaskInGroupView {
    void printAllTaskInGroupSuccess(List<TaskDTO> taskDTOList);
    void printAllTaskInGroupFail(String messageFail);
}
