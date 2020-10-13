package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface CreateSuggestedActivityRepository {
    void createSuggestedActivity(Context context, Integer idTrip, SuggestedActivityResponseDTO suggestedActivityResponseDTO, CallBackData callBackData);
}
