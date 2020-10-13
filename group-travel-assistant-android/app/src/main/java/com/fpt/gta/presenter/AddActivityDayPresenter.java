package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.AddActivityDayRepository;
import com.fpt.gta.repository.AddActivityDayRepositoryIml;
import com.fpt.gta.repository.CreateGroupReponsitory;
import com.fpt.gta.repository.CreateGroupReponsitoryIml;
import com.fpt.gta.view.AddActivityDayView;
import com.fpt.gta.view.CreateGroupView;
import com.fpt.gta.webService.CallBackData;

import javax.security.auth.callback.Callback;

public class AddActivityDayPresenter {
    private Context mContext;
    private AddActivityDayView mAddActivityDayView;
    private AddActivityDayRepository mAddActivityDayRepository;

    public AddActivityDayPresenter(Context mContext, AddActivityDayView mAddActivityDayView) {
        this.mContext = mContext;
        this.mAddActivityDayView = mAddActivityDayView;
        this.mAddActivityDayRepository = new AddActivityDayRepositoryIml ();
    }

    public void AddActivity(int id, ActivityDTO activityDTO){
        mAddActivityDayRepository.AddActivityDay ( mContext, id, activityDTO, new CallBackData<String> () {
            @Override
            public void onSuccess(String s) {
                mAddActivityDayView.AddActivitySuccess ( "Add Successfully" );
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mAddActivityDayView.AddActivityFail (message);
            }
        } );
    }


}
