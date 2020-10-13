package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintAllTransactionDocumentRepository {
    void printAllTransactionDocumentRepository(Context context, Integer idTransaction, CallBackData<List<DocumentDTO>> callBackData);

}
