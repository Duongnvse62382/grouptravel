package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteActivityRepository;
import com.fpt.gta.repository.DeleteActivityRepositoryIml;
import com.fpt.gta.view.DeleteActivityView;
import com.fpt.gta.webService.CallBackData;

public class DeleteActivityPresenter {
    private Context mContext;
    private DeleteActivityView mDeleteActivityView;
    private DeleteActivityRepository mDeleteActivityRepository ;

    public DeleteActivityPresenter(Context mContext, DeleteActivityView mDeleteActivityView) {
        this.mContext = mContext;
        this.mDeleteActivityView = mDeleteActivityView;
        this.mDeleteActivityRepository = new DeleteActivityRepositoryIml();
    }


    public void deleteSuggestedActivity(Integer idActivity){
        mDeleteActivityRepository.deleteActivity(mContext, idActivity, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteActivityView.deleteActivitySuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteActivityView.deleteActivityFail(message);
            }
        });
    }
}
