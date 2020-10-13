package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.webService.ClientApi;
import com.kaopiz.kprogresshud.KProgressHUD;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePositionTaskRepositoryIml implements ChangePositionTaskRepository {
    @Override
    public void changePositionTask(Context context, Integer idTask, Integer order, CallBackData callBackData) {
        ClientApi clientAPI = new ClientApi();
        Call<ResponseBody> bodyCall = clientAPI.gtaService().changePositionTask(idTask, order);
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
                    callBackData.onSuccessString("Update success");
                }else{
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
