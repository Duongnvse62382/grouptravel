package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.repository.ChangeVoteDeadlineRepository;
import com.fpt.gta.repository.ChangeVoteDeadlineRepositoryIml;
import com.fpt.gta.view.ChangeVoteDeadlineView;
import com.fpt.gta.webService.CallBackData;

public class ChangeVoteDeadlinePresenter {
    private Context mContext;
    private ChangeVoteDeadlineRepository mChangeVoteDeadlineRepository;
    private ChangeVoteDeadlineView mChangeVoteDeadlineView;

    public ChangeVoteDeadlinePresenter(Context mContext, ChangeVoteDeadlineView mChangeVoteDeadlineView) {
        this.mContext = mContext;
        this.mChangeVoteDeadlineView = mChangeVoteDeadlineView;
        this.mChangeVoteDeadlineRepository = new ChangeVoteDeadlineRepositoryIml();
    }

    public void changeVotePlan(TripReponseDTO tripReponseDTO){
        mChangeVoteDeadlineRepository.changeVoteDeadline(mContext, tripReponseDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mChangeVoteDeadlineView.changeVotePlanSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mChangeVoteDeadlineView.changeVotePlanFail(message);
            }
        });
    }
}
