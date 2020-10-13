package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.AppInstanceDTO;
import com.fpt.gta.repository.SignOutFirebaseRepository;
import com.fpt.gta.repository.SignOutFirebaseRepositoryIml;
import com.fpt.gta.view.SignOutFirebaseView;
import com.fpt.gta.webService.CallBackData;

public class SignOutFirebasePresenter {
    private Context mContext;
    private SignOutFirebaseRepository mSignOutFirebaseRepository;
    private SignOutFirebaseView mSignOutFirebaseView;

    public SignOutFirebasePresenter(Context mContext, SignOutFirebaseView mSignOutFirebaseView) {
        this.mContext = mContext;
        this.mSignOutFirebaseView = mSignOutFirebaseView;
        this.mSignOutFirebaseRepository = new SignOutFirebaseRepositoryIml();
    }

    public void signOutFirebase(AppInstanceDTO appInstanceDTO){
        mSignOutFirebaseRepository.signoutcFirebase(mContext, appInstanceDTO, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                mSignOutFirebaseView.signoutSuccess(mess);
            }

            @Override
            public void onFail(String message) {
                mSignOutFirebaseView.signoutFail(message);
            }
        });
    }
}
