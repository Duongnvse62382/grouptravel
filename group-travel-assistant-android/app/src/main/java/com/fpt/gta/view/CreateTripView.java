package com.fpt.gta.view;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.views.BaseView;

public interface CreateTripView extends BaseView {
    void createTripSuccess(String messageSuccess);
    void createTripFail(String messageFail);
}
