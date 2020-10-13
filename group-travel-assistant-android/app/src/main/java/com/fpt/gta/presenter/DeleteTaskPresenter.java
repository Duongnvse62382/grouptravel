package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteTaskRepository;
import com.fpt.gta.repository.DeleteTaskRepositoryIml;
import com.fpt.gta.view.DeleteTaskView;
import com.fpt.gta.webService.CallBackData;

public class DeleteTaskPresenter {
    private Context mContext;
    private DeleteTaskView mDeleteTaskView;
    private DeleteTaskRepository mDeleteTaskRepository;

    public DeleteTaskPresenter(Context mContext, DeleteTaskView mDeleteTaskView) {
        this.mContext = mContext;
        this.mDeleteTaskView = mDeleteTaskView;
        this.mDeleteTaskRepository = new DeleteTaskRepositoryIml();
    }

    public void deleteTask(Integer idTask){
        mDeleteTaskRepository.deleteTask(mContext, idTask, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteTaskView.deleteTaskSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mDeleteTaskView.deleteTaskFail(message);
            }
        });
    }

}
