package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.view.TripOverviewView;
import com.fpt.gta.repository.PrinTripResponsitoryIml;
import com.fpt.gta.repository.PrintTripResponsitory;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrinaAllTripPresenter {
    private Context mContext;
    private TripOverviewView mView;
    private PrintTripResponsitory printTripResponsitory;

    public PrinaAllTripPresenter(Context mContext, TripOverviewView mView) {
        this.mContext = mContext;
        this.mView = mView;
        this.printTripResponsitory = new PrinTripResponsitoryIml ();
    }

    public void getAllTripByGroupId(int groupId){
        printTripResponsitory.printTrip (mContext, groupId, new CallBackData<List<TripReponseDTO>> () {
            @Override
            public void onSuccess(List<TripReponseDTO> tripReponseDTOS) {
                mView.onSucessFul ( tripReponseDTOS );
            }

            @Override
            public void onSuccessString(String mess) {
            }

            @Override
            public void onFail(String message) {
               mView.onFail(message);
            }
        } );
    }
}
