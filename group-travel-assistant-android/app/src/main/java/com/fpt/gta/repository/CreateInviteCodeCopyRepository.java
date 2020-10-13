package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface CreateInviteCodeCopyRepository {
    void createInviteCodeCopy(Context context,Integer idGroup ,String invitationCode, CallBackData<GroupResponseDTO> callBackData);
}
