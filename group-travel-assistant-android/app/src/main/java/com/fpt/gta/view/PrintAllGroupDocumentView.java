package com.fpt.gta.view;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.TransactionDTO;

import java.util.List;

public interface PrintAllGroupDocumentView {
    void printAllGroupDocumentSuccess(List<DocumentDTO> documentDTOList);
    void printAllGroupDocumentFail(String messageFail);
}
