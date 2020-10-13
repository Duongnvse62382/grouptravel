package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.repository.PrintActivityByIdRepository;
import com.fpt.gta.repository.PrintActivityByIdRepositoryIml;
import com.fpt.gta.view.PrintActivityByIdView;
import com.fpt.gta.webService.CallBackData;

public class PrintActivityByIdPresenter {
    private Context mContext;
    private PrintActivityByIdView mPrintActivityByIdView;
    private PrintActivityByIdRepository mPrintActivityByIdRepository;

    public PrintActivityByIdPresenter(Context mContext, PrintActivityByIdView mPrintActivityByIdView) {
        this.mContext = mContext;
        this.mPrintActivityByIdView = mPrintActivityByIdView;
        this.mPrintActivityByIdRepository = new PrintActivityByIdRepositoryIml();
    }

    public void getActivityById(Integer idActivity){
        this.mPrintActivityByIdRepository.getActivityById(mContext, idActivity, new CallBackData<ActivityDTO>() {
            @Override
            public void onSuccess(ActivityDTO activityDTO) {
                mPrintActivityByIdView.printActivityById(activityDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintActivityByIdView.printActivityByIdFail(message);
            }
        });
    }
}
