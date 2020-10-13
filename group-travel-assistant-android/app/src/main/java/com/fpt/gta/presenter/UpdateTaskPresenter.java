package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.repository.UpdateTaskRepository;
import com.fpt.gta.repository.UpdateTaskRepositoryIml;
import com.fpt.gta.view.UpdateTaskview;
import com.fpt.gta.webService.CallBackData;

public class UpdateTaskPresenter {
    private Context mContext;
    private UpdateTaskview mUpdateTaskview;
    private UpdateTaskRepository mUpdateTaskRepository;

    public UpdateTaskPresenter(Context mContext, UpdateTaskview mUpdateTaskview) {
        this.mContext = mContext;
        this.mUpdateTaskview = mUpdateTaskview;
        this.mUpdateTaskRepository = new UpdateTaskRepositoryIml();
    }

    public void updateTask(TaskDTO taskDTO){
        mUpdateTaskRepository.updateTask(mContext, taskDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mUpdateTaskview.updateTaskSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mUpdateTaskview.updateTaskFail(message);
            }
        });
    }
}
