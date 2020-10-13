package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.repository.UpdateTransactionRepository;
import com.fpt.gta.repository.UpdateTransactionRepositoryIml;
import com.fpt.gta.view.UpdateTransactionView;
import com.fpt.gta.webService.CallBackData;

public class UpdateTransacionPresenter {
    private UpdateTransactionView mUpdateTransactionView;
    private UpdateTransactionRepository mUpdateTransactionRepository;
    private Context mContext;

    public UpdateTransacionPresenter(UpdateTransactionView mUpdateTransactionView, Context mContext) {
        this.mUpdateTransactionView = mUpdateTransactionView;
        this.mContext = mContext;
        this.mUpdateTransactionRepository = new UpdateTransactionRepositoryIml();
    }

    public void updateTransaction(TransactionDTO transactionDTO){
        mUpdateTransactionRepository.updateTransaction(mContext, transactionDTO, new CallBackData<String>() {
            @Override
            public void onSuccess(String s) {
                mUpdateTransactionView.updateTransactionSuccess("Success");
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mUpdateTransactionView.updateTransactionFail(message);
            }
        });
    }
}
