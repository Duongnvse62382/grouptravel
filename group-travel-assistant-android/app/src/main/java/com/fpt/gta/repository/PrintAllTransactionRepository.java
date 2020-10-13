package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintAllTransactionRepository {
    void printAllTransactionRepository(Context mContext, int idGroup, CallBackData<List<TransactionDTO>> listCallBackData);
}
