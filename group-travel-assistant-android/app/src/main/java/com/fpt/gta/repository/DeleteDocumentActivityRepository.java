package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteDocumentActivityRepository {
    void deleteDocumentActivity(Context context, Integer idDocument, CallBackData callBackData);

}
