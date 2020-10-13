package com.fpt.gta.view;

import com.fpt.gta.data.dto.PlanDTO;

public interface CreatePlanInTripView {
    void getIdPlan(PlanDTO planDTO);
    void createPlanFail(String messageFail);
}
