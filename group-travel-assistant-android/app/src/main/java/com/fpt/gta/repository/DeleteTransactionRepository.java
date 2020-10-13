package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.webService.CallBackData;

public interface DeleteTransactionRepository {
    void deleteTransaction(Context mContext, int idTransaction, CallBackData callBackData);
}
