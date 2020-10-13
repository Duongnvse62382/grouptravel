package com.fpt.gta.repository;

import android.content.Context;
import android.util.Log;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class    AddActivityDayRepositoryIml implements AddActivityDayRepository {

    @Override
    public void AddActivityDay(Context context, int id, ActivityDTO activityDTO, CallBackData<String> callBackData) {
        ClientApi clientAPI = new ClientApi();
        Gson gson = GsonUtil.getGson();
        String requestJsonBody = gson.toJson ( activityDTO );
        Log.d("API", requestJsonBody);
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJsonBody);
        Call<ResponseBody> responseBodyCall = clientAPI.gtaService ().createActivity ( id, requestBody );
        final KProgressHUD khub = KProgressHUDManager.showProgressBar(context);
        responseBodyCall.enqueue ( new Callback<ResponseBody> () {
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
                    callBackData.onSuccess ("Create activity success");
                }else {
                    callBackData.onFail ("Create activity fail");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                KProgressHUDManager.dismiss(context, khub);
                callBackData.onFail("Network error");
            }
        } );
    }
}
