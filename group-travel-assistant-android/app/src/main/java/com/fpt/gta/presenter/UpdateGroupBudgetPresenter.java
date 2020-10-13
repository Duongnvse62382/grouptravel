package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.repository.UpdateGroupBudgetRepository;
import com.fpt.gta.repository.UpdateGroupBudgetRepositoryIml;
import com.fpt.gta.repository.UpdateMyBudgetRepository;
import com.fpt.gta.repository.UpdateMyBudgetRepositoryIml;
import com.fpt.gta.view.UpdateGroupBudgetView;
import com.fpt.gta.view.UpdateMyBudgetView;
import com.fpt.gta.webService.CallBackData;

public class UpdateGroupBudgetPresenter {
    private Context mContext;
    private UpdateGroupBudgetRepository updateGroupBudgetRepository;
    private UpdateGroupBudgetView updateGroupBudgetView;

    public UpdateGroupBudgetPresenter(Context mContext, UpdateGroupBudgetView updateGroupBudgetView) {
        this.mContext = mContext;
        this.updateGroupBudgetView = updateGroupBudgetView;
        this.updateGroupBudgetRepository = new UpdateGroupBudgetRepositoryIml();
    }

    public void updateGroupBudgetPresenter(Integer idGroup, GroupResponseDTO groupResponseDTO) {
        updateGroupBudgetRepository.updateGroupBudget(mContext, idGroup, groupResponseDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {
                updateGroupBudgetView.updateGroupBudgetSuccess("Successfully");
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                updateGroupBudgetView.updateGroupBudgetFail("Fail");
            }
        });
    }
}
