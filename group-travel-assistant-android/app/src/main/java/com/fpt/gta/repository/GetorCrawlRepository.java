package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.webService.CallBackData;


public interface GetorCrawlRepository {
    void getOrCrawl(Context context, String googlePlaceId, Integer idTrip, CallBackData<PlaceDTO> callBackData);
}
