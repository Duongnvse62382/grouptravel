package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.repository.CreateGroupDocumentRepository;
import com.fpt.gta.repository.CreateGroupDocumentRepositoryIml;
import com.fpt.gta.view.CreateGroupDocumentView;
import com.fpt.gta.webService.CallBackData;

public class CreateGroupDocumentPresenter {
    private Context mContext;
    private CreateGroupDocumentView mmCreateGroupDocumentView;
    private CreateGroupDocumentRepository mCreateGroupDocumentRepository;

    public CreateGroupDocumentPresenter(Context mContext, CreateGroupDocumentView mmCreateGroupDocumentView) {
        this.mContext = mContext;
        this.mmCreateGroupDocumentView = mmCreateGroupDocumentView;
        this.mCreateGroupDocumentRepository = new CreateGroupDocumentRepositoryIml();
    }

    public void createGroupDocument(int idGroup, DocumentDTO documentDTO){
        mCreateGroupDocumentRepository.createGroupDocument(mContext, idGroup, documentDTO, new CallBackData<String>() {
            @Override
            public void onSuccess(String s) {
                mmCreateGroupDocumentView.createGroupDocumentSuccess(s);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {

            }
        });
    }
}
