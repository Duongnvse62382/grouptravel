package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface SearchPlaceRepository {
    void searchPlace(Context context, String srearchValues, CallBackData<List<PlaceDTO>> callBackData);
}
