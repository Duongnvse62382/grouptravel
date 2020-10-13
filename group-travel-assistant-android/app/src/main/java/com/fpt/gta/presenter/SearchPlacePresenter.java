package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.repository.SearchPlaceRepository;
import com.fpt.gta.repository.SearchPlaceRepositoryIml;
import com.fpt.gta.view.SearchPlaceView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class SearchPlacePresenter {
    private Context mContext;
    private SearchPlaceView mSearchPlaceView;
    private SearchPlaceRepository mSearchPlaceRepository;

    public SearchPlacePresenter(Context mContext, SearchPlaceView mSearchPlaceView) {
        this.mContext = mContext;
        this.mSearchPlaceView = mSearchPlaceView;
        this.mSearchPlaceRepository = new SearchPlaceRepositoryIml();
    }

    public void searchPlace(String srearchValues){
        mSearchPlaceRepository.searchPlace(mContext, srearchValues, new CallBackData<List<PlaceDTO>>() {
            @Override
            public void onSuccess(List<PlaceDTO> placeDTOList) {
                mSearchPlaceView.searchPlaceSS(placeDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mSearchPlaceView.searchPlaceFail(message);
            }
        });
    }
}
