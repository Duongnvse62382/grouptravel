package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.repository.GetorCrawlRepository;
import com.fpt.gta.repository.GetorCrawlRepositoryIml;
import com.fpt.gta.view.GetorCrawlView;
import com.fpt.gta.webService.CallBackData;


public class GetorCrawlPresenter {
    private Context mContext;
    private GetorCrawlRepository mGetorCrawlRepository;
    private GetorCrawlView mGetorCrawlView;

    public GetorCrawlPresenter(Context mContext, GetorCrawlView mGetorCrawlView) {
        this.mContext = mContext;
        this.mGetorCrawlView = mGetorCrawlView;
        this.mGetorCrawlRepository = new GetorCrawlRepositoryIml();
    }


    public void getorCrawl(String googlePlaceId, Integer idTrip){
        mGetorCrawlRepository.getOrCrawl(mContext, googlePlaceId, idTrip,new CallBackData<PlaceDTO>() {
            @Override
            public void onSuccess(PlaceDTO placeDTO) {
                mGetorCrawlView.crawlSuccess(placeDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mGetorCrawlView.crawlFail(message);
            }
        });
    }

}
