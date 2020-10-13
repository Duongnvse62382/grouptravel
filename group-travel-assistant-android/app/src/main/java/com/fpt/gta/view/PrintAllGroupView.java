package com.fpt.gta.view;

import com.fpt.gta.data.dto.GroupResponseDTO;

import java.util.List;

public interface PrintAllGroupView {
    void printAllGroupSuccess(List<GroupResponseDTO> groupResponseDTOList);
    void printAllGroupFail(String messageFail);
}
