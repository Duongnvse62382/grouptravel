package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintSearchSuggestedPlaceRepository {
    void getSearchSuggestedPlace(Context context, Integer idTrip, CallBackData<List<PlaceDTO>> callBackData);
}
