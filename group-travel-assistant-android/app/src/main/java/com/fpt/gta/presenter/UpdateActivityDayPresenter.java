package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.repository.UpdateActivityRepository;
import com.fpt.gta.repository.UpdateActivityRepositoryIml;
import com.fpt.gta.repository.UpdateSuggestedActivityRepository;
import com.fpt.gta.view.UpdateActivityView;
import com.fpt.gta.webService.CallBackData;

public class UpdateActivityDayPresenter {
    private Context mContext;
    private UpdateActivityView mUpdateActivityView;
    private UpdateActivityRepository updateActivityRepository;

    public UpdateActivityDayPresenter(Context mContext, UpdateActivityView mUpdateActivityView) {
        this.mContext = mContext;
        this.mUpdateActivityView = mUpdateActivityView;
        this.updateActivityRepository = new UpdateActivityRepositoryIml();
    }

    public void UpdateActivity(int idPlan, ActivityDTO activityDTO){
        updateActivityRepository.UpdateActivityDay(mContext, idPlan, activityDTO, new CallBackData<String>() {
            @Override
            public void onSuccess(String s) {
                mUpdateActivityView.updateActivitySuccess("Update Successfully");
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mUpdateActivityView.updateActivityFail(message);
            }
        });
    }
}
