package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.repository.PrintAllTransactionRepository;
import com.fpt.gta.repository.PrintAllTransactionRepositoryIml;
import com.fpt.gta.view.PrintAllTransactionView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintAllTransactionPresenter {
    private Context mContext;
    private PrintAllTransactionView mPrintAllTransactionView;
    private PrintAllTransactionRepository mPrintAllTransactionRepository;

    public PrintAllTransactionPresenter(Context mContext, PrintAllTransactionView mPrintAllTransactionView) {
        this.mContext = mContext;
        this.mPrintAllTransactionView = mPrintAllTransactionView;
        this.mPrintAllTransactionRepository = new PrintAllTransactionRepositoryIml();
    }

    public void printAllTransaction(int idGroup){
        mPrintAllTransactionRepository.printAllTransactionRepository(mContext, idGroup, new CallBackData<List<TransactionDTO>>() {
            @Override
            public void onSuccess(List<TransactionDTO> transactionDTOS) {
                mPrintAllTransactionView.printAllTransactionSuccess(transactionDTOS);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
            mPrintAllTransactionView.printAllTransactionFail(message);
            }
        });
    }
}
