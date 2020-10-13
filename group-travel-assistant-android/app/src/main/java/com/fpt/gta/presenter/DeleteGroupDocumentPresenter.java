package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeleteGroupDocumentRepository;
import com.fpt.gta.repository.DeleteGroupDocumentRepositoryIml;
import com.fpt.gta.repository.DeleteTripRepository;
import com.fpt.gta.view.DeleteGroupDocumentView;
import com.fpt.gta.view.DeleteTripView;
import com.fpt.gta.webService.CallBackData;

public class DeleteGroupDocumentPresenter {
    private Context mContext;
    private DeleteGroupDocumentView mDeleteGroupDocumentView;
    private DeleteGroupDocumentRepository mDeleteGroupDocumentRepository;

    public DeleteGroupDocumentPresenter(Context mContext, DeleteGroupDocumentView mDeleteGroupDocumentView) {
        this.mContext = mContext;
        this.mDeleteGroupDocumentView = mDeleteGroupDocumentView;
        this.mDeleteGroupDocumentRepository = new DeleteGroupDocumentRepositoryIml();
    }

    public void deleteGroupDocument(Integer idDocument){
        mDeleteGroupDocumentRepository.deleteGroupDocument(mContext, idDocument, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mDeleteGroupDocumentView.deleteGroupDocumentSuccess("Delete Group Document Success");
            }

            @Override
            public void onFail(String message) {
                mDeleteGroupDocumentView.deleteGroupDocumentFail(message);
            }
        });
    }
}
