package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteTripRepository;
import com.fpt.gta.repository.DeleteTripRepositoryIml;
import com.fpt.gta.view.DeleteTripView;
import com.fpt.gta.webService.CallBackData;

public class DeleteTripPresenter {
    private Context mMontext;
    private DeleteTripView mDeleteTripView;
    private DeleteTripRepository mDeleteTripRepository;

    public DeleteTripPresenter(Context mMontext, DeleteTripView mDeleteTripView) {
        this.mMontext = mMontext;
        this.mDeleteTripView = mDeleteTripView;
        this.mDeleteTripRepository = new DeleteTripRepositoryIml();
    }

    public void deleteTrip(int tripid){
        mDeleteTripRepository.deleteTrip(mMontext, tripid, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteTripView.deleteTripSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteTripView.deleteTripFail(message);
            }
        });
    }
}
