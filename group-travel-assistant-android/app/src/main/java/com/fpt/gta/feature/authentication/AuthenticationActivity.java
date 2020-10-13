package com.fpt.gta.feature.authentication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.fpt.gta.App;
import com.fpt.gta.R;
import com.fpt.gta.presenter.SyncFirebasePresenter;
import com.fpt.gta.view.LoginGoogleMailView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.List;

public class AuthenticationActivity extends AppCompatActivity implements LoginGoogleMailView {

    private SyncFirebasePresenter mSyncFirebasePresenter;
    private DatabaseReference reference;
    private Dialog dialog;
    private static final int RC_SIGN_IN = 123;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    public Intent getSigninIntent() {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
//        setFinishOnTouchOutside(false);
        checkAuthenticationState();

    }

    public void checkAuthenticationState() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            startLoginActivity();
        }
    }

    public void startLoginActivity() {
// Create and launch sign-in intent
        Intent signInIntent = getSigninIntent();
        startActivityForResult(
                signInIntent,
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mSyncFirebasePresenter = new SyncFirebasePresenter(this, AuthenticationActivity.this);
                mSyncFirebasePresenter.syncFirebase(requestCode, resultCode, data);
            } else {
                startLoginActivity();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void loginSS(String messageSS) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getDisplayName() == null) {
            dialogInformation();
        } else {
            setResult(RESULT_OK);
            Toast.makeText(this, "Hi " + user.getDisplayName(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void dialogInformation() {
        dialog = new Dialog(AuthenticationActivity.this);
        dialog.setContentView(R.layout.dialog_input_information);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                AuthUI.getInstance()
//                        .signOut(App.getAppContext())
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Intent intent = new Intent();
//                                    setResult(RESULT_CANCELED, intent);
//                                    finish();
//                                }
//                            }
//                        })
//                ;
//                Intent intent = new Intent();
//                setResult(RESULT_CANCELED, intent);
//                finish();
//            }
//        });
        dialog.setCanceledOnTouchOutside(false);
        EditText edtDialogName = dialog.findViewById(R.id.dialogInputName);
        Button btnDialogChangeInformation = dialog.findViewById(R.id.dialog_button_information_apply);
        btnDialogChangeInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtDialogName.getText().toString();
                if (name == null || name.trim().length() <= 0 || name.equals("")) {
                    edtDialogName.setError("Please input Name");
                } else {
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK);
                                            mSyncFirebasePresenter.syncFirebase(RC_SIGN_IN, RESULT_OK, new Intent());
                                        } else {
                                            Toast.makeText(AuthenticationActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent();
                                            setResult(RESULT_CANCELED);
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        Button btnDialogCancelInformation = dialog.findViewById(R.id.dialog_button_close_information);
        btnDialogCancelInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (edtDialogName.getText().toString()!=null && edtDialogName.getText().toString().trim().length() <=0){
//                    edtDialogName.setError("Please input Name");
//                }else {
//                dialog.dismiss();
//                }
                Intent intent = new Intent();
                setResult(RESULT_CANCELED);
                dialog.dismiss();
                finish();

            }
        });
        dialog.show();
    }


    @Override
    public void loginFail(String message) {
        startLoginActivity();
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
