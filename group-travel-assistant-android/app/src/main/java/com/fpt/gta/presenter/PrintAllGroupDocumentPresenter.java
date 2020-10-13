package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.repository.PrintAllGroupDocumentRepository;
import com.fpt.gta.repository.PrintAllGroupDocumentRepositoryIml;
import com.fpt.gta.view.PrintAllGroupDocumentView;
import com.fpt.gta.webService.CallBackData;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

public class PrintAllGroupDocumentPresenter {
    private Context mContext;
    private PrintAllGroupDocumentRepository mPrintAllGroupDocumentRepository;
    private PrintAllGroupDocumentView mPrintAllGroupDocumentView;

    public PrintAllGroupDocumentPresenter(Context mContext, PrintAllGroupDocumentView mPrintAllGroupDocumentView) {
        this.mContext = mContext;
        this.mPrintAllGroupDocumentView = mPrintAllGroupDocumentView;
        this.mPrintAllGroupDocumentRepository = new PrintAllGroupDocumentRepositoryIml();
    }

    public void PrintAllGroupDocument(int idGroup){
        mPrintAllGroupDocumentRepository.printAllGroupDocumentRepository(mContext, idGroup, new CallBackData<List<DocumentDTO>>() {
            @Override
            public void onSuccess(List<DocumentDTO> documentDTOList) {
                mPrintAllGroupDocumentView.printAllGroupDocumentSuccess(documentDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintAllGroupDocumentView.printAllGroupDocumentFail(message);
            }
        });
    }
}
