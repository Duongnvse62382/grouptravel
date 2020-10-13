package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.repository.UpdateTripResponsitory;
import com.fpt.gta.repository.UpdateTripResponsitoryIml;
import com.fpt.gta.view.UpdateTripView;
import com.fpt.gta.webService.CallBackData;

public class UpdateTripPresenter {
    private UpdateTripView mView;
    private Context mContext;
    private UpdateTripResponsitory mRepo;

    public UpdateTripPresenter(UpdateTripView mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
        this.mRepo = new UpdateTripResponsitoryIml ();
    }

    public void updateTrip(TripReponseDTO tripReponseDTO){
        mRepo.updateTrip (mContext, tripReponseDTO, new CallBackData<String> () {
            @Override
            public void onSuccess(String s) {
                mView.updateTripSuccess ( s );
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mView.updateTripFail(message);
            }
        } );
    }
}
