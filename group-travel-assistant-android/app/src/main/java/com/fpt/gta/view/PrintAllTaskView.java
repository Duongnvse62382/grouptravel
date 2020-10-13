package com.fpt.gta.view;
import com.fpt.gta.data.dto.TaskDTO;

import java.util.List;

public interface PrintAllTaskView {
    void printAllTaskSuccess(List<TaskDTO> taskDTOList);
    void printAllTaskFail(String messageFail);
}
