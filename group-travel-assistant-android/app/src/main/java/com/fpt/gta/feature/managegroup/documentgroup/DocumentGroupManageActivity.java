package com.fpt.gta.feature.managegroup.documentgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.manageplan.documentactivity.ActivityDocumentActivity;
import com.fpt.gta.feature.managetransaction.addtransaction.AddTransactionDocumentActivity;
import com.fpt.gta.feature.managetransaction.edittransaction.EditTransactionDocumentActivity;
import com.fpt.gta.feature.managetrip.overviewtrip.TripOverviewActivity;
import com.fpt.gta.presenter.CreateGroupDocumentPresenter;
import com.fpt.gta.presenter.PrintAllGroupDocumentPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.HandleFile;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.CreateGroupDocumentView;
import com.fpt.gta.view.PrintAllGroupDocumentView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DocumentGroupManageActivity extends AppCompatActivity implements CreateGroupDocumentView, PrintAllGroupDocumentView, PrintMemberInGroupView {
    private int groupId;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageButton mSelectBtn;
    private ImageView imgGroupDocumentBack;
    private RecyclerView rcvDocumentList;
    private Bitmap photo;
    private CreateGroupDocumentPresenter mCreateGroupDocumentPresenter;
    final private int CAPTURE_IMAGE = 100;
    final private int PERMISSION_REQUEST_CODE = 200;
    private List<DocumentDTO> mDocumentDTOList;
    private int numberOfColumn = 3;
    private String contentType, txtUri;
    private DocumentGroupAdapter documentGroupAdapter;
    private StorageReference mStorage;
    private String checker = "";
    private PrintAllGroupDocumentPresenter mPrintAllGroupDocumentPresenter;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private List<MemberDTO> memberList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private boolean valid = false;
    private Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_manage);
        initView();
        initData();
    }

    private void initView() {
        groupId = SharePreferenceUtils.getIntSharedPreference(DocumentGroupManageActivity.this, GTABundle.IDGROUP);
        mStorage = FirebaseStorage.getInstance().getReference();
        mSelectBtn = (ImageButton) findViewById(R.id.select_btn);
        imgGroupDocumentBack = findViewById(R.id.imgGroupDocumentBack);
        rcvDocumentList = (RecyclerView) findViewById(R.id.upload_list);
        //RecyclerView
        rcvDocumentList.setLayoutManager(new GridLayoutManager(this, numberOfColumn));

    }

    private void initData() {
        imgGroupDocumentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(DocumentGroupManageActivity.this, DocumentGroupManageActivity.this);
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDocumentType();
            }
        });
        getData();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeAccepted && cameraAccepted & readAccepted) {
                        Toast.makeText(this, "All permissions are accepted", Toast.LENGTH_SHORT).show();
                        initData();
                    } else {
//                        Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DocumentGroupManageActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void updateUI() {
        if (documentGroupAdapter == null) {
            documentGroupAdapter = new DocumentGroupAdapter(mDocumentDTOList, DocumentGroupManageActivity.this);
            rcvDocumentList.setAdapter(documentGroupAdapter);
            documentGroupAdapter.setOnItemImageClickListener(new DocumentGroupAdapter.OnItemImageClickListener() {
                @Override
                public void onItemImageClickListener(DocumentDTO documentDTO, int position) {
                    if (documentDTO.getContentType().equals("image/jpeg") || documentDTO.getContentType().equals("image/png")) {
                        onClickImage(documentDTO);
                    } else if (documentDTO.getContentType().equals("application/pdf")) {
                        onClicPDF(documentDTO);
                    }
                }
            });
        } else {
            documentGroupAdapter.notifyChangeData(mDocumentDTOList);
        }
    }

    public void onClicPDF(DocumentDTO documentDTO) {
        Intent intent = new Intent(DocumentGroupManageActivity.this, PdfViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "DocumentGroupManageActivity");
        bundle.putBoolean("GroupDocumentValid", valid);
        bundle.putSerializable(GTABundle.GROUP_DOCUMENT_PDF, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.GROUP_DOCUMENT_PDF_CODE);
    }

    public void onClickImage(DocumentDTO documentDTO) {
        Intent intent = new Intent(DocumentGroupManageActivity.this, ImageOpenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "DocumentGroupManageActivity");
        bundle.putBoolean("GroupDocumentValid", valid);
        bundle.putSerializable(GTABundle.GROUP_DOCUMENT_IMAGE, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.GROUP_DOCUMENT_IMAGE_CODE);
    }


    private void getData() {
        mPrintAllGroupDocumentPresenter = new PrintAllGroupDocumentPresenter(DocumentGroupManageActivity.this, this);
        mPrintAllGroupDocumentPresenter.PrintAllGroupDocument(groupId);
    }


    private void showDocumentType() {
        final Dialog dialog = new Dialog(DocumentGroupManageActivity.this);
        dialog.setContentView(R.layout.dialog_menu_document_group);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtCamera, txtImageDevice, txtPdf, txtGroupEmail, txtGroupMailLink;
        LinearLayout lnlGroupMail, lnlEmail;
        Button btnGroupEmailCopy;
        txtCamera = dialog.findViewById(R.id.txtDocumentGroupCamera);
        txtImageDevice = dialog.findViewById(R.id.txtDocumentGroupFromDevice);
        txtPdf = dialog.findViewById(R.id.txtDocumentGroupPDF);
        lnlEmail = dialog.findViewById(R.id.lnlEmail);
        txtGroupEmail = dialog.findViewById(R.id.txtGroupEmail);
        lnlGroupMail = dialog.findViewById(R.id.lnlGroupMail);
        txtGroupMailLink = dialog.findViewById(R.id.txtGroupMailLink);
        btnGroupEmailCopy = dialog.findViewById(R.id.btnGroupEmailCopy);
        lnlGroupMail.setVisibility(View.GONE);
        lnlEmail.setVisibility(View.GONE);
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    dialog.dismiss();
                    checker = "camera";
                    if (checker.equals("camera")) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "NewPic");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
                        uriImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
                        startActivityForResult(intent, CAPTURE_IMAGE);
                    }
                } else if (!checkPermission()) {
                    requestPermission();
                }

            }
        });

        txtGroupEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "camera";
                lnlGroupMail.setVisibility(View.VISIBLE);
                txtGroupMailLink.setText("gta-uploadservice-group-" + groupId + "@areyousure.xyz");
                btnGroupEmailCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        String txtCopy = txtGroupMailLink.getText().toString();
                        ClipData clip = ClipData.newPlainText("text", txtCopy);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(DocumentGroupManageActivity.this, "Copy", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });

        txtImageDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    dialog.dismiss();
                    checker = "image";
                    if (checker.equals("image")) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

                    }
                } else if (!checkPermission()) {
                    requestPermission();
                }
            }
        });

        txtPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    dialog.dismiss();
                    checker = "pdf";
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    contentType = "application/pdf";
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                } else if (!checkPermission()) {
                    requestPermission();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                boolean check = true;
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    check = HandleFile.checkFileUpload(DocumentGroupManageActivity.this, data.getClipData().getItemAt(i).getUri());
                    if (check == false){
                        break;
                    }
                }
                if (check == true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure to upload this file?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final KProgressHUD khub = KProgressHUDManager.showProgressBar(DocumentGroupManageActivity.this);
                            int totalItemsSelected = data.getClipData().getItemCount();
                            for (int i = 0; i < totalItemsSelected; i++) {
                                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                ContentResolver cR = DocumentGroupManageActivity.this.getContentResolver();
                                contentType = cR.getType(fileUri);
                                String fileName = Instant.now().toString()
                                        + "-"
                                        + Thread.currentThread().getId()
                                        + "-"
                                        + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                                StorageReference fileToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName);
                                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                txtUri = uri.toString();
                                                createGroupDocument(txtUri);
                                            }
                                        });
                                        Toast.makeText(DocumentGroupManageActivity.this, "Uploaded " + " " + totalItemsSelected + " " + "File Succesfully", Toast.LENGTH_SHORT).show();
                                        KProgressHUDManager.dismiss(DocumentGroupManageActivity.this, khub);

                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    Toast.makeText(this, "File upload larger than 15mbs.", Toast.LENGTH_SHORT).show();
                }


            } else if (data.getData() != null) {
                boolean check = true;
                    check = HandleFile.checkFileUpload(DocumentGroupManageActivity.this, data.getData());
                if (check == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure to upload this file?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final KProgressHUD khub = KProgressHUDManager.showProgressBar(DocumentGroupManageActivity.this);
                            Uri uri = data.getData();
                            ContentResolver cR = DocumentGroupManageActivity.this.getContentResolver();
                            contentType = cR.getType(uri);
                            String fileName = Instant.now().toString()
                                    + "-"
                                    + Thread.currentThread().getId()
                                    + "-"
                                    + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                            StorageReference fileToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName);
                            fileToUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            txtUri = uri.toString();
                                            createGroupDocument(txtUri);
                                        }
                                    });
                                    Toast.makeText(DocumentGroupManageActivity.this, "Uploaded 1 File Succesfully", Toast.LENGTH_SHORT).show();
                                    KProgressHUDManager.dismiss(DocumentGroupManageActivity.this, khub);
                                }
                            });

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    Toast.makeText(this, "File upload larger than 15mbs.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            boolean check = true;
            check = HandleFile.checkFileUpload(DocumentGroupManageActivity.this, uriImage);
            if (check == true) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure to upload this file?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final KProgressHUD khub = KProgressHUDManager.showProgressBar(DocumentGroupManageActivity.this);
                        contentType = "image/jpeg";
                        String fileName = Instant.now().toString()
                                + "-"
                                + Thread.currentThread().getId()
                                + "-"
                                + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                        StorageReference fileToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName);
                        //StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
                        fileToUpload.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        txtUri = uri.toString();
                                        createGroupDocument(txtUri);
                                    }
                                });
                                Toast.makeText(DocumentGroupManageActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                KProgressHUDManager.dismiss(DocumentGroupManageActivity.this, khub);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DocumentGroupManageActivity.this, "failed", Toast.LENGTH_LONG).show();
                                KProgressHUDManager.dismiss(DocumentGroupManageActivity.this, khub);
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else {
                Toast.makeText(this, "File upload larger than 15mbs.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPrintAllGroupDocumentPresenter.PrintAllGroupDocument(groupId);
    }

    public void createGroupDocument(String txtUri) {
        DocumentDTO dto = new DocumentDTO();
        dto.setContentType(contentType);
        dto.setUri(txtUri);
        mCreateGroupDocumentPresenter = new CreateGroupDocumentPresenter(DocumentGroupManageActivity.this, this);
        mCreateGroupDocumentPresenter.createGroupDocument(groupId, dto);
    }


    @Override
    public void createGroupDocumentSuccess(String messageSuccess) {
        mPrintAllGroupDocumentPresenter = new PrintAllGroupDocumentPresenter(DocumentGroupManageActivity.this, this);
        mPrintAllGroupDocumentPresenter.PrintAllGroupDocument(groupId);
    }

    @Override
    public void createGroupDocumentFail(String messageFail) {

    }

    @Override
    public void printAllGroupDocumentSuccess(List<DocumentDTO> documentDTOList) {
        mDocumentDTOList = new ArrayList<>();
        mDocumentDTOList = documentDTOList;
        updateUI();
    }

    @Override
    public void printAllGroupDocumentFail(String messageFail) {

    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        memberList = new ArrayList<>();
        memberList = memberDTOList;
        String idFribase;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        idFribase = user.getUid();

        for (MemberDTO memberDTO : memberList) {
            int idRole = memberDTO.getIdRole();
            if (idFribase.equals(memberDTO.getPerson().getFirebaseUid()) && idRole == MemberRole.ADMIN) {
                valid = true;
                break;
            }
        }
    }

    @Override
    public void PrintMemberFail(String message) {

    }
}
