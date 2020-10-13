package com.fpt.gta.view;

import com.fpt.gta.data.dto.GroupResponseDTO;

public interface PrintGroupPreviewView {
    void printGroupPreviewSucess(GroupResponseDTO groupResponseDTO);
    void printGroupPreviewFail(String messageFail);
}
