package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.CreateVotePlanRepository;
import com.fpt.gta.repository.CreateVotePlanRepositoryIml;
import com.fpt.gta.view.CreateVotePlanView;
import com.fpt.gta.webService.CallBackData;

public class CreateVotePlanPresenter {
    private Context mContext;
    private CreateVotePlanView mCreateVotePlanView;
    private CreateVotePlanRepository mCreateVotePlanRepository;

    public CreateVotePlanPresenter(Context mContext, CreateVotePlanView mCreateVotePlanView) {
        this.mContext = mContext;
        this.mCreateVotePlanView = mCreateVotePlanView;
        this.mCreateVotePlanRepository = new CreateVotePlanRepositoryIml();
    }

    public void createVotePlan(Integer idPlan){
        mCreateVotePlanRepository.createVotePlan(mContext, idPlan, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mCreateVotePlanView.createVotePlanSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mCreateVotePlanView.createVotePlanFail(message);
            }
        });
    }
}
