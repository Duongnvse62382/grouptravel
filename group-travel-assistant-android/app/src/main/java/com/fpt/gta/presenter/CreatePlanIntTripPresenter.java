package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.repository.CreatePlanInTripRepository;
import com.fpt.gta.repository.CreatePlanInTripRepositoryIml;
import com.fpt.gta.view.CreatePlanInTripView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class CreatePlanIntTripPresenter {
    private Context mContext;
    private CreatePlanInTripView mCreatePlanInTripView;
    private CreatePlanInTripRepository mCreatePlanInTripRepository;

    public CreatePlanIntTripPresenter(Context mContext, CreatePlanInTripView mCreatePlanInTripView) {
        this.mContext = mContext;
        this.mCreatePlanInTripView = mCreatePlanInTripView;
        this.mCreatePlanInTripRepository = new CreatePlanInTripRepositoryIml();
    }

    public void createPlanIntrip(Integer idTrip, List<ActivityDTO> activityDTOList) {
        mCreatePlanInTripRepository.createPlanIntrip ( mContext, idTrip, activityDTOList,new CallBackData<PlanDTO> () {
            @Override
            public void onSuccess(PlanDTO planDTO) {
                mCreatePlanInTripView.getIdPlan (planDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mCreatePlanInTripView.createPlanFail ( message );
            }
        } );
    }

}
