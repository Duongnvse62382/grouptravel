package com.fpt.gta.repository;

import android.content.Context;
import android.util.Log;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePlanInTripRepositoryIml implements CreatePlanInTripRepository{
    @Override
    public void createPlanIntrip(Context context, Integer idTrip, List<ActivityDTO> activityDTOList, CallBackData<PlanDTO> planDTOCallBackData) {
        ClientApi clientApi = new ClientApi();
        Gson gson = GsonUtil.getGson();
        String requestJsonBody = gson.toJson(activityDTOList);
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJsonBody);
        Call<ResponseBody> bodyCall = clientApi.gtaService().createPlanInTrip(idTrip,  requestBody);
        final KProgressHUD khub = KProgressHUDManager.showProgressBar(context);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                KProgressHUDManager.dismiss(context, khub);
                if (response.code() == 200 & response.body() != null) {
                    String result = null;
                    try {
                        result = response.body().string();
                        Gson gson = GsonUtil.getGson();
                        Type collectionType = new TypeToken<PlanDTO> () {
                        }.getType();
                        PlanDTO responseDTOList = gson.fromJson(result, collectionType);
                        planDTOCallBackData.onSuccess (responseDTOList);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    planDTOCallBackData.onFail("Create Fail");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                KProgressHUDManager.dismiss(context, khub);
                planDTOCallBackData.onFail("Network error");

            }
        });
    }
}
