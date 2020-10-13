package com.fpt.gta.view;

import com.fpt.gta.data.dto.ActivityDTO;

import java.util.List;

public interface SuggestedPlanView {
    void suggestedPlanSuccess(List<ActivityDTO> activityDTOList);
    void suggestedPlanFail(String messageFail);
}
