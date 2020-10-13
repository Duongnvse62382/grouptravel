package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrintGroupReponsitotyIml implements PrintGroupReponsitory {
    @Override
    public void printGroup(Context context, CallBackData<List<GroupResponseDTO>> callBackData) {
        ClientApi clientAPI = new ClientApi();
        Call<ResponseBody> bodyCall = clientAPI.gtaService().getViewAllGroup();
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
                    Type collectionType = new TypeToken<ArrayList<GroupResponseDTO>>() {
                    }.getType();
                    List<GroupResponseDTO> responseDTOList = gson.fromJson(result, collectionType);
                    callBackData.onSuccess(responseDTOList);
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
