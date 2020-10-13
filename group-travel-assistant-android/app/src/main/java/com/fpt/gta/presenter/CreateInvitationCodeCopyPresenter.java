package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.CreateInviteCodeCopyRepository;
import com.fpt.gta.repository.CreateInviteCodeCopyRepositoryIml;
import com.fpt.gta.view.CreateInvitationCodeCopyView;
import com.fpt.gta.webService.CallBackData;

public class CreateInvitationCodeCopyPresenter {
    private Context mContex;
    private CreateInvitationCodeCopyView mCreateInvitationCodeCopyView;
    private CreateInviteCodeCopyRepository mCreateInviteCodeCopyRepository;

    public CreateInvitationCodeCopyPresenter(Context mContex, CreateInvitationCodeCopyView mCreateInvitationCodeCopyView) {
        this.mContex = mContex;
        this.mCreateInvitationCodeCopyView = mCreateInvitationCodeCopyView;
        this.mCreateInviteCodeCopyRepository = new CreateInviteCodeCopyRepositoryIml();
    }

    public void createInvitationCodeCopy(Integer idGroup, String invitationCode) {
        mCreateInviteCodeCopyRepository.createInviteCodeCopy(mContex, idGroup, invitationCode, new CallBackData<GroupResponseDTO>() {
            @Override
            public void onSuccess(GroupResponseDTO groupResponseDTO) {
                mCreateInvitationCodeCopyView.createCopyInvitationSuccess(groupResponseDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mCreateInvitationCodeCopyView.createCopyInvitationFail(message);
            }
        });
    }
}
