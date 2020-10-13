package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteVotePlanRepository;
import com.fpt.gta.repository.DeleteVotePlanRepositoryIml;
import com.fpt.gta.view.DeleteVotePlanView;
import com.fpt.gta.webService.CallBackData;

public class DeleteVotePlanPresenter {
    private Context mContext;
    private DeleteVotePlanView mDeleteVotePlanView;
    private DeleteVotePlanRepository mDeleteVotePlanRepository;

    public DeleteVotePlanPresenter(Context mContext, DeleteVotePlanView mDeleteVotePlanView) {
        this.mContext = mContext;
        this.mDeleteVotePlanView = mDeleteVotePlanView;
        this.mDeleteVotePlanRepository = new DeleteVotePlanRepositoryIml();
    }

    public void deleteVotePlan(Integer idPlan){
        mDeleteVotePlanRepository.deleteVotePlan(mContext, idPlan, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteVotePlanView.deleteVotePlanSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mDeleteVotePlanView.deleteVotePlanFail(message);
            }
        });
    }

}
