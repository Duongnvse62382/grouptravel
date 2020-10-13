package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.repository.CreateSuggestedActivityRepository;
import com.fpt.gta.repository.CreateSuggestedActivityRepositoryIml;
import com.fpt.gta.view.CreateSuggestedActivityView;
import com.fpt.gta.webService.CallBackData;

public class CreateSuggestedActivityPresenter {
    private Context mContext;
    private CreateSuggestedActivityView mCreateSuggestedActivityView;
    private CreateSuggestedActivityRepository mCreateSuggestedActivityRepository;

    public CreateSuggestedActivityPresenter(Context mContext, CreateSuggestedActivityView mCreateSuggestedActivityView) {
        this.mContext = mContext;
        this.mCreateSuggestedActivityView = mCreateSuggestedActivityView;
        mCreateSuggestedActivityRepository = new CreateSuggestedActivityRepositoryIml();
    }

    public void createSuggestActivity(Integer idTrip, SuggestedActivityResponseDTO suggestedActivityResponseDTO){
        mCreateSuggestedActivityRepository.createSuggestedActivity(mContext, idTrip, suggestedActivityResponseDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mCreateSuggestedActivityView.createSuggestSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mCreateSuggestedActivityView.createSuggestFail(message);

            }
        });
    }
}
