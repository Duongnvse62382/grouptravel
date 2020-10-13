package com.fpt.gta.view;

import com.fpt.gta.data.dto.PlanDTO;

import java.util.List;

public interface PrintHighestVotePlanView {
    void printVotePlanSuccess(List<PlanDTO> planDTOList);
    void printVotePlanFail(String messageFail);
}
