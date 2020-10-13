package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.repository.GetSuggestedActivityRepository;
import com.fpt.gta.repository.GetSuggestedActivityRepositoryIml;
import com.fpt.gta.view.PrintSuggestedActivityView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintSuggestedActivityPresenter {
    private Context mContext;
    private PrintSuggestedActivityView mPrintSuggestedActivityView;
    private GetSuggestedActivityRepository mGetSuggestedActivityRepository;

    public PrintSuggestedActivityPresenter(Context mContext, PrintSuggestedActivityView mPrintSuggestedActivityView) {
        this.mContext = mContext;
        this.mPrintSuggestedActivityView = mPrintSuggestedActivityView;
        mGetSuggestedActivityRepository = new GetSuggestedActivityRepositoryIml();
    }

    public void getSuggestedActivity(Integer idTrip){
        mGetSuggestedActivityRepository.printSuggestedActivity(mContext, idTrip, new CallBackData<List<SuggestedActivityResponseDTO>>() {
            @Override
            public void onSuccess(List<SuggestedActivityResponseDTO> suggestedActivityResponseDTOList) {
                mPrintSuggestedActivityView.getSuggestedSuccess(suggestedActivityResponseDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintSuggestedActivityView.getSuggestedFail(message);

            }
        });
    }
}
