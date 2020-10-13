package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.webService.CallBackData;

public interface UpdateMyBudgetRepository {
    void updateMyBudget(Context context, Integer idGroup,MemberDTO memberDTO, CallBackData callBackData);
}
