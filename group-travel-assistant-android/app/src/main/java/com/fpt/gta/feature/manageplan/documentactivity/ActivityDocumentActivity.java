package com.fpt.gta.feature.manageplan.documentactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.gta.MainActivity;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.feature.managegroup.documentgroup.ImageOpenActivity;
import com.fpt.gta.feature.managegroup.documentgroup.PdfViewActivity;
import com.fpt.gta.feature.managetransaction.edittransaction.EditTransactionDocumentActivity;
import com.fpt.gta.feature.managetransaction.transactiondocument.TransactionDocumentAdapter;
import com.fpt.gta.presenter.CreateGroupDocumentPresenter;
import com.fpt.gta.util.DocumentImageUtil;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.HandleFile;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActivityDocumentActivity extends AppCompatActivity {
    private int groupId;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageButton mSelectBtn, imgSaveActivityDocument;
    private RecyclerView rcvDocumentList;
    private Bitmap photo;
    private CreateGroupDocumentPresenter mCreateGroupDocumentPresenter;
    final private int PERMISSION_REQUEST_CODE = 200;
    final private int CAPTURE_IMAGE = 100;
    private DocumentDTO documentDTO;
    private List<DocumentDTO> mDocumentDTOList;
    private int numberOfColumn = 3;
    private String contentType, txtUri;
    private TransactionDocumentAdapter transactionDocumentAdapter;
    private StorageReference mStorage;
    private String checker = "";
    private String CALLING_ACTIVITY;
    private Uri uriImage;
    private ImageView imgActivityDocumentBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (CALLING_ACTIVITY.equals("AddActivityDayActivity")) {
            onclickSaveAdd();
        } else if (CALLING_ACTIVITY.equals("UpdateActivityDayActivity")) {
            onclickSaveUpdate();
        }
    }

    private void initView() {
        groupId = SharePreferenceUtils.getIntSharedPreference(ActivityDocumentActivity.this, GTABundle.IDGROUP);
        Bundle bundle = getIntent().getExtras();
        CALLING_ACTIVITY = (String) bundle.get("CALLING_ACTIVITY");
        if (bundle.containsKey(GTABundle.UPDATE_ACTIVITY_DOCUMENT)) {
            mDocumentDTOList = (List<DocumentDTO>) bundle.getSerializable(GTABundle.UPDATE_ACTIVITY_DOCUMENT);
        } else if (bundle.containsKey(GTABundle.ADD_ACTIVITY_DOCUMENT)) {
            mDocumentDTOList = (List<DocumentDTO>) bundle.getSerializable(GTABundle.ADD_ACTIVITY_DOCUMENT);
        }

        mStorage = FirebaseStorage.getInstance().

                getReference();

        mSelectBtn = (ImageButton)

                findViewById(R.id.img_add_activity_document);
        imgActivityDocumentBack= findViewById(R.id.imgActivityDocumentBack);

        imgSaveActivityDocument = (ImageButton)

                findViewById(R.id.imgSaveActivityDocument);

        rcvDocumentList = (RecyclerView)

                findViewById(R.id.rcvUploadActivityDocument);
        //RecyclerView
        rcvDocumentList.setLayoutManager(new
                GridLayoutManager(this, numberOfColumn));

        updateUI();

    }

    private void initData() {

        imgActivityDocumentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CALLING_ACTIVITY.equals("AddActivityDayActivity")) {
                    onclickSaveAdd();
                } else if (CALLING_ACTIVITY.equals("UpdateActivityDayActivity")) {
                    onclickSaveUpdate();
                }
            }
        });
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionDocumentType();
            }
        });

        imgSaveActivityDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CALLING_ACTIVITY.equals("AddActivityDayActivity")) {
                    onclickSaveAdd();
                } else if (CALLING_ACTIVITY.equals("UpdateActivityDayActivity")) {
                    onclickSaveUpdate();
                }
            }
        });
    }


    public void onclickSaveAdd() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ADD_ACTIVITY_DOCUMENT, (Serializable) mDocumentDTOList);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onclickSaveUpdate() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DOCUMENT, (Serializable) mDocumentDTOList);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
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
        new AlertDialog.Builder(ActivityDocumentActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showTransactionDocumentType() {
        final Dialog dialog = new Dialog(ActivityDocumentActivity.this);
        dialog.setContentView(R.layout.dialog_menu_document);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtCamera, txtImageDevice, txtPdf, txtFromGroupDocument;
        txtCamera = dialog.findViewById(R.id.txtCamera);
        txtImageDevice = dialog.findViewById(R.id.txtImageFromDevice);
        txtPdf = dialog.findViewById(R.id.txtPDF);
        txtFromGroupDocument = dialog.findViewById(R.id.txtFromGroupDocument);


        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    dialog.dismiss();
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "NewPic");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
                    uriImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
                    startActivityForResult(intent, CAPTURE_IMAGE);
                } else if (!checkPermission()) {
                    requestPermission();
                }
            }

        });

        txtFromGroupDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    dialog.dismiss();
                    checker = "group";
                    if (checker.equals("group")) {
                        Intent intent = new Intent(ActivityDocumentActivity.this, DocumenGroupMergeToActivity.class);
                        Bundle bundle = new Bundle();
//                    bundle.putString("CALLING_ACTIVITY", "ActivityDocumentActivity");
                        bundle.putSerializable(GTABundle.ADD_DOCUMENT_GROUP_TO_ACTIVITY, (Serializable) mDocumentDTOList);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, GTABundle.ADD_DOCUMENT_GROUP_TO_ACTIVITY_CODE);
                    }
                } else if (!checkPermission()) {
                    requestPermission();
                }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                boolean check = true;
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    check = HandleFile.checkFileUpload(ActivityDocumentActivity.this, data.getClipData().getItemAt(i).getUri());
                    if (check == false) {
                        break;
                    }
                }
                if (check == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure to upload this file?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final KProgressHUD khub = KProgressHUDManager.showProgressBar(ActivityDocumentActivity.this);
                            int totalItemsSelected = data.getClipData().getItemCount();
                            for (int i = 0; i < totalItemsSelected; i++) {
                                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                ContentResolver cR = ActivityDocumentActivity.this.getContentResolver();
                                contentType = cR.getType(fileUri);
                                String fileName = Instant.now().toString()
                                        + "-"
                                        + Thread.currentThread().getId()
                                        + "-"
                                        + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                                StorageReference fileToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName);
                                StorageReference thumbnailToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName + "-thumbnail");
                                AtomicInteger atomicInteger = new AtomicInteger(0);
                                DocumentDTO documentDTO = new DocumentDTO();
                                documentDTO.setContentType(contentType);

                                if (contentType.equals("application/pdf")) {
                                    DocumentImageUtil
                                            .getPdfThumbnailBitmap(ActivityDocumentActivity.this, getContentResolver(), fileUri)
                                            .thenAccept(bitmap -> {
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                                byte[] imageBytes = baos.toByteArray();
                                                thumbnailToUpload.putBytes(imageBytes).addOnCompleteListener(putTask -> {
                                                    putTask.getResult().getStorage().getDownloadUrl().addOnCompleteListener(uriTask -> {
                                                        String downloadThumbnailUrl = uriTask.getResult().toString();
                                                        documentDTO.setDownloadThumbnailUrl(downloadThumbnailUrl);
                                                        if (atomicInteger.addAndGet(1) == 2) {
                                                            mDocumentDTOList.add(documentDTO);
                                                            updateUI();
                                                        }
                                                    });
                                                });
                                            });
                                }
                                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUrl = uri.toString();
                                                documentDTO.setDownloadUrl(downloadUrl);
                                                documentDTO.setUri(downloadUrl);
                                                if (!contentType.equals("application/pdf")) {
                                                    documentDTO.setDownloadThumbnailUrl(downloadUrl);
                                                }
                                                if (atomicInteger.addAndGet(1) == 2 || !contentType.equals("application/pdf")) {
                                                    mDocumentDTOList.add(documentDTO);
                                                    updateUI();
                                                }
                                            }
                                        });
                                        Toast.makeText(ActivityDocumentActivity.this, "Uploaded " + " " + totalItemsSelected + " " + "File Succesfully", Toast.LENGTH_SHORT).show();
                                        KProgressHUDManager.dismiss(ActivityDocumentActivity.this, khub);
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
                } else {
                    Toast.makeText(this, "File upload lager than 15mbs", Toast.LENGTH_SHORT).show();
                }

            } else if (data.getData() != null) {
                boolean check = true;
                check = HandleFile.checkFileUpload(ActivityDocumentActivity.this, data.getData());
                if (check == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure to upload this file?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final KProgressHUD khub = KProgressHUDManager.showProgressBar(ActivityDocumentActivity.this);
                            Uri uri = data.getData();
                            ContentResolver cR = ActivityDocumentActivity.this.getContentResolver();
                            contentType = cR.getType(uri);
                            String fileName = Instant.now().toString()
                                    + "-"
                                    + Thread.currentThread().getId()
                                    + "-"
                                    + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                            StorageReference fileToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName);
                            StorageReference thumbnailToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName + "-thumbnail");
                            AtomicInteger atomicInteger = new AtomicInteger(0);
                            DocumentDTO documentDTO = new DocumentDTO();
                            documentDTO.setContentType(contentType);

                            if (contentType.equals("application/pdf")) {
                                DocumentImageUtil
                                        .getPdfThumbnailBitmap(ActivityDocumentActivity.this, getContentResolver(), uri)
                                        .thenAccept(bitmap -> {
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                            byte[] imageBytes = baos.toByteArray();
                                            thumbnailToUpload.putBytes(imageBytes).addOnCompleteListener(putTask -> {
                                                putTask.getResult().getStorage().getDownloadUrl().addOnCompleteListener(uriTask -> {
                                                    String downloadThumbnailUrl = uriTask.getResult().toString();
                                                    documentDTO.setDownloadThumbnailUrl(downloadThumbnailUrl);
                                                    if (atomicInteger.addAndGet(1) == 2) {
                                                        mDocumentDTOList.add(documentDTO);
                                                        updateUI();
                                                    }
                                                });
                                            });
                                        });
                            }
                            fileToUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            documentDTO.setDownloadUrl(downloadUrl);
                                            documentDTO.setUri(downloadUrl);
                                            if (!contentType.equals("application/pdf")) {
                                                documentDTO.setDownloadThumbnailUrl(downloadUrl);
                                            }
                                            if (atomicInteger.addAndGet(1) == 2 || !contentType.equals("application/pdf")) {
                                                mDocumentDTOList.add(documentDTO);
                                                updateUI();
                                            }
                                        }
                                    });
                                    Toast.makeText(ActivityDocumentActivity.this, "Uploaded 1 File Succesfully", Toast.LENGTH_SHORT).show();
                                    KProgressHUDManager.dismiss(ActivityDocumentActivity.this, khub);
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
                } else {
                    Toast.makeText(this, "File upload lager than 15mbs", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            boolean check = true;
            check = HandleFile.checkFileUpload(ActivityDocumentActivity.this, uriImage);
            if (check == true) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure to upload this file?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final KProgressHUD khub = KProgressHUDManager.showProgressBar(ActivityDocumentActivity.this);
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
                                        DocumentDTO documentDTO = new DocumentDTO();
                                        documentDTO.setUri(txtUri);
                                        documentDTO.setDownloadThumbnailUrl(txtUri);
                                        documentDTO.setContentType(contentType);
                                        mDocumentDTOList.add(documentDTO);
                                        updateUI();
                                    }
                                });
                                Toast.makeText(ActivityDocumentActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                KProgressHUDManager.dismiss(ActivityDocumentActivity.this, khub);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ActivityDocumentActivity.this, "failed", Toast.LENGTH_LONG).show();
                                KProgressHUDManager.dismiss(ActivityDocumentActivity.this, khub);
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
            } else {
                Toast.makeText(this, "File upload lager than 15mbs", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GTABundle.DELETE_ACTIVITY_DOCUMENT_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                documentDTO = (DocumentDTO) data.getSerializableExtra(GTABundle.DELETE_ACTIVITY_DOCUMENT_IMAGE);
                mDocumentDTOList.removeIf(removingDocumentDTO -> removingDocumentDTO.getDownloadThumbnailUrl().compareTo(documentDTO.getDownloadThumbnailUrl()) == 0);
                updateUI();
            }
        } else if (requestCode == GTABundle.DELETE_ACTIVITY_DOCUMENT_PDF_CODE) {
            if (resultCode == RESULT_OK) {
                documentDTO = (DocumentDTO) data.getSerializableExtra(GTABundle.DELETE_ACTIVITY_DOCUMENT_PDF);
                mDocumentDTOList.removeIf(removingDocumentDTO -> removingDocumentDTO.getDownloadThumbnailUrl().compareTo(documentDTO.getDownloadThumbnailUrl()) == 0);
                updateUI();
            }
        } else if (requestCode == GTABundle.ADD_DOCUMENT_GROUP_TO_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                mDocumentDTOList = (List<DocumentDTO>) data.getSerializableExtra(GTABundle.ADD_DOCUMENT_GROUP_TO_ACTIVITY);
                updateUI();
            }
        }
    }

    private void updateUI() {
        if (transactionDocumentAdapter == null) {
            transactionDocumentAdapter = new TransactionDocumentAdapter(mDocumentDTOList, ActivityDocumentActivity.this);
            rcvDocumentList.setAdapter(transactionDocumentAdapter);
            transactionDocumentAdapter.setOnItemImageClickListener(new TransactionDocumentAdapter.OnItemImageClickListener() {
                @Override
                public void onItemImageClickListener(DocumentDTO documentDTO, int position) {
                    if (documentDTO.getContentType().equals("image/jpeg") || documentDTO.getContentType().equals("image/png")) {
                        onClickImage(documentDTO);
                    } else if (documentDTO.getContentType().equals("application/pdf")) {
                        onClickPDF(documentDTO);
                    }
                }
            });

        } else {
            transactionDocumentAdapter.notifyChangeData(mDocumentDTOList);
        }
    }

    public void onClickImage(DocumentDTO documentDTO) {
        Intent intent = new Intent(ActivityDocumentActivity.this, ImageOpenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "ActivityDocumentActivity");
        bundle.putSerializable(GTABundle.DELETE_ACTIVITY_DOCUMENT_IMAGE, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.DELETE_ACTIVITY_DOCUMENT_IMAGE_CODE);
    }

    public void onClickPDF(DocumentDTO documentDTO) {
        Intent intent = new Intent(ActivityDocumentActivity.this, PdfViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "ActivityDocumentActivity");
        bundle.putSerializable(GTABundle.DELETE_ACTIVITY_DOCUMENT_PDF, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.DELETE_ACTIVITY_DOCUMENT_PDF_CODE);
    }


}