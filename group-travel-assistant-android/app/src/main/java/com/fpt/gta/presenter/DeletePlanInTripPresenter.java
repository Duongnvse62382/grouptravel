package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeletePlanInTripRepository;
import com.fpt.gta.repository.DeletePlanInTripRepositoryIml;
import com.fpt.gta.view.DeletePlanInTripView;
import com.fpt.gta.webService.CallBackData;

public class DeletePlanInTripPresenter {
    private Context mContext;
    private DeletePlanInTripView mDeletePlanInTripView;
    private DeletePlanInTripRepository mDeletePlanInTripRepository;

    public DeletePlanInTripPresenter(Context mContext, DeletePlanInTripView mDeletePlanInTripView) {
        this.mContext = mContext;
        this.mDeletePlanInTripView = mDeletePlanInTripView;
        this.mDeletePlanInTripRepository = new DeletePlanInTripRepositoryIml();
    }

    public void deletePlanInTrip(int idPlan){
        mDeletePlanInTripRepository.deletePlanIntrip(mContext, idPlan, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeletePlanInTripView.deletePlanSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mDeletePlanInTripView.deletePlanFail(message);
            }
        });
    }
}
