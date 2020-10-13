package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.repository.SuggestedPlanRepository;
import com.fpt.gta.repository.SuggestedPlanRepositoryIml;
import com.fpt.gta.view.SuggestedPlanView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class SuggestedPlanPresenter {
    private Context mContext;
    private SuggestedPlanRepository mSuggestedPlanRepository;
    private SuggestedPlanView mSuggestedPlanView;

    public SuggestedPlanPresenter(Context mContext, SuggestedPlanView mSuggestedPlanView) {
        this.mContext = mContext;
        this.mSuggestedPlanView = mSuggestedPlanView;
        this.mSuggestedPlanRepository = new SuggestedPlanRepositoryIml();
    }

    public void suggestedPlan(Integer idTrip){
        mSuggestedPlanRepository.suggestedPlan(mContext, idTrip, new CallBackData<List<ActivityDTO>>() {
            @Override
            public void onSuccess(List<ActivityDTO> activityDTOList) {
                mSuggestedPlanView.suggestedPlanSuccess(activityDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mSuggestedPlanView.suggestedPlanFail(message);
            }
        });
    }
}
