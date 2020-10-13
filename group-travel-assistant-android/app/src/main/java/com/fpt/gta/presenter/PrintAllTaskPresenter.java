package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.repository.PrintAllTaskRepository;
import com.fpt.gta.repository.PrintAllTaskRepositoryIml;
import com.fpt.gta.view.PrintAllTaskView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintAllTaskPresenter {
    private Context mContext;
    private PrintAllTaskView mPrintAllTaskView;
    private PrintAllTaskRepository mPrintAllTaskRepository;

    public PrintAllTaskPresenter(Context mContext, PrintAllTaskView mPrintAllTaskView) {
        this.mContext = mContext;
        this.mPrintAllTaskView = mPrintAllTaskView;
        this.mPrintAllTaskRepository = new PrintAllTaskRepositoryIml();
    }

    public void printAllTask(Integer idActivity){
        mPrintAllTaskRepository.printAllTask(mContext, idActivity, new CallBackData<List<TaskDTO>>() {
            @Override
            public void onSuccess(List<TaskDTO> taskDTOList) {
                mPrintAllTaskView.printAllTaskSuccess(taskDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintAllTaskView.printAllTaskFail(message);
            }
        });
    }
}
