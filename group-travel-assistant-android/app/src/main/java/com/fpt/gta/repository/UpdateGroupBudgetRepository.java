package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.IndividualBudgetDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

import javax.security.auth.callback.Callback;

public interface UpdateGroupBudgetRepository {
    void updateGroupBudget(Context context, Integer idGroup, GroupResponseDTO groupResponseDTO, CallBackData callBackData);
}
