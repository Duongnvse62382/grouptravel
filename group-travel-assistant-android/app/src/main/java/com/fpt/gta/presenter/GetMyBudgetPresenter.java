package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.IndividualBudgetDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.repository.GetMyBudgetRepository;
import com.fpt.gta.repository.GetMyBudgetRepositoryIml;
import com.fpt.gta.view.GetMyBudgetView;
import com.fpt.gta.webService.CallBackData;

public class GetMyBudgetPresenter {
    private Context mContext;
    private GetMyBudgetRepository mGetMyBudgetRepository;
    private GetMyBudgetView mGetMyBudgetView;

    public GetMyBudgetPresenter(Context mContext, GetMyBudgetView mGetMyBudgetView) {
        this.mContext = mContext;
        this.mGetMyBudgetView = mGetMyBudgetView;
        this.mGetMyBudgetRepository = new GetMyBudgetRepositoryIml();
    }

    public void GetMyBudgetPresenter(Integer idGroup) {
        mGetMyBudgetRepository.getMyBudget(mContext, idGroup, new CallBackData<MemberDTO>() {
            @Override
            public void onSuccess(MemberDTO memberDTO) {
                mGetMyBudgetView.getMyBudgetSuccess(memberDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mGetMyBudgetView.getMyBudgetFail("Fail");
            }
        });
    }
}
