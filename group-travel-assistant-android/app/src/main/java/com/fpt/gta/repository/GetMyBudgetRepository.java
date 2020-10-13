package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.IndividualBudgetDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface GetMyBudgetRepository {
    void getMyBudget(Context context, Integer idGroup, CallBackData<MemberDTO> callBackData);
}


