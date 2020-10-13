package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface UpdateGroupResponsitory {
    void updateGroup(Context context, GroupResponseDTO groupResponseDTO, CallBackData callBackData);
}
