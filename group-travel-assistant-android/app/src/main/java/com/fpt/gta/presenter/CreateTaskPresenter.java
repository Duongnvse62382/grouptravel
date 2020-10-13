package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.repository.CreateTaskRepository;
import com.fpt.gta.repository.CreateTaskRepositoryIml;
import com.fpt.gta.view.CreateTaskView;
import com.fpt.gta.webService.CallBackData;

public class CreateTaskPresenter {
    private Context mContext;
    private CreateTaskView mCreateTaskView;
    private CreateTaskRepository mCreateTaskRepository;

    public CreateTaskPresenter(Context mContext, CreateTaskView mCreateTaskView) {
        this.mContext = mContext;
        this.mCreateTaskView = mCreateTaskView;
        this.mCreateTaskRepository = new CreateTaskRepositoryIml();
    }

    public void createTask(Integer idActivity, TaskDTO taskDTO){
        mCreateTaskRepository.createTask(mContext, idActivity, taskDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mCreateTaskView.createTaskSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mCreateTaskView.createTaskFail(message);
            }
        });
    }
}
