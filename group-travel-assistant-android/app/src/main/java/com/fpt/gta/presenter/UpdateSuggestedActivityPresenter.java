package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.repository.UpdateSuggestedActivityRepository;
import com.fpt.gta.repository.UpdateSuggestedActivityRepositoryIml;
import com.fpt.gta.view.UpdateSuggestedActivityView;
import com.fpt.gta.webService.CallBackData;

public class UpdateSuggestedActivityPresenter {
    private Context mContext;
    private UpdateSuggestedActivityView umUpdateSuggestedActivityView;
    private UpdateSuggestedActivityRepository mUpdateSuggestedActivityRepository;

    public UpdateSuggestedActivityPresenter(Context mContext, UpdateSuggestedActivityView umUpdateSuggestedActivityView) {
        this.mContext = mContext;
        this.umUpdateSuggestedActivityView = umUpdateSuggestedActivityView;
        mUpdateSuggestedActivityRepository = new UpdateSuggestedActivityRepositoryIml();
    }

    public void updateSuggestedActivity(Integer idTrip, SuggestedActivityResponseDTO suggestedActivityResponseDTO){
        mUpdateSuggestedActivityRepository.updateSuggestedActivity(mContext, idTrip, suggestedActivityResponseDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {
            }

            @Override
            public void onSuccessString(String mess) {
                umUpdateSuggestedActivityView.updateSuggestedSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                umUpdateSuggestedActivityView.updateSuggestedFail(message);
            }
        });
    }
}
