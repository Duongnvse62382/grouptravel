package com.fpt.gta.view;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.views.BaseView;

import java.util.List;

public interface TripOverviewView extends BaseView {
    void onSucessFul(List<TripReponseDTO> tripReponseDTOList);
    void onFail(String messageFail);
}
