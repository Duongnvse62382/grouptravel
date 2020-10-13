package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintTripResponsitory {
    void printTrip(Context context, int groupId , CallBackData<List<TripReponseDTO>> callBackData);
}
