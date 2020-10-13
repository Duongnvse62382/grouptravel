package com.fpt.gta.view;


import com.fpt.gta.data.dto.PlanDTO;

import java.util.List;

public interface PrintPlanInTripView {
    void printPlanSuccess(List<PlanDTO> planDTOList);
    void printPlanFail(String messageFail);
}
