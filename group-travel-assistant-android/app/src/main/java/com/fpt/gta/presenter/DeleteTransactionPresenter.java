package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteTransactionRepository;
import com.fpt.gta.repository.DeleteTransactionRepositoryIml;
import com.fpt.gta.view.DeleteTransactionView;
import com.fpt.gta.webService.CallBackData;

public class DeleteTransactionPresenter {
    private Context mContext;
    private DeleteTransactionView mDeleteTransactionView;
    private DeleteTransactionRepository mDeleteTransactionRepository;

    public DeleteTransactionPresenter(Context mContext, DeleteTransactionView mDeleteTransactionView) {
        this.mContext = mContext;
        this.mDeleteTransactionView = mDeleteTransactionView;
        this.mDeleteTransactionRepository = new DeleteTransactionRepositoryIml();
    }

    public void deleteTransaction(int idTransaction){
        mDeleteTransactionRepository.deleteTransaction(mContext, idTransaction, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteTransactionView.deleteTransactionSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteTransactionView.deleteTransactionFail(message);
                }
        });
    }
}
