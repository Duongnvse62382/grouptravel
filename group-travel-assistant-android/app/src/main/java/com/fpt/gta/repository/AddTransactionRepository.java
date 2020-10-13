package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface AddTransactionRepository {
    void createTransaction(Context context, int idGroup, TransactionDTO transactionDTO, CallBackData callBackData);
}
