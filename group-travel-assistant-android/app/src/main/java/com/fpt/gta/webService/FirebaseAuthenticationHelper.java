package com.fpt.gta.webService;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class FirebaseAuthenticationHelper {
    private FirebaseAuth mAuth;

    public FirebaseAuthenticationHelper() {
        mAuth = FirebaseAuth.getInstance();
    }


    public String getUid() {
        return getCurrentUser().getUid();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void getIdToken(boolean forceRefresh, OnCompleteListener<GetTokenResult> callback) {
        FirebaseUser firebaseUser = getCurrentUser();

        firebaseUser.getIdToken(forceRefresh).addOnCompleteListener(callback);
    }

    public Task<GetTokenResult> getIdToken(boolean forceRefresh) throws NoSignedInUserException {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            throw new NoSignedInUserException();
        }
        return firebaseUser.getIdToken(forceRefresh);
    }

    public class NoSignedInUserException extends Exception {

    }
}
