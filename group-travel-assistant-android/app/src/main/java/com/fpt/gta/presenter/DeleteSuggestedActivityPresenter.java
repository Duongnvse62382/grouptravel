package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteSuggestedActivityRespository;
import com.fpt.gta.repository.DeleteSuggestedActivityRespositoryIml;
import com.fpt.gta.view.DeleteSuggestedActivityView;
import com.fpt.gta.webService.CallBackData;

public class DeleteSuggestedActivityPresenter {
    private Context mContext;
    private DeleteSuggestedActivityView mDeleteSuggestedActivityView;
    private DeleteSuggestedActivityRespository mDeleteSuggestedActivityRespository;

    public DeleteSuggestedActivityPresenter(Context mContext, DeleteSuggestedActivityView mDeleteSuggestedActivityView) {
        this.mContext = mContext;
        this.mDeleteSuggestedActivityView = mDeleteSuggestedActivityView;
        this.mDeleteSuggestedActivityRespository = new DeleteSuggestedActivityRespositoryIml();
    }

    public void deleteSuggestedActivity(Integer idSuggested){
        mDeleteSuggestedActivityRespository.deleteSuggestedActivity(mContext, idSuggested, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteSuggestedActivityView.deleteSuggestedSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteSuggestedActivityView.deleteSuggestedFail(message);
            }
        });
    }

}
