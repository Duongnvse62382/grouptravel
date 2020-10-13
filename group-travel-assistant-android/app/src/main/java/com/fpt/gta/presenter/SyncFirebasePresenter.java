package com.fpt.gta.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fpt.gta.data.dto.AppInstanceDTO;
import com.fpt.gta.feature.managegroup.overviewgroup.GroupOverViewActivity;
import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.repository.LoginReponsitory;
import com.fpt.gta.repository.LoginRepositoryIml;
import com.fpt.gta.view.LoginGoogleMailView;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.app.Activity.RESULT_OK;

public class SyncFirebasePresenter {
    private static final int RC_SIGN_IN = 123;

    private Context mContext;
    private LoginGoogleMailView loginGoogleMailView;
    private LoginReponsitory loginReponsitory;

    public SyncFirebasePresenter(Context mContext, LoginGoogleMailView loginGoogleMailView) {
        this.mContext = mContext;
        this.loginGoogleMailView = loginGoogleMailView;
        this.loginReponsitory = new LoginRepositoryIml();
    }

    public void syncFirebase(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    String token = task.getResult().getToken();
                    AppInstanceDTO appInstanceDTO = new AppInstanceDTO();
                    appInstanceDTO.setIdInstance(token);
                    loginReponsitory.syncFirebase(mContext, appInstanceDTO, new CallBackData() {
                        @Override
                        public void onSuccess(Object o) {
                        }

                        @Override
                        public void onSuccessString(String mess) {
                            loginGoogleMailView.loginSS("Đăng nhập thành công");
                        }

                        @Override
                        public void onFail(String message) {
                            loginGoogleMailView.loginFail("Login error");
                        }
                    });
                });

            } else {
                loginGoogleMailView.loginFail("Login error");
            }
        }
    }

}
