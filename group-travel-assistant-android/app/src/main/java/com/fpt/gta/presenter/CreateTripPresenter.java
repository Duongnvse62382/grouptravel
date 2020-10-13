package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.repository.CreateTripReponsitory;
import com.fpt.gta.repository.CreateTripReponsitoryIml;
import com.fpt.gta.view.CreateTripView;
import com.fpt.gta.webService.CallBackData;

public class CreateTripPresenter {
    private Context mContext;
    private CreateTripView createTripView;
    private CreateTripReponsitory mCreateTripReponsitory;

    public CreateTripPresenter(Context mContext, CreateTripView createTripView) {
        this.mContext = mContext;
        this.createTripView = createTripView;
        this.mCreateTripReponsitory = new CreateTripReponsitoryIml ();
    }


    public void createTrip(int groupId, TripReponseDTO tripReponseDTO) {
        mCreateTripReponsitory.createTrip (mContext, groupId, tripReponseDTO, new CallBackData<String> () {
            @Override
            public void onSuccess(String s) {
                createTripView.createTripSuccess ( s );
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                createTripView.createTripFail(message);
            }
        } );
    }
}
