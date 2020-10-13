package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteTransactionDocumentRepository {
    void deleteTransactionDocument(Context mContext, Integer idDocument, CallBackData callBackData);
}
