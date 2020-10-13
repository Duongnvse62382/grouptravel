package com.fpt.gta.view;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;

import java.util.List;

public interface PrintAllActivityInPlanView {
    void printAllActivitySuccess(List<ActivityDTO> activityDTOList);
    void printAllActivityFail(String messageFail);
}
