package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteTransactionDocumentRepository;
import com.fpt.gta.repository.DeleteTransactionDocumentRepositoryIml;
import com.fpt.gta.repository.DeleteTransactionRepository;
import com.fpt.gta.repository.DeleteTransactionRepositoryIml;
import com.fpt.gta.view.DeleteTransactionDocumentView;
import com.fpt.gta.view.DeleteTransactionView;
import com.fpt.gta.webService.CallBackData;

public class DeleteTransactionDocumentPresenter {
    private Context mContext;
    private DeleteTransactionDocumentView mDeleteTransactionDocumentView;
    private DeleteTransactionDocumentRepository mDeleteTransactionDocumentRepository;

    public DeleteTransactionDocumentPresenter(Context mContext, DeleteTransactionDocumentView mDeleteTransactionDocumentView) {
        this.mContext = mContext;
        this.mDeleteTransactionDocumentView = mDeleteTransactionDocumentView;
        this.mDeleteTransactionDocumentRepository = new DeleteTransactionDocumentRepositoryIml();
    }

    public void deleteTransactionDocument(int idTransaction){
        mDeleteTransactionDocumentRepository.deleteTransactionDocument(mContext, idTransaction, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteTransactionDocumentView.deleteTransactionDocumentSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteTransactionDocumentView.deleteTransactionDocumentFail(message);
            }
        });
    }
}
