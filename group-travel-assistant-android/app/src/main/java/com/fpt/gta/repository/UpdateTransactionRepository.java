package com.fpt.gta.repository;

        import android.content.Context;

        import com.fpt.gta.data.dto.TransactionDTO;
        import com.fpt.gta.webService.CallBackData;

public interface UpdateTransactionRepository {
    void updateTransaction(Context context, TransactionDTO transactionDTO, CallBackData<String> callBackData);

}
