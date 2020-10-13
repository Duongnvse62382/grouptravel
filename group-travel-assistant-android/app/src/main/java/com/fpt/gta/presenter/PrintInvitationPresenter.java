package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.PrintInvitationRepository;
import com.fpt.gta.repository.PrintInvitationRepositoryIml;
import com.fpt.gta.view.PrintInvitationView;
import com.fpt.gta.webService.CallBackData;

public class PrintInvitationPresenter {
    private Context mContext;
    private PrintInvitationView mPrintInvitationView;
    private PrintInvitationRepository mPrintInvitationRepository;

    public PrintInvitationPresenter(Context mContext, PrintInvitationView mPrintInvitationView) {
        this.mContext = mContext;
        this.mPrintInvitationView = mPrintInvitationView;
        this.mPrintInvitationRepository = new PrintInvitationRepositoryIml();
    }

    public void printInvitationCode(int idGroup){
        mPrintInvitationRepository.printInvitation(mContext, idGroup, new CallBackData<String>() {
            @Override
            public void onSuccess(String s) {
                mPrintInvitationView.viewInvitationDialog("https://grouptravelassistant.page.link/?apn=com.fpt.gta&link=https://www.grouptravel.com/?test=" + s + "%26idgroup=" + idGroup);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintInvitationView.printCodeInviteFail(message);
            }
        });
    }
}
