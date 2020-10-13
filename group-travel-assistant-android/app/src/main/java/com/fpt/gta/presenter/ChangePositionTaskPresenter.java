package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.ChangePositionTaskRepository;
import com.fpt.gta.repository.ChangePositionTaskRepositoryIml;
import com.fpt.gta.view.ChangePositionTaskView;
import com.fpt.gta.webService.CallBackData;

public class ChangePositionTaskPresenter {
    private Context mContext;
    private ChangePositionTaskView mChangePositionTaskView;
    private ChangePositionTaskRepository mChangePositionTaskRepository;

    public ChangePositionTaskPresenter(Context mContext, ChangePositionTaskView mChangePositionTaskView) {
        this.mContext = mContext;
        this.mChangePositionTaskView = mChangePositionTaskView;
        this.mChangePositionTaskRepository = new ChangePositionTaskRepositoryIml();
    }

    public void changePositionTask(Integer idTask, Integer order){
        mChangePositionTaskRepository.changePositionTask(mContext, idTask, order, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mChangePositionTaskView.changePositionTaskSS(mess);
            }

            @Override
            public void onFail(String message) {
                mChangePositionTaskView.changePositionFail(message);
            }
        });
    }
}
