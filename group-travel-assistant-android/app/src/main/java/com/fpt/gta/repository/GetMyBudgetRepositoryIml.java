package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.IndividualBudgetDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMyBudgetRepositoryIml implements GetMyBudgetRepository {
    @Override
    public void getMyBudget(Context context, Integer idGroup, CallBackData<MemberDTO> callBackData) {
        ClientApi clientAPI = new ClientApi();
        Call<ResponseBody> bodyCall = clientAPI.gtaService().getMyBudget(idGroup);
        final KProgressHUD khub = KProgressHUDManager.showProgressBar(context);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                KProgressHUDManager.dismiss(context, khub);
                if (response.code() == 200 & response.body() != null) {
                    String result = null;
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = GsonUtil.getGson();
                    Type collectionType = new TypeToken<MemberDTO>() {
                    }.getType();
                    MemberDTO memberDTO = gson.fromJson(result, collectionType);
                    callBackData.onSuccess(memberDTO);
                } else {
                    callBackData.onFail("Fail");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                KProgressHUDManager.dismiss(context, khub);
                callBackData.onFail("Network error");
            }
        });


    }
}
