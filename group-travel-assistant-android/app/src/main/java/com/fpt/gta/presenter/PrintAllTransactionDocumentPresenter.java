package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.repository.PrintAllGroupDocumentRepositoryIml;
import com.fpt.gta.repository.PrintAllTransactionDocumentRepository;
import com.fpt.gta.repository.PrintAllTransactionDocumentRepositoryIml;
import com.fpt.gta.view.PrintAllGroupDocumentView;
import com.fpt.gta.view.PrintAllTransactionDocumentView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintAllTransactionDocumentPresenter {
    private Context mContext;
    private PrintAllTransactionDocumentRepository mPrintAllTransactionDocumentRepository;
    private PrintAllTransactionDocumentView mPrintAllTransactionDocumentView;


    public PrintAllTransactionDocumentPresenter(Context mContext, PrintAllTransactionDocumentView mPrintAllTransactionDocumentView) {
        this.mContext = mContext;
        this.mPrintAllTransactionDocumentView = mPrintAllTransactionDocumentView;
        this.mPrintAllTransactionDocumentRepository = new PrintAllTransactionDocumentRepositoryIml();

    }

    public void printAllTransactionDocument(Integer idTransaction) {
        mPrintAllTransactionDocumentRepository.printAllTransactionDocumentRepository(mContext, idTransaction, new CallBackData<List<DocumentDTO>>() {
            @Override
            public void onSuccess(List<DocumentDTO> documentDTOList) {
                mPrintAllTransactionDocumentView.printAllTransactionDocumentSuccess(documentDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintAllTransactionDocumentView.printAllTransactionDocumentFail(message);
            }
        });
    }
}


