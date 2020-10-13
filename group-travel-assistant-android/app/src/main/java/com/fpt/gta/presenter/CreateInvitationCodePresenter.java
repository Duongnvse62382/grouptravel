package com.fpt.gta.presenter;

import android.content.Context;


import com.fpt.gta.repository.CreateInviteCodeRepository;
import com.fpt.gta.repository.CreateInviteCodeRepositoryIml;
import com.fpt.gta.repository.PrintInvitationRepository;
import com.fpt.gta.repository.PrintInvitationRepositoryIml;
import com.fpt.gta.view.CreateInvitationCodeView;
import com.fpt.gta.view.PrintInvitationView;
import com.fpt.gta.webService.CallBackData;

public class CreateInvitationCodePresenter {
    private Context mContex;
    private CreateInvitationCodeView mCreateInvitationCodeView;
    private CreateInviteCodeRepository mCreateInviteCodeRepository;

    public CreateInvitationCodePresenter(Context mContex, CreateInvitationCodeView mCreateInvitationCodeView) {
        this.mContex = mContex;
        this.mCreateInvitationCodeView = mCreateInvitationCodeView;
        this.mCreateInviteCodeRepository = new CreateInviteCodeRepositoryIml();
    }

    public void createInvitationCode(int idGroup) {
        mCreateInviteCodeRepository.createInviteCode(mContex,idGroup, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mCreateInvitationCodeView.createInvitationSuccess("Success");
                ((PrintInvitationView)mCreateInvitationCodeView).viewInvitationDialog("https://grouptravelassistant.page.link/?apn=com.fpt.gta&link=https://www.grouptravel.com/?test=" + mess + "%26idgroup=" + idGroup);
            }

            @Override
            public void onFail(String message) {
                mCreateInvitationCodeView.createInvitationFail(message);
            }
        });
    }

}
