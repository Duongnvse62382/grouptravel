package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.repository.PrintPlanInTripRepository;
import com.fpt.gta.repository.PrintPlanInTripRepositoryIml;
import com.fpt.gta.view.PrintPlanInTripView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintPlanInTripPresenter {
    private Context mContext;
    private PrintPlanInTripView mPrintPlanInTripView;
    private PrintPlanInTripRepository mPrintPlanInTripRepository;

    public PrintPlanInTripPresenter(Context mContext, PrintPlanInTripView mPrintPlanInTripView) {
        this.mContext = mContext;
        this.mPrintPlanInTripView = mPrintPlanInTripView;
        this.mPrintPlanInTripRepository = new PrintPlanInTripRepositoryIml();
    }

    public void printPlanInTrip(Integer idTrip){
        mPrintPlanInTripRepository.printPlanInTrip(mContext, idTrip, new CallBackData<List<PlanDTO>>() {
            @Override
            public void onSuccess(List<PlanDTO> planDTOS) {
                mPrintPlanInTripView.printPlanSuccess(planDTOS);
            }

            @Override
            public void onSuccessString(String mess) {
            }

            @Override
            public void onFail(String message) {
                mPrintPlanInTripView.printPlanFail(message);
            }
        });
    }
}
