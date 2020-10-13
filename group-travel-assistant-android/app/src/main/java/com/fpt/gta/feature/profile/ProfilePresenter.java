package com.fpt.gta.feature.profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.esafirm.imagepicker.model.Image;
import com.firebase.ui.auth.AuthUI;
import com.fpt.gta.App;
import com.fpt.gta.data.dto.AppInstanceDTO;
import com.fpt.gta.feature.managegroup.overviewgroup.GroupOverViewActivity;
import com.fpt.gta.repository.LoginReponsitory;
import com.fpt.gta.repository.LoginRepositoryIml;
import com.fpt.gta.repository.SignOutFirebaseRepository;
import com.fpt.gta.repository.SignOutFirebaseRepositoryIml;
import com.fpt.gta.util.ExifUtil;
import com.fpt.gta.webService.CallBackData;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class ProfilePresenter {
    ProfileView profileView;
    Context context;

    public ProfilePresenter(ProfileView profileView, Context context) {
        this.profileView = profileView;
        this.context = context;
    }

    public void setupView() {
        profileView.updateInitView(FirebaseAuth.getInstance().getCurrentUser());
        FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                profileView.updateInitView(FirebaseAuth.getInstance().getCurrentUser());
            }
        });

    }

    public void signOut(Context context) {
        SignOutFirebaseRepository signOutFirebaseRepository = new SignOutFirebaseRepositoryIml();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            AppInstanceDTO appInstanceDTO = new AppInstanceDTO();
            String token = task.getResult().getToken();
            appInstanceDTO.setIdInstance(token);
            signOutFirebaseRepository.signoutcFirebase(context, appInstanceDTO, new CallBackData() {
                @Override
                public void onSuccess(Object o) {

                }

                @Override
                public void onSuccessString(String mess) {
                    AuthUI.getInstance()
                            .signOut(App.getAppContext())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        profileView.signOutSuccess();
                                    }
                                }
                            })
                    ;
                }

                @Override
                public void onFail(String message) {

                }
            });

        });


    }

//    public Intent getGoogleSigninIntent(Context context, String serverClientId) {
//        return getGoogleSignInClient().getSignInIntent();
//    }

//    public GoogleSignInClient getGoogleSignInClient() {
//        return GoogleSignIn.getClient(App.getAppContext(), firebaseAuthenticationHelper.getGoogleSignInOptions());
//    }
//
//    public void handleLinkWithGoogleAccount(Intent data) {
//        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//        try {
//            GoogleSignInAccount account = task.getResult(ApiException.class);
//            profileView.showProgressDialog();
//            linkWithGoogle(account);
//            getGoogleSignInClient().revokeAccess();
//            getGoogleSignInClient().signOut();
//
//        } catch (ApiException e) {
//            profileView.toastMessage("Lỗi mạng");
//        }
//    }

    private void linkWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setupView();
                            profileView.toastMessage("Link Successfully");
                        } else {
                            profileView.toastMessage("Account was used");
                        }
                        profileView.hideProgressDialog();
                    }
                });
    }

    public void unlinkWithGoogle() {
        profileView.showProgressDialog();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    AtomicBoolean canUnlink = new AtomicBoolean(false);
                    reloadCurrentUser().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (FirebaseAuth.getInstance().getCurrentUser().getProviderData().size() > 2) {
                                    canUnlink.set(true);
                                } else {
                                    profileView.toastMessage("At least one login type");
                                    profileView.hideProgressDialog();
                                }
                            } else {
                                profileView.toastMessage("Link unsuccessfully");
                                profileView.hideProgressDialog();
                            }
                            countDownLatch.countDown();
                        }
                    });
                    try {
                        countDownLatch.await(10L, TimeUnit.SECONDS);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }

                    if (canUnlink.get()) {
                        FirebaseAuth.getInstance().getCurrentUser().unlink(GoogleAuthProvider.PROVIDER_ID)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            setupView();
                                            profileView.toastMessage("Unlink Successfully");
                                        } else {
                                            profileView.toastMessage("Unlink Unsuccessfully");
                                        }
                                        profileView.hideProgressDialog();
                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    String verificationId;

    public void handleSubmitPhoneNumber(DialogInterface phoneCodeAlertDialog, String phoneNumber, Activity activity) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                phoneCodeAlertDialog.dismiss();
                linkPhoneCredential(phoneAuthCredential, activity);
                setupView();
                profileView.dismissPhoneCodeInput();
            }

            @Override
            public void onCodeSent(String verifyId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                profileView.toastMessage("Sent Code");
                verificationId = verifyId;
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                profileView.dismissPhoneCodeInput();
                profileView.toastMessage("Plase check your phone login number");
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60l,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,
                mCallbacks// Activity (for callback binding)
        );
        profileView.showPhoneCodeInput();
    }

    public void handleSubmitConfirmCode(String varificationCode, Activity activity) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, varificationCode);
        linkPhoneCredential(credential, activity);
        setupView();
    }

    public void linkPhoneCredential(PhoneAuthCredential phoneAuthCredential, Activity activity) {
        FirebaseAuth.getInstance().getCurrentUser().updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    profileView.toastMessage("Link successfully");
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthInvalidUserException) {
                        profileView.toastMessage("Wrong code");
                    }
                    if (exception instanceof FirebaseAuthUserCollisionException) {
                        profileView.toastMessage("Phone number was used");
                    }

                }
            }
        });
    }

    private Task<Void> reloadCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser().reload();
    }

    public void handleSubmitUpdateDisplayName(String displayName) {
        if (displayName.trim().isEmpty()) {
            profileView.toastMessage("Name is not blank");
        } else {
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(displayName).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        LoginReponsitory loginReponsitory = new LoginRepositoryIml();
                        loginReponsitory.syncFirebase(context, new AppInstanceDTO(), new CallBackData() {
                            @Override
                            public void onSuccess(Object o) {

                            }

                            @Override
                            public void onSuccessString(String mess) {
                                profileView.toastMessage("Change display name successfully");
                                setupView();
                            }

                            @Override
                            public void onFail(String message) {

                            }
                        });
                    } else {
                        profileView.toastMessage("Change name unsuccessfully");
                        setupView();
                    }
                }
            });
        }
    }

    public void handleSubmitUpdateEmail(String email) {
        FirebaseAuth.getInstance().getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    profileView.toastMessage("Updated email successfully");
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                    setupView();
                } else {
                    profileView.toastMessage("Email was used");
                }

            }
        });
    }

    public void handleUploadProfileImage(Image firstImageOrNull) {
        if (firstImageOrNull != null) {
            profileView.showProgressDialog();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference imageFolderReference = FirebaseStorage.getInstance().getReference("profile-image");
            StorageReference imageReference = imageFolderReference.child(firebaseUser.getUid() + "jpg");

            Bitmap bitmap = BitmapFactory.decodeFile(firstImageOrNull.getPath());
            bitmap = ExifUtil.rotateBitmap(firstImageOrNull.getPath(), bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

//            bitmap = Bitmap.createScaledBitmap(bitmap, 480, 640, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageReference.putBytes(data);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        profileView.hideProgressDialog();
                        Log.e("Firebase Exception", task.getException().getStackTrace().toString());
                    }
                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                        firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                LoginReponsitory loginReponsitory = new LoginRepositoryIml();
                                loginReponsitory.syncFirebase(context, new AppInstanceDTO(), new CallBackData() {
                                    @Override
                                    public void onSuccess(Object o) {

                                    }

                                    @Override
                                    public void onSuccessString(String mess) {
                                        profileView.toastMessage("Updated profile image successfully");
                                        setupView();
                                    }

                                    @Override
                                    public void onFail(String message) {

                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }
    public void handleUploadImage(Uri pickedImage) {
        if (pickedImage != null) {
            profileView.showProgressDialog();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference imageFolderReference = FirebaseStorage.getInstance().getReference("profile-image");
            StorageReference imageReference = imageFolderReference.child(firebaseUser.getUid() + "jpg");

            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            UploadTask uploadTask = imageReference.putFile(pickedImage);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        profileView.hideProgressDialog();
                        Log.e("Firebase Exception", task.getException().getStackTrace().toString());
                    }
                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                        firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                LoginReponsitory loginReponsitory = new LoginRepositoryIml();
                                loginReponsitory.syncFirebase(context, new AppInstanceDTO(), new CallBackData() {
                                    @Override
                                    public void onSuccess(Object o) {

                                    }

                                    @Override
                                    public void onSuccessString(String mess) {
                                        profileView.toastMessage("Uploaded image profile successfully");
                                        setupView();
                                    }

                                    @Override
                                    public void onFail(String message) {

                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }


    public void resendVerificationEmail() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    profileView.toastMessage("Sent");
                }
            }
        });
    }
}