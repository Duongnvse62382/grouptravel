package com.fpt.gta.feature.profile;

import com.google.firebase.auth.FirebaseUser;

public interface ProfileView {
    void updateInitView(FirebaseUser firebaseUser);

    void signOutSuccess();

    void toastMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();

    void showPhoneCodeInput();

    void dismissPhoneCodeInput();
}
