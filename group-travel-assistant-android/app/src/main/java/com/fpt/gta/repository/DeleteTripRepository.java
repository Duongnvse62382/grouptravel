package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteTripRepository {
    void deleteTrip(Context context, int tripId, CallBackData callBackData);
}
