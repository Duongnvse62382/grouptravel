package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface PrintGroupPreviewRepository {
    void printGroupPreview(Context context, String idGroup, CallBackData<GroupResponseDTO> callBackData);
}
