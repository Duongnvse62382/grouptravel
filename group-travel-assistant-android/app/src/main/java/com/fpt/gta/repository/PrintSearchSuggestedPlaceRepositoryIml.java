package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrintSearchSuggestedPlaceRepositoryIml implements PrintSearchSuggestedPlaceRepository {
    @Override
    public void getSearchSuggestedPlace(Context context, Integer idTrip, CallBackData<List<PlaceDTO>> callBackData) {
        ClientApi clientApi = new ClientApi();
        Call<ResponseBody> bodyCall = clientApi.gtaService().getSearchSuggestedPlace(idTrip);
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
                    Type collectionType = new TypeToken<List<PlaceDTO>>() {
                    }.getType();
                    List<PlaceDTO> responseDTOList = gson.fromJson(result, collectionType);
                    callBackData.onSuccess(responseDTOList);
                } else {
                    callBackData.onFail("fial");
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
