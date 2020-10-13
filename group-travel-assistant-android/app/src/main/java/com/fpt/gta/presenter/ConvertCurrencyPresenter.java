package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.ConvertCurrencyRepository;
import com.fpt.gta.repository.ConvertCurrencyRepositoryIml;
import com.fpt.gta.repository.SearchPlaceRepository;
import com.fpt.gta.view.ConvertCurrencyView;
import com.fpt.gta.view.SearchPlaceView;
import com.fpt.gta.webService.CallBackData;

import java.math.BigDecimal;

public class ConvertCurrencyPresenter {
    private ConvertCurrencyView mConvertCurrencyView;
    private ConvertCurrencyRepository mConvertCurrencyRepository;
    private Context mContext;

    public ConvertCurrencyPresenter(ConvertCurrencyView mConvertCurrencyView, Context mContext) {
        this.mConvertCurrencyView = mConvertCurrencyView;
        this.mContext = mContext;
        this.mConvertCurrencyRepository = new ConvertCurrencyRepositoryIml();
    }

    public void convertCurrencyMoney(String firstCurrencyCode, String secondCurrencyCode){
        mConvertCurrencyRepository.convertCurrency(mContext, firstCurrencyCode, secondCurrencyCode, new CallBackData<BigDecimal>() {
            @Override
            public void onSuccess(BigDecimal bigDecimal) {
                mConvertCurrencyView.convertCurrencySuccess(bigDecimal);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
            mConvertCurrencyView.convertCurrencyFail(message);
            }
        });
    }

}
