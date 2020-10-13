package com.fpt.gta.view;

import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.IndividualBudgetDTO;
import com.fpt.gta.data.dto.MemberDTO;

import java.util.List;

public interface GetMyBudgetView {
    void getMyBudgetSuccess(MemberDTO memberDTO);

    void getMyBudgetFail(String messageFail);
}
