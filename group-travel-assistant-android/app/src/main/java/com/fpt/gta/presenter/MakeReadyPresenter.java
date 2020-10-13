package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.MakeReadyRepository;
import com.fpt.gta.repository.MakeReadyRepositoryIml;
import com.fpt.gta.view.MakeReadyView;
import com.fpt.gta.webService.CallBackData;

public class MakeReadyPresenter {
    private Context mContext;
    private MakeReadyView makeReadyView;
    private MakeReadyRepository makeReadyRepository;

    public MakeReadyPresenter(Context mContext, MakeReadyView makeReadyView) {
        this.mContext = mContext;
        this.makeReadyView = makeReadyView;
        this.makeReadyRepository = new MakeReadyRepositoryIml();
    }

    public void makeMeReady(Integer idGroup, boolean isReady) {
        makeReadyRepository.makeReady(mContext, idGroup, isReady, new CallBackData() {
            @Override
            public void onSuccess(Object o) {
                makeReadyView.makeReadySuccess("Success");
            }

            @Override
            public void onSuccessString(String mess) {
                makeReadyView.makeReadySuccess(mess);
            }

            @Override
            public void onFail(String message) {
                makeReadyView.makeReadyFail("Ready Fail");
            }
        });
    }
}
