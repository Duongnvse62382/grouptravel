package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.repository.AddTransactionRepository;
import com.fpt.gta.repository.AddTransactionRepositoryIml;
import com.fpt.gta.view.AddTransactionView;
import com.fpt.gta.webService.CallBackData;

public class AddTransactionPresenter {
    private Context mContext;
    private AddTransactionRepository addTransactionRepository;
    private AddTransactionView addTransactionView;

    public AddTransactionPresenter(Context mContext, AddTransactionView addTransactionView) {
        this.mContext = mContext;
        this.addTransactionView = addTransactionView;
        this.addTransactionRepository = new AddTransactionRepositoryIml();
    }

    public void createTransaction(int groupId, TransactionDTO transactionDTO){
        addTransactionRepository.createTransaction(mContext, groupId, transactionDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                addTransactionView.AddTransactionSuccess("success");
            }

            @Override
            public void onFail(String message) {
                addTransactionView.AddTransactionFail(message);
            }
        });

    }
}
