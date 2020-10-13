package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.repository.CreateGroupReponsitory;
import com.fpt.gta.repository.CreateGroupReponsitoryIml;
import com.fpt.gta.view.CreateGroupView;

public class CreateGroupPresenter {
    private Context mContext;
    private CreateGroupView mCreateGroupView;
    private CreateGroupReponsitory mCreateGroupReponsitory;

    public CreateGroupPresenter(Context mContext, CreateGroupView mCreateGroupView) {
        this.mContext = mContext;
        this.mCreateGroupView = mCreateGroupView;
        this.mCreateGroupReponsitory = new CreateGroupReponsitoryIml();
    }

    public void createGroup(GroupResponseDTO groupResponseDTO) {
        mCreateGroupReponsitory.createGroup(mContext, groupResponseDTO, new CallBackData<GroupResponseDTO>() {
            @Override
            public void onSuccess(GroupResponseDTO groupResponseDTO) {
                mCreateGroupView.createGroupSuccess(groupResponseDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mCreateGroupView.CreateGroupFail(message);
            }
        });
    }
}
