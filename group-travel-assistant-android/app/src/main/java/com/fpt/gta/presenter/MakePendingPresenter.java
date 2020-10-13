package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.MakePendingRepository;
import com.fpt.gta.repository.MakePendingRepositoryIml;
import com.fpt.gta.view.MakePendingView;
import com.fpt.gta.webService.CallBackData;

public class MakePendingPresenter {
    private Context mContext;
    private MakePendingView mMakePendingView;
    private MakePendingRepository mMakePendingRepository;

    public MakePendingPresenter(Context mContext, MakePendingView mMakePendingView) {
        this.mContext = mContext;
        this.mMakePendingView = mMakePendingView;
        this.mMakePendingRepository = new MakePendingRepositoryIml();
    }

    public void makePending(Integer idGroup, GroupResponseDTO groupResponseDTO) {
        mMakePendingRepository.makePending(mContext, idGroup, groupResponseDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mMakePendingView.makePendingSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mMakePendingView.makePendingFail(message);
            }
        });
    }
}
