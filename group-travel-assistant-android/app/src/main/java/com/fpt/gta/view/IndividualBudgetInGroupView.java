package com.fpt.gta.view;

import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.IndividualBudgetDTO;

import java.util.List;

public interface IndividualBudgetInGroupView {
    void printAllBudgetIndividualInGroupSuccess(List<IndividualBudgetDTO> individualBudgetDTOList);
    void printAllBudgetIndividualInGroupFail(String messageFail);
}
