package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintAllCurrencyRepository {
    void printAllCurrency(Context context, CallBackData<List<CurrencyDTO>> callBackData);

}
