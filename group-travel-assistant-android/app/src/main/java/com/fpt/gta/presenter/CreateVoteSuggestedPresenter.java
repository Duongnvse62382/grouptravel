package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.CreateVoteSuggestedRepository;
import com.fpt.gta.repository.CreateVoteSuggestedRepositoryIml;
import com.fpt.gta.view.CreateVoteSuggestedView;
import com.fpt.gta.webService.CallBackData;

public class CreateVoteSuggestedPresenter {
    private Context mContext;
    private CreateVoteSuggestedView mCreateVoteSuggestedView;
    private CreateVoteSuggestedRepository mCreateVoteSuggestedRepository;

    public CreateVoteSuggestedPresenter(Context mContext, CreateVoteSuggestedView mCreateVoteSuggestedView) {
        this.mContext = mContext;
        this.mCreateVoteSuggestedView = mCreateVoteSuggestedView;
        this.mCreateVoteSuggestedRepository = new CreateVoteSuggestedRepositoryIml();
    }

    public void createVote(Integer idSuggested){
        mCreateVoteSuggestedRepository.createVote(mContext, idSuggested, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mCreateVoteSuggestedView.createVoteSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mCreateVoteSuggestedView.createVoteFail(message);
            }
        });
    }
}
