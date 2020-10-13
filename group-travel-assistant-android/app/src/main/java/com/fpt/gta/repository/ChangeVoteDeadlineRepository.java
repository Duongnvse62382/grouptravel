package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.webService.CallBackData;

public interface ChangeVoteDeadlineRepository {
    void changeVoteDeadline(Context context, TripReponseDTO tripReponseDTO, CallBackData callBackData);
}
