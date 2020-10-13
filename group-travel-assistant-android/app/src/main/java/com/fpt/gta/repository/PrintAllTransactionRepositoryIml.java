package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.data.dto.TripReponseDTO;
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

public class PrintAllTransactionRepositoryIml implements PrintAllTransactionRepository {

    @Override
    public void printAllTransactionRepository(Context mContext, int idGroup, CallBackData<List<TransactionDTO>> listCallBackData) {
        ClientApi clientApi = new ClientApi();
        Call<ResponseBody> bodyCall = clientApi.gtaService().getAllTransaction(idGroup);
        final KProgressHUD khub = KProgressHUDManager.showProgressBar(mContext);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                KProgressHUDManager.dismiss(mContext, khub);
                if(response.code () ==200 && response.body ()!=null){
                    String result = null;
                    try {
                        result = response.body ().string();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                    Gson gson = GsonUtil.getGson();
                    Type type = new TypeToken<List<TransactionDTO>>(){
                    }.getType ();
                    List<TransactionDTO> mTransactionReponseDTOList = gson.fromJson ( result,type );
                    listCallBackData.onSuccess ( mTransactionReponseDTOList );
                }
                else {
                    listCallBackData.onFail ( "Fail" );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                KProgressHUDManager.dismiss(mContext, khub);
                listCallBackData.onFail("Network error");
            }
        });

    }
}
