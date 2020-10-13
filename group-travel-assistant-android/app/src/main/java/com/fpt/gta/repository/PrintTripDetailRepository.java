package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface PrintTripDetailRepository {
    void getTripDetail(Context context, Integer idTrip, CallBackData<TripReponseDTO> callBackData);
}
