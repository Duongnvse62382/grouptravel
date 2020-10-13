package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

import java.math.BigDecimal;

public interface MakeReadyRepository {
    void makeReady(Context context,Integer idGroup, boolean isReady, CallBackData callBackData);
}
