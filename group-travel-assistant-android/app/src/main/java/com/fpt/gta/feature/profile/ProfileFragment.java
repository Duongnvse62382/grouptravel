package com.fpt.gta.feature.profile;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.fpt.gta.MainActivity;
import com.fpt.gta.R;
import com.fpt.gta.feature.managegroup.overviewgroup.GroupOverViewActivity;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.util.ProgressDialogUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment implements ProfileView {
    private  Uri uriImage;
    @BindView(R.id.txtFullName)
    TextView txtFullName;
    @BindView(R.id.txtEmail)
    TextView txtEmail;
    @BindView(R.id.txtPhone)
    TextView txtPhone;
    @BindView(R.id.txtDisplayName)
    TextView txtDisplayName;
    @BindView(R.id.profile_image)
    ImageView profile_image;

    @BindView(R.id.button_edit_photo)
    TextView button_edit_photo;

    @BindView(R.id.txtEmailShouldRequired)
    TextView txtEmailShouldRequired;

    @BindView(R.id.txtEmailVerified)
    TextView txtEmailVerified;

    @BindView(R.id.button_resend_verification_email)
    TextView button_resend_verification_email;

    ProfilePresenter profilePresenter;
    final private int RESULT_LOAD_IMAGE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialogUtil(getContext());
        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        profilePresenter = new ProfilePresenter(this,getActivity());
        profilePresenter.setupView();
    }

    public void updateInitView(FirebaseUser firebaseUser) {
        hud.dismiss();
        if (!TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            txtFullName.setText(firebaseUser.getDisplayName());
            txtDisplayName.setText(firebaseUser.getDisplayName());
            txtFullName.setTextColor(Color.BLACK);
            txtDisplayName.setTextColor(Color.BLACK);
        } else {
            txtFullName.setTextColor(Color.RED);
            txtDisplayName.setTextColor(Color.RED);
        }

        if (firebaseUser.getPhoneNumber() != null && !firebaseUser.getPhoneNumber().trim().isEmpty()) {
            txtPhone.setText(firebaseUser.getPhoneNumber());
            txtPhone.setTextColor(Color.BLACK);
        } else {
            txtPhone.setTextColor(Color.RED);
        }

        if (!TextUtils.isEmpty(firebaseUser.getEmail())) {
            txtEmail.setText(firebaseUser.getEmail());
            txtEmail.setTextColor(Color.BLACK);
        } else {
            txtEmail.setText("Not linked yet");
            txtEmail.setTextColor(Color.RED);
        }
        if (firebaseUser.isEmailVerified()) {
            txtEmailVerified.setVisibility(View.VISIBLE);
        } else {
            if (!TextUtils.isEmpty(firebaseUser.getEmail())) {
                button_resend_verification_email.setVisibility(View.VISIBLE);
            }
            txtEmailShouldRequired.setVisibility(View.VISIBLE);
        }

        try {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.default_profile).into(profile_image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.button_sign_out)
    public void signOut() {
        profilePresenter.signOut(getContext());
    }

    @Override
    public void signOutSuccess() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        getContext().startActivity(intent);
        getActivity().finish();
    }

//    @OnClick(R.id.button_link_google)
//    public void linkWithGoogle() {
//        startActivityForResult(profilePresenter.getGoogleSigninIntent(getContext(), getString(R.string.default_web_client_id)), RequestCode.RC_SIGN_IN_GOOGLE);
//    }
//
//    @OnClick(R.id.button_unlink_google)
//    public void unlinkWithGoogle() {
//        profilePresenter.unlinkWithGoogle();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK) {
//            profilePresenter.handleUploadProfileImage(ImagePicker.getFirstImageOrNull(data));
            uriImage = data.getData();
            profilePresenter.handleUploadImage(uriImage);
        }
//        if (requestCode == RequestCode.RC_SIGN_IN_GOOGLE) {
//            profilePresenter.handleLinkWithGoogleAccount(data);
//        } else {
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }
    }

    @OnClick(R.id.button_link_phone)
    public void linkPhone() {
        showPhoneNumberInput();
    }

    public void showPhoneNumberInput() {
        EditText edtPhone = new EditText(getContext());
        edtPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        edtPhone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+ "");
        new AlertDialog.Builder(getContext()).setTitle("Phone Number").setView(edtPhone).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtPhone.getText().toString().isEmpty()) {
                    toastMessage("Cannot update the phone number you have entered");
                } else {
                    profilePresenter.handleSubmitPhoneNumber(dialog, edtPhone.getText().toString(), getActivity());
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    AlertDialog phoneCodeAlertDialog;

    public void showPhoneCodeInput() {
        EditText edtVerificationCode = new EditText(getContext());
        InputFilter maxLengthFilter = new InputFilter.LengthFilter(6);
        edtVerificationCode.setFilters((InputFilter[]) Arrays.asList(maxLengthFilter).toArray());
        edtVerificationCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        phoneCodeAlertDialog = new AlertDialog.Builder(getContext()).setTitle("Enter the 6-digit verification code").setView(edtVerificationCode).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtVerificationCode.getText().toString().length() != 6) {
                    showPhoneCodeInput();
                } else {
                    profilePresenter.handleSubmitConfirmCode(edtVerificationCode.getText().toString(), getActivity());
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    @Override
    public void dismissPhoneCodeInput() {
        if (phoneCodeAlertDialog != null) {
            phoneCodeAlertDialog.dismiss();
        }
    }


    @OnClick(R.id.button_displayname_udate)
    public void onClickButtonDisplayNameUpdate() {
        showDisplayNameInput();
    }

    public void showDisplayNameInput() {
        EditText edtDisplayName = new EditText(getContext());
        edtDisplayName.setInputType(InputType.TYPE_CLASS_TEXT);
        edtDisplayName.setMaxLines(1);
        edtDisplayName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        phoneCodeAlertDialog = new AlertDialog.Builder(getContext()).setTitle("Enter your name").setView(edtDisplayName).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profilePresenter.handleSubmitUpdateDisplayName(edtDisplayName.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    @OnClick(R.id.button_email_update)
    public void onClickButtonEmailUpdate() {
        showEmailInput();
    }

    public void showEmailInput() {
        EditText edtEmail = new EditText(getContext());
        edtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtEmail.setMaxLines(1);
        edtEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        phoneCodeAlertDialog = new AlertDialog.Builder(getContext()).setTitle("Enter your Email").setView(edtEmail).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profilePresenter.handleSubmitUpdateEmail(edtEmail.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    @OnClick(R.id.button_resend_verification_email)
    public void resendVerificationEmail() {
        profilePresenter.resendVerificationEmail();
    }

    @OnClick(R.id.profile_image)
    public void onClickProfileImage() {
        onClickEditPhoto();
    }

    @OnClick(R.id.button_edit_photo)
    public void onClickEditPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

    }

    @Override
    public void toastMessage(String message) {
        try {
            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ProgressDialogUtil progressDialog;
    private KProgressHUD hud;

    @Override
    public void showProgressDialog() {
        hud.show();
//        progressDialog.showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        hud.dismiss();
        progressDialog.hideProgressDialog();
    }
}