package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.SetBudgetPlanRepository;
import com.fpt.gta.repository.SetBudgetPlanRepositoryIml;
import com.fpt.gta.repository.UpdateGroupResponsitory;
import com.fpt.gta.repository.UpdateGroupResponsitotyIml;
import com.fpt.gta.view.SetBudgetPlanView;
import com.fpt.gta.view.UpdateGroupView;
import com.fpt.gta.webService.CallBackData;

import java.math.BigDecimal;

public class SetBudgetPlanPresenter {
    private Context mContext;
    private SetBudgetPlanRepository mSetBudgetPlanRepository;
    private SetBudgetPlanView mSetBudgetPlanView;

    public SetBudgetPlanPresenter(Context mContext, SetBudgetPlanView mSetBudgetPlanView) {
        this.mContext = mContext;
        this.mSetBudgetPlanView = mSetBudgetPlanView;
        this.mSetBudgetPlanRepository = new SetBudgetPlanRepositoryIml();
    }

    public void setBudgetPlan(Integer idPlan, BigDecimal activityBudget, BigDecimal accommodationBudget, BigDecimal transportationBudget, BigDecimal foodBudget) {
        mSetBudgetPlanRepository.setBudgetPlan(mContext, idPlan, activityBudget, accommodationBudget, transportationBudget, foodBudget, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mSetBudgetPlanView.setBudgetPlanSuccess("Set Budget Plan Successfully");
            }

            @Override
            public void onFail(String message) {
                mSetBudgetPlanView.setBudgetPlanFail("Set Budget Plan Fail");
            }
        });
    }
}
