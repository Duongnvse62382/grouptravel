package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface UpdateTripResponsitory {
    void updateTrip(Context context,TripReponseDTO tripReponseDTO, CallBackData<String> callBackData);
}
