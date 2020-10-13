package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.PrintAllActivityInPlanRepository;
import com.fpt.gta.repository.PrintAllActivityInPlanRepositoryIml;
import com.fpt.gta.repository.PrintGroupReponsitory;
import com.fpt.gta.repository.PrintGroupReponsitotyIml;
import com.fpt.gta.view.PrintAllActivityInPlanView;
import com.fpt.gta.view.PrintAllGroupView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintAllActivityInPlanPresenter {
    private Context mContext;
    private PrintAllActivityInPlanView mPrintAllActivityInPlanView;
    private PrintAllActivityInPlanRepository mPrintAllActivityInPlanRepository;

    public PrintAllActivityInPlanPresenter(Context mContext, PrintAllActivityInPlanView mPrintAllActivityInPlanView) {
        this.mContext = mContext;
        this.mPrintAllActivityInPlanView = mPrintAllActivityInPlanView;
        this.mPrintAllActivityInPlanRepository = new PrintAllActivityInPlanRepositoryIml ();
    }



    public void printAllActivityInPlan(int planId){
        mPrintAllActivityInPlanRepository.printAllActivityInPlanRepository ( mContext, planId, new CallBackData<List<ActivityDTO>> () {
            @Override
            public void onSuccess(List<ActivityDTO> activityDTOList) {
                mPrintAllActivityInPlanView.printAllActivitySuccess ( activityDTOList );
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintAllActivityInPlanView.printAllActivityFail ( message );
            }
        } );
    }
}
