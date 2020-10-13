package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.repository.SearchPlaceCitiesRepository;
import com.fpt.gta.repository.SearchPlaceCitiesRepositoryIml;
import com.fpt.gta.view.SearchPlaceCitiesView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class SearchPlaceCitiesPresenter {
    private Context mContext;
    private SearchPlaceCitiesView mSearchPlaceCitiesView;
    private SearchPlaceCitiesRepository mSearchPlaceCitiesRepository;

    public SearchPlaceCitiesPresenter(Context mContext, SearchPlaceCitiesView mSearchPlaceCitiesView) {
        this.mContext = mContext;
        this.mSearchPlaceCitiesView = mSearchPlaceCitiesView;
        this.mSearchPlaceCitiesRepository = new SearchPlaceCitiesRepositoryIml();
    }

    public void searchPlaceCities(String srearchValues){
        mSearchPlaceCitiesRepository.searchPlaceCities(mContext, srearchValues, new CallBackData<List<PlaceDTO>>() {
            @Override
            public void onSuccess(List<PlaceDTO> placeDTOList) {
                mSearchPlaceCitiesView.searchPlaceCitiesSS(placeDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mSearchPlaceCitiesView.searchPlaceCitiesFail(message);
            }
        });
    }
}
