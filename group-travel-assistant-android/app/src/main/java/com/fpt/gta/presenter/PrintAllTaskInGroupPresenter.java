package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.repository.PrintAllTaskInGroupRepository;
import com.fpt.gta.repository.PrintAllTaskInGroupRepositoryIml;
import com.fpt.gta.view.PrintAllTaskInGroupView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintAllTaskInGroupPresenter {
    private Context mContext;
    private PrintAllTaskInGroupRepository mPrintAllTaskInGroupRepository;
    private PrintAllTaskInGroupView mPrintAllTaskInGroupView;

    public PrintAllTaskInGroupPresenter(Context mContext, PrintAllTaskInGroupView mPrintAllTaskInGroupView) {
        this.mContext = mContext;
        this.mPrintAllTaskInGroupView = mPrintAllTaskInGroupView;
        this.mPrintAllTaskInGroupRepository = new PrintAllTaskInGroupRepositoryIml();
    }

    public void printAllTaskInGroup(Integer idGroup){
        mPrintAllTaskInGroupRepository.printAllTaskInGroup(mContext, idGroup, new CallBackData<List<TaskDTO>>() {
            @Override
            public void onSuccess(List<TaskDTO> taskDTOList) {
                mPrintAllTaskInGroupView.printAllTaskInGroupSuccess(taskDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintAllTaskInGroupView.printAllTaskInGroupFail(message);
            }
        });
    }
}
