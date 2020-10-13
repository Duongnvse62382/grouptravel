package com.fpt.gta.view;

import com.fpt.gta.data.dto.ActivityDTO;

public interface PrintActivityByIdView {
    void printActivityById(ActivityDTO activityDTO);
    void printActivityByIdFail(String messageFail);
}
