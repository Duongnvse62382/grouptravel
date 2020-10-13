package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.webService.CallBackData;

import java.math.BigDecimal;

public interface ConvertCurrencyRepository {
    void convertCurrency(Context context, String firstCurrency, String secondCurrency, CallBackData<BigDecimal> callBackData);
}
