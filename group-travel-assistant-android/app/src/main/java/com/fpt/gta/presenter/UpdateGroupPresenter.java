package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.repository.UpdateGroupResponsitory;
import com.fpt.gta.repository.UpdateGroupResponsitotyIml;
import com.fpt.gta.view.UpdateGroupView;

public class UpdateGroupPresenter {
    private Context mContext;
    private UpdateGroupResponsitory mUpdateGroupResponsitory;
    private UpdateGroupView mUpdateGroupView;

    public UpdateGroupPresenter(Context mContext, UpdateGroupView mUpdateGroupView) {
        this.mContext = mContext;
        this.mUpdateGroupView = mUpdateGroupView;
        this.mUpdateGroupResponsitory = new UpdateGroupResponsitotyIml();
    }

    public void updateGroup(GroupResponseDTO groupResponseDTO){
        mUpdateGroupResponsitory.updateGroup(mContext, groupResponseDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mUpdateGroupView.updateGroupSuccess("Update success");
            }

            @Override
            public void onFail(String message) {
                mUpdateGroupView.updateGroupFail(message);
            }
        });
    }
}
