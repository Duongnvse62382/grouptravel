package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteVoteSuggestedRepository;
import com.fpt.gta.repository.DeleteVoteSuggestedRepositoryIml;
import com.fpt.gta.view.DeleteVoteSuggestedView;
import com.fpt.gta.webService.CallBackData;

public class DeleteVoteSuggestedPresenter {
    private Context mContext;
    private DeleteVoteSuggestedView mDeleteVoteSuggestedView;
    private DeleteVoteSuggestedRepository mDeleteVoteSuggestedRepository;

    public DeleteVoteSuggestedPresenter(Context mContext, DeleteVoteSuggestedView mDeleteVoteSuggestedView) {
        this.mContext = mContext;
        this.mDeleteVoteSuggestedView = mDeleteVoteSuggestedView;
        this.mDeleteVoteSuggestedRepository = new DeleteVoteSuggestedRepositoryIml();
    }

    public void deleteVote(Integer idSuggested){
        mDeleteVoteSuggestedRepository.deleteVote(mContext, idSuggested, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }
            @Override
            public void onSuccessString(String mess) {
                mDeleteVoteSuggestedView.deleteVoteSuccess(mess);

            }

            @Override
            public void onFail(String message) {
                mDeleteVoteSuggestedView.deleteVoteFail(message);
            }
        });
    }
}
