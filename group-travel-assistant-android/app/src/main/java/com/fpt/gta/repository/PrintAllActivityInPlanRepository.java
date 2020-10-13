package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintAllActivityInPlanRepository {
    void printAllActivityInPlanRepository(Context context, int idPlan, CallBackData<List<ActivityDTO>> callBackData);

}
