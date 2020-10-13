package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvertCurrencyRepositoryIml implements ConvertCurrencyRepository {
    @Override
    public void convertCurrency(Context context, String firstCurrency, String secondCurrency, CallBackData<BigDecimal> callBackData) {
        ClientApi clientApi = new ClientApi();
        Call<ResponseBody> bodyCall = clientApi.gtaService().convertCurrency(firstCurrency, secondCurrency);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200 & response.body() != null) {
                    String result = null;
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BigDecimal bigDecimal = new BigDecimal(result);
                    callBackData.onSuccess(bigDecimal);
                } else {
                    callBackData.onFail("fail");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                callBackData.onFail("Network error");
            }
        });
    }
}
