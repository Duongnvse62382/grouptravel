package com.fpt.gta.view;

import com.fpt.gta.data.dto.GroupResponseDTO;

import java.text.ParseException;

public interface CreateInvitationCodeCopyView {
    void createCopyInvitationSuccess(GroupResponseDTO groupResponseDTO);
    void createCopyInvitationFail(String messageFail);
}
