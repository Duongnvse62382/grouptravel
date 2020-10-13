package com.fpt.gta.repository;

import android.content.Context;

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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrintTripDetailRepositoryIml implements PrintTripDetailRepository {
    @Override
    public void getTripDetail(Context context, Integer idTrip, CallBackData<TripReponseDTO> callBackData) {
        ClientApi clientApi=  new ClientApi ();
        Call<ResponseBody> responseBody = clientApi.gtaService ().getViewTripDetail ( idTrip );
        final KProgressHUD khub = KProgressHUDManager.showProgressBar(context);
        responseBody.enqueue ( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                KProgressHUDManager.dismiss(context, khub);
                if(response.code () ==200 && response.body ()!=null){
                    String result = null;
                    try {
                        result = response.body ().string();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                    Gson gson = GsonUtil.getGson();
                    Type type = new TypeToken<TripReponseDTO>(){
                    }.getType ();
                    TripReponseDTO mTripReponseDTO = gson.fromJson ( result,type );
                    callBackData.onSuccess ( mTripReponseDTO );
                }
                else {
                    callBackData.onFail ( "Fail" );
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
