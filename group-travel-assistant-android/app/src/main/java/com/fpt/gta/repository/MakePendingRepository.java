package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface MakePendingRepository {
    void makePending(Context context,Integer idGroup, GroupResponseDTO groupResponseDTO, CallBackData callBackData);
}
