package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteGroupRepository;
import com.fpt.gta.repository.DeleteGroupRepositoryIml;
import com.fpt.gta.view.DeleteGroupView;
import com.fpt.gta.webService.CallBackData;

public class DeleteGroupPresenter {
    private Context mContext;
    private DeleteGroupView mDeleteGroupView;
    private DeleteGroupRepository mDeleteGroupRepository;

    public DeleteGroupPresenter(Context mContext, DeleteGroupView mDeleteGroupView) {
        this.mContext = mContext;
        this.mDeleteGroupView = mDeleteGroupView;
        this.mDeleteGroupRepository = new DeleteGroupRepositoryIml();
    }

    public void deleteGroup(int idGroup){
        mDeleteGroupRepository.deleteGroup(mContext, idGroup,new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteGroupView.deleteGroupSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteGroupView.deleteFail(message);

            }
        });
    }
}
