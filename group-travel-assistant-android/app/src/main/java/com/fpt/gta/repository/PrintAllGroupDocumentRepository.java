package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintAllGroupDocumentRepository {
    void printAllGroupDocumentRepository(Context context, int idGroup, CallBackData<List<DocumentDTO>> callBackData);

}
