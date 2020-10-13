package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteGroupDocumentRepository {
    void deleteGroupDocument(Context context, Integer idDocument, CallBackData callBackData);

}
