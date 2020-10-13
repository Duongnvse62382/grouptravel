package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.PrintGroupReponsitory;
import com.fpt.gta.repository.PrintGroupReponsitotyIml;
import com.fpt.gta.view.PrintAllGroupView;

import java.util.List;

public class PrintAllGroupPresenter {
    private Context mContext;
    private PrintAllGroupView mPrintAllGroupView;
    private PrintGroupReponsitory mPrintGroupReponsitory;

    public PrintAllGroupPresenter(Context mContext, PrintAllGroupView mPrintAllGroupView) {
        this.mContext = mContext;
        this.mPrintAllGroupView = mPrintAllGroupView;
        this.mPrintGroupReponsitory = new PrintGroupReponsitotyIml();
    }

    public void printAllGroup(){
        mPrintGroupReponsitory.printGroup(mContext,new CallBackData<List<GroupResponseDTO>>() {
            @Override
            public void onSuccess(List<GroupResponseDTO> groupResponseDTOList) {
                mPrintAllGroupView.printAllGroupSuccess(groupResponseDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintAllGroupView.printAllGroupFail(message);
            }
        });
    }
}
