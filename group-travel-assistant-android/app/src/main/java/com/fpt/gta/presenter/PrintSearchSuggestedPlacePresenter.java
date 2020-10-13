package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.repository.PrintSearchSuggestedPlaceRepository;
import com.fpt.gta.repository.PrintSearchSuggestedPlaceRepositoryIml;
import com.fpt.gta.view.PrintSearchSuggestedPlaceView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintSearchSuggestedPlacePresenter {
    private Context mContext;
    private PrintSearchSuggestedPlaceView mPrintSearchSuggestedPlaceView;
    private PrintSearchSuggestedPlaceRepository mPrintSearchSuggestedPlaceRepository;

    public PrintSearchSuggestedPlacePresenter(Context mContext, PrintSearchSuggestedPlaceView mPrintSearchSuggestedPlaceView) {
        this.mContext = mContext;
        this.mPrintSearchSuggestedPlaceView = mPrintSearchSuggestedPlaceView;
        this.mPrintSearchSuggestedPlaceRepository = new PrintSearchSuggestedPlaceRepositoryIml();
    }

    public void getSearchSuggestedPlaceView(Integer idTrip){
        mPrintSearchSuggestedPlaceRepository.getSearchSuggestedPlace(mContext, idTrip, new CallBackData<List<PlaceDTO>>() {
            @Override
            public void onSuccess(List<PlaceDTO> placeDTOList) {
                mPrintSearchSuggestedPlaceView.getSearchSuggestedPlaceSS(placeDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintSearchSuggestedPlaceView.getSearchSuggestedPlaceFail(message);
            }
        });
    }
}
