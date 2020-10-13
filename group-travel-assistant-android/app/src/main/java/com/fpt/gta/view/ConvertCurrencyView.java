package com.fpt.gta.view;

import java.math.BigDecimal;

public interface ConvertCurrencyView {
    void convertCurrencySuccess(BigDecimal bigDecimal);
    void convertCurrencyFail(String messageFail);
}
