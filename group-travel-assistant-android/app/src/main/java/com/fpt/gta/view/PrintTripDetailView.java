package com.fpt.gta.view;

import com.fpt.gta.data.dto.TripReponseDTO;

public interface PrintTripDetailView {
    void getTripDetailSuccess(TripReponseDTO tripReponseDTO);
    void getTripDetailFail(String message);
}
