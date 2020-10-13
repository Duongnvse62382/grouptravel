package com.fpt.gta.view;

import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;

import java.util.List;

public interface PrintAllCurrencyView {

    void printAllCurrencySuccess(List<CurrencyDTO> currencyDTOList);

    void printAllCurrencyFail(String messageFail);

}
