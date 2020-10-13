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

public class DeleteVoteSuggestedRepositoryIml implements DeleteVoteSuggestedRepository {
    @Override
    public void deleteVote(Context context, Integer idSuggested, CallBackData callBackData) {
        ClientApi clientApi = new ClientApi();
        Call<ResponseBody> bodyCall = clientApi.gtaService().deleteVote(idSuggested);
        final KProgressHUD khub = KProgressHUDManager.showProgressBar(context);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                KProgressHUDManager.dismiss(context, khub);
                if(response.code()==200 & response.body()!=null){
                    String result = null;
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callBackData.onSuccessString("Unlike success");
                }else{
                    callBackData.onFail("Unlike Fail");
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
