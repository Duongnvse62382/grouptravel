package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.PrintAllCurrencyRepository;
import com.fpt.gta.repository.PrintAllCurrencyRepositoryIml;
import com.fpt.gta.view.PrintAllCurrencyView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintAllCurrencyPresenter {
    private Context mContext;
    private PrintAllCurrencyRepository mPrintAllCurrencyRepository;
    private PrintAllCurrencyView mPrintAllCurrencyView;

    public PrintAllCurrencyPresenter(Context mContext, PrintAllCurrencyView mPrintAllCurrencyView) {
        this.mContext = mContext;
        this.mPrintAllCurrencyView = mPrintAllCurrencyView;
        this.mPrintAllCurrencyRepository = new PrintAllCurrencyRepositoryIml();
    }

    public void printAllCurrency(){
        mPrintAllCurrencyRepository.printAllCurrency(mContext,new CallBackData<List<CurrencyDTO>>() {
            @Override
            public void onSuccess(List<CurrencyDTO> currencyDTOList) {
                mPrintAllCurrencyView.printAllCurrencySuccess(currencyDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintAllCurrencyView.printAllCurrencyFail(message);
            }
        });
    }}
