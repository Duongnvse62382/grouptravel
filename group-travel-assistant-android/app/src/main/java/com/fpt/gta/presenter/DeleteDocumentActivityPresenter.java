package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteDocumentActivityRepository;
import com.fpt.gta.repository.DeleteDocumentActivityRepositoryIml;
import com.fpt.gta.repository.DeleteTransactionDocumentRepository;
import com.fpt.gta.repository.DeleteTransactionDocumentRepositoryIml;
import com.fpt.gta.view.DeleteDocumentActivityView;
import com.fpt.gta.view.DeleteTransactionDocumentView;
import com.fpt.gta.webService.CallBackData;

public class DeleteDocumentActivityPresenter {
    private Context mContext;
    private DeleteDocumentActivityView mDeleteDocumentActivityView;
    private DeleteDocumentActivityRepository mDeleteDocumentActivityRepository;

    public DeleteDocumentActivityPresenter(Context mContext, DeleteDocumentActivityView mDeleteDocumentActivityView) {
        this.mContext = mContext;
        this.mDeleteDocumentActivityView = mDeleteDocumentActivityView;
        this.mDeleteDocumentActivityRepository = new DeleteDocumentActivityRepositoryIml();
    }

    public void deleteDocumentActivity(int idTransaction){
        mDeleteDocumentActivityRepository.deleteDocumentActivity(mContext, idTransaction, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteDocumentActivityView.deleteDocumentActivitySuccess("Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteDocumentActivityView.deleteDocumentActivityFail(message);
            }
        });
    }
}
