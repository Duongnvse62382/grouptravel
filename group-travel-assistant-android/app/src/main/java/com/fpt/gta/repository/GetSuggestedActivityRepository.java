package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface GetSuggestedActivityRepository {
    void printSuggestedActivity(Context context, Integer idTrip ,CallBackData<List<SuggestedActivityResponseDTO>> callBackData);
}
