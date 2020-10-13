package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.webService.CallBackData;

public interface CreateGroupDocumentRepository {
    void createGroupDocument(Context context, int idGroup, DocumentDTO documentDTO,  CallBackData<String> callBackData);
}
