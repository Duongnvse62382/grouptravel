package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
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

public class PrintAllCurrencyRepositoryIml implements PrintAllCurrencyRepository{
    @Override
    public void printAllCurrency(Context context, CallBackData<List<CurrencyDTO>> callBackData) {
        ClientApi clientAPI = new ClientApi();
        Call<ResponseBody> bodyCall = clientAPI.gtaService().getAllCurrency();
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
                    Type collectionType = new TypeToken<ArrayList<CurrencyDTO>>() {
                    }.getType();
                    List<CurrencyDTO> currencyDTOList = gson.fromJson(result, collectionType);
                    callBackData.onSuccess(currencyDTOList);
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
