package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTripReponsitoryIml implements CreateTripReponsitory{
    @Override
    public void createTrip(Context context, int groupId, TripReponseDTO tripReponseDTO, CallBackData<String> callBackData) {
        ClientApi clientApi = new ClientApi ();
        Gson gson = GsonUtil.getGson();
        String requestJsonBody = gson.toJson(tripReponseDTO);
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJsonBody);
        Call<ResponseBody> responseBodyCall = clientApi.gtaService ().createTrip ( groupId, requestBody);
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
                    callBackData.onSuccess ("Create success");
                }else {
                    callBackData.onFail("Create Fail");
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
