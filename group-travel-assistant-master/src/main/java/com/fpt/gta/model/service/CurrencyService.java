package com.fpt.gta.model.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fpt.gta.model.entity.Currency;
import com.fpt.gta.model.entity.CurrencyRate;

import java.util.List;

public interface CurrencyService {
    void crawlCurrencyCode() throws JsonProcessingException;

    CurrencyRate getCurrencyRate(String firstCurrencyCode, String secondCurrencyCode);

    List<Currency> findAllCurrency();

    double crawlCurrencyRate(String firstCurrencyCode, String secondCurrencyCode);
}
