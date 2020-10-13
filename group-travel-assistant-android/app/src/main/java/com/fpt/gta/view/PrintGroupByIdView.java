package com.fpt.gta.view;

import com.fpt.gta.data.dto.GroupResponseDTO;

public interface PrintGroupByIdView {
    void printGroupByIdSS(GroupResponseDTO groupResponseDTO);
    void printGroupByIdFail(String messageFail);
}
