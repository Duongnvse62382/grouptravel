package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.repository.PrintTripDetailRepository;
import com.fpt.gta.repository.PrintTripDetailRepositoryIml;
import com.fpt.gta.view.PrintTripDetailView;
import com.fpt.gta.webService.CallBackData;

public class PrintTripDetailPresenter {
    private Context mContext;
    private PrintTripDetailView mPrintTripDetailView;
    private PrintTripDetailRepository mPrintTripDetailRepository;

    public PrintTripDetailPresenter(Context mContext, PrintTripDetailView mPrintTripDetailView) {
        this.mContext = mContext;
        this.mPrintTripDetailView = mPrintTripDetailView;
        mPrintTripDetailRepository = new PrintTripDetailRepositoryIml();
    }

    public void getTripDetail(Integer idTrip){
        mPrintTripDetailRepository.getTripDetail(mContext, idTrip, new CallBackData<TripReponseDTO>() {
            @Override
            public void onSuccess(TripReponseDTO tripReponseDTO) {
                mPrintTripDetailView.getTripDetailSuccess(tripReponseDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintTripDetailView.getTripDetailFail(message);
            }
        });
    }
}
