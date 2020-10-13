package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.PickHighestVotePlanRepository;
import com.fpt.gta.repository.PickHighestVotePlanRepositoryIml;
import com.fpt.gta.view.PickHighestVotePlanView;
import com.fpt.gta.webService.CallBackData;

public class PickHighestVotePlanPresenter {
    private Context mContext;
    private PickHighestVotePlanView mPickHighestVotePlanView;
    private PickHighestVotePlanRepository mPickHighestVotePlanRepository;

    public PickHighestVotePlanPresenter(Context mContext, PickHighestVotePlanView mPickHighestVotePlanView) {
        this.mContext = mContext;
        this.mPickHighestVotePlanView = mPickHighestVotePlanView;
        mPickHighestVotePlanRepository = new PickHighestVotePlanRepositoryIml();
    }

    public void resolveConflictPlan(Integer idPlan){
        mPickHighestVotePlanRepository.resolveConflictPlan(mContext, idPlan, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mPickHighestVotePlanView.PickVotePlanSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mPickHighestVotePlanView.PickVotePlanFail(message);
            }
        });
    }
}
