package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.MakePlanningRepository;
import com.fpt.gta.repository.MakePlanningRepositoryIml;
import com.fpt.gta.view.MakePlanningView;
import com.fpt.gta.webService.CallBackData;

public class MakePlanningPresenter {
    private Context mContext;
    private MakePlanningView makePlanningView;
    private MakePlanningRepository makePlanningRepository;

    public MakePlanningPresenter(Context mContext, MakePlanningView makePlanningView) {
        this.mContext = mContext;
        this.makePlanningView = makePlanningView;
        this.makePlanningRepository = new MakePlanningRepositoryIml();
    }

    public void makePlanning(Integer idGroup) {
        makePlanningRepository.makePlanning(mContext, idGroup, new CallBackData() {
            @Override
            public void onSuccess(Object o) {
                makePlanningView.makePlanningSuccess("Success");
            }

            @Override
            public void onSuccessString(String mess) {
                makePlanningView.makePlanningSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                makePlanningView.makePlanningFail("Ready Fail");
            }
        });
    }
}
