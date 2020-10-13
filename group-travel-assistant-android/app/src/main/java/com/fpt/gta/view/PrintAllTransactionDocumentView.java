package com.fpt.gta.view;

import com.fpt.gta.data.dto.DocumentDTO;

import java.util.List;

public interface PrintAllTransactionDocumentView {
    void printAllTransactionDocumentSuccess(List<DocumentDTO> documentDTOList);
    void printAllTransactionDocumentFail(String messageFail);
}
