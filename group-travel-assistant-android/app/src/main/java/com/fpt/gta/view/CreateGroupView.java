package com.fpt.gta.view;

import com.fpt.gta.data.dto.GroupResponseDTO;

public interface CreateGroupView {
    void createGroupSuccess(GroupResponseDTO groupResponseDTO);
    void CreateGroupFail(String message);
}
