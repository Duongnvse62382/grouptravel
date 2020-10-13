package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.repository.PrintHighestVotePlanRepository;
import com.fpt.gta.repository.PrintHighestVotePlanRepositoryIml;
import com.fpt.gta.view.PrintHighestVotePlanView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintHighestVotePlanPresenter {
    private Context mContext;
    private PrintHighestVotePlanView mPrintHighestVotePlanView;
    private PrintHighestVotePlanRepository mPrintHighestVotePlanRepository;


    public PrintHighestVotePlanPresenter(Context mContext, PrintHighestVotePlanView mPrintHighestVotePlanView) {
        this.mContext = mContext;
        this.mPrintHighestVotePlanView = mPrintHighestVotePlanView;
        this.mPrintHighestVotePlanRepository = new PrintHighestVotePlanRepositoryIml();
    }

    public void getHighestVotePlan(Integer idTrip){
        mPrintHighestVotePlanRepository.getHighestVotePlan(mContext, idTrip, new CallBackData<List<PlanDTO>>() {
            @Override
            public void onSuccess(List<PlanDTO> planDTOS) {
                mPrintHighestVotePlanView.printVotePlanSuccess(planDTOS);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintHighestVotePlanView.printVotePlanFail(message);
            }
        });
    }
}
