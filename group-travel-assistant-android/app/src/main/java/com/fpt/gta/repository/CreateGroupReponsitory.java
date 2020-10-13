package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;


public interface CreateGroupReponsitory {
    void createGroup(Context context, GroupResponseDTO groupResponseDTO, CallBackData<GroupResponseDTO> callBackData);
}
