package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.IndividualBudgetDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.repository.UpdateGroupBudgetRepository;
import com.fpt.gta.repository.UpdateGroupBudgetRepositoryIml;
import com.fpt.gta.repository.UpdateMyBudgetRepository;
import com.fpt.gta.repository.UpdateMyBudgetRepositoryIml;
import com.fpt.gta.view.IndividualBudgetInGroupView;
import com.fpt.gta.view.UpdateMyBudgetView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class UpdateMyBudgetPresenter {
    private Context mContext;
    private UpdateMyBudgetRepository updateMyBudgetRepository;
    private UpdateMyBudgetView myBudgetView;

    public UpdateMyBudgetPresenter(Context mContext, UpdateMyBudgetView myBudgetView) {
        this.mContext = mContext;
        this.myBudgetView = myBudgetView;
        this.updateMyBudgetRepository = new UpdateMyBudgetRepositoryIml();
    }

    public void updateMyBudgetPresenter(Integer idGroup, MemberDTO memberDTO) {
        updateMyBudgetRepository.updateMyBudget(mContext, idGroup, memberDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {
                myBudgetView.updateMyBudgetSuccessfully("Successfully");
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                myBudgetView.updateMyBudgetFail("Fail");
            }
        });
    }
}
