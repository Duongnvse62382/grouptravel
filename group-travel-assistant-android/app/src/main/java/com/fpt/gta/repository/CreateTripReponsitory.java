package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface CreateTripReponsitory {
    void createTrip(Context context,int groupId, TripReponseDTO tripReponseDTO, CallBackData<String >callBackData);
}
