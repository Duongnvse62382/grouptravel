package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.repository.PrinTripResponsitoryIml;
import com.fpt.gta.repository.PrintBudgetElectedPlanRepository;
import com.fpt.gta.repository.PrintBudgetElectedPlanRepositoryIml;
import com.fpt.gta.repository.PrintTripResponsitory;
import com.fpt.gta.view.BudgetElectedPlanView;
import com.fpt.gta.view.TripOverviewView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintAllBudgetElectedPlanPresenter {
    private Context mContext;
    private BudgetElectedPlanView mView;
    private PrintBudgetElectedPlanRepository mPrintBudgetElectedPlanRepository;

    public PrintAllBudgetElectedPlanPresenter(Context mContext, BudgetElectedPlanView mView) {
        this.mContext = mContext;
        this.mView = mView;
        this.mPrintBudgetElectedPlanRepository = new PrintBudgetElectedPlanRepositoryIml();
    }

    public void getAllTripByGroupId(int groupId) {
        mPrintBudgetElectedPlanRepository.printBudgetElectedPlan(mContext, groupId, new CallBackData<List<TripReponseDTO>>() {
            @Override
            public void onSuccess(List<TripReponseDTO> tripReponseDTOList) {
                mView.onSucessFul(tripReponseDTOList);
            }

            @Override
            public void onSuccessString(String mess) {
            }

            @Override
            public void onFail(String message) {
                mView.onFail(message);
            }
        });
    }
}
