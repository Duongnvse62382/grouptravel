package com.fpt.gta.view;

import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.TransactionDTO;

import java.util.List;

public interface PrintAllTransactionView {
    void printAllTransactionSuccess(List<TransactionDTO> transactionDTOList);
    void printAllTransactionFail(String messageFail);
}
