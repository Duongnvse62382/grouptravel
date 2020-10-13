package com.fpt.gta.feature.managetransaction.addtransaction.OCRTransaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.OcrDTO;
import com.fpt.gta.data.dto.SubsetSum;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.feature.managetransaction.addtransaction.AddTransactionActivity;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.InternetHelper;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

public class OcrTransactionActivity extends AppCompatActivity {
    private Uri uriImage;
    private ImageView imgTransactionRecognize, imgRetakePicture, imgBack;
    public final int REQUEST_CAPTURE_IMAGE = 93;
    public final int OCR_CODE = 98;
    private RecyclerView rcvRecognizeResult;
    private OcrTransactionAdapter mOcrTransactionAdapter;
    private int numberOfColumn = 4;
    private int groupId;
    private StorageReference mStorage;
    private DocumentDTO documentDTO;
    private Button btnOcrToTransaction;
    private List<BigDecimal> bigDecimalList;
    private List<Line> moneyLines;
    private List<BigDecimal> bigDecimalArrayList = new ArrayList<>();
    private List<BigDecimal> withoutDuplicatedList = new ArrayList<>();
    private OcrDTO ocrDTO;
    private TextView txtMaybeTotal;
    private LinearLayout lnlMaybeTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_transaction);
        initView();
        checkInternet();
    }

    public void checkInternet() {
        if (InternetHelper.isOnline(OcrTransactionActivity.this) == false) {
            DialogShowErrorMessage.showDialogNoInternet(this, "No Internetion");
        } else {
            initData();
        }
    }

    public void initView() {
        rcvRecognizeResult = findViewById(R.id.rcvRecognizeResult);
        btnOcrToTransaction = findViewById(R.id.btnOcrToTransaction);
        imgTransactionRecognize = findViewById(R.id.imgTransactionRecognize);
        imgBack = findViewById(R.id.imgOcrBack);
        txtMaybeTotal = findViewById(R.id.txtMaybeTotal);
        lnlMaybeTotal = findViewById(R.id.lnlMaybeTotal);
        lnlMaybeTotal.setVisibility(View.GONE);
        imgRetakePicture = findViewById(R.id.imgRetakePicture);
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public void initData() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        groupId = SharePreferenceUtils.getIntSharedPreference(OcrTransactionActivity.this, GTABundle.IDGROUP);
        rcvRecognizeResult.setLayoutManager(new GridLayoutManager(this, numberOfColumn));
        pickCamera();
        imgRetakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigDecimalArrayList = new ArrayList<>();
                withoutDuplicatedList = new ArrayList<>();
                pickCamera();
            }
        });
        btnOcrToTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OcrTransactionActivity.this, AddTransactionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void pickCamera() {
        bigDecimalArrayList = new ArrayList<>();
        withoutDuplicatedList = new ArrayList<>();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        uriImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    private void updateUI() {
        if (mOcrTransactionAdapter == null) {
            mOcrTransactionAdapter = new OcrTransactionAdapter(withoutDuplicatedList, OcrTransactionActivity.this);
            rcvRecognizeResult.setAdapter(mOcrTransactionAdapter);
            mOcrTransactionAdapter.setOnItemClickListener(new OcrTransactionAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(BigDecimal line, int position) {
                    String amountBill = line.toString();
                    Intent intent = new Intent(OcrTransactionActivity.this, AddTransactionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Calling_OcrTransaction", "AddOcrTransaction");
                    bundle.putString("OcrBill", amountBill);
                    bundle.putSerializable("OcrDocument", documentDTO);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, OCR_CODE);
                }
            });
        } else {
            mOcrTransactionAdapter.notifyDataChange(withoutDuplicatedList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAPTURE_IMAGE:
                    if (resultCode == RESULT_OK) {
                        imgTransactionRecognize.setImageURI(uriImage);
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) (imgTransactionRecognize).getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                        if (!recognizer.isOperational()) {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        } else {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = recognizer.detect(frame);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock myItems = items.valueAt(i);
                                List<Line> lines = (List<Line>) myItems.getComponents();
                                for (int j = 0; j < lines.size(); j++) {
                                    try {
                                        Log.e("Result", lines.get(j).getValue().toString());
                                        String lineString = CharMatcher.inRange('0', '9').retainFrom(lines.get(j).getValue());
                                        bigDecimalArrayList.add(new BigDecimal(Double.parseDouble(lineString)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (bigDecimalArrayList.size() != 0) {
                            withoutDuplicatedList = OcrHandler.prepareLines(bigDecimalArrayList);
                            List<OcrDTO> ocrFindHiggestCourseList = new ArrayList<>();
                            if (withoutDuplicatedList.size()!=0) {
                                ocrFindHiggestCourseList = OcrHandler.findOutTotalNumber(withoutDuplicatedList, bigDecimalArrayList);
                            }else {
                                Toast.makeText(this, "Invalid Bill", Toast.LENGTH_SHORT).show();
                            }
                            int flag = 0;
                            if (ocrFindHiggestCourseList.size() != 0) {
                                List<BigDecimal> listToCalculateSubsetSum = OcrHandler.getListToCalculateSubsetSum(bigDecimalArrayList, ocrFindHiggestCourseList);
                                if (listToCalculateSubsetSum.size() != 0) {
                                    HashSet<SubsetSum> subsetHashSet = new HashSet<SubsetSum>();
                                    OcrHandler.subsetSums(listToCalculateSubsetSum, 0, listToCalculateSubsetSum.size() - 1, BigDecimal.ZERO, 0, subsetHashSet);

                                    for (OcrDTO dto : ocrFindHiggestCourseList) {
                                        if (subsetHashSet.contains(new SubsetSum(dto.getBigDecimal()))) {
                                            dto.setCount(dto.getCount() + 10);
                                        }
                                    }

                                    ocrFindHiggestCourseList.sort((o1, o2) -> {
                                        return o2.getBigDecimal().compareTo(o1.getBigDecimal());
                                    });

                                    ocrDTO = new OcrDTO();
                                    ocrDTO = ocrFindHiggestCourseList.get(0);
                                    for (int k = 1; k < ocrFindHiggestCourseList.size(); k++) {
                                        if (ocrFindHiggestCourseList.get(k).getCount() > ocrDTO.getCount()) {
                                            ocrDTO = ocrFindHiggestCourseList.get(k);
                                        }
                                    }
                                    if (ocrDTO.getBigDecimal().compareTo(BigDecimal.ZERO) > 0) {
                                        lnlMaybeTotal.setVisibility(View.VISIBLE);
                                        String amountBill = ocrDTO.getBigDecimal().toString().replaceAll("[,.]", "");
                                        txtMaybeTotal.setText(ChangeValue.formatOcrMoney(BigDecimal.valueOf(Double.valueOf(amountBill))));
                                        lnlMaybeTotal.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(OcrTransactionActivity.this, AddTransactionActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("Calling_OcrTransaction", "AddOcrTransaction");
                                                bundle.putString("OcrBill", amountBill);
//                                                bundle.putBoolean();
                                                bundle.putSerializable("OcrDocument", documentDTO);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtras(bundle);
                                                startActivityForResult(intent, OCR_CODE);
                                            }
                                        });
                                    }else {
                                        Toast.makeText(this, "Not Found Total Bill", Toast.LENGTH_SHORT).show();
                                    }
                                    String fileName = Instant.now().toString()
                                            + "-"
                                            + Thread.currentThread().getId()
                                            + "-"
                                            + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                                    final KProgressHUD khub = KProgressHUDManager.showProgressBar(OcrTransactionActivity.this);
                                    StorageReference fileToUpload = mStorage.child("temp/").child(String.valueOf(groupId)).child(fileName);
                                    fileToUpload.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    documentDTO = new DocumentDTO();
                                                    documentDTO.setUri(uri.toString());
                                                    documentDTO.setDownloadThumbnailUrl(uri.toString());
                                                    documentDTO.setContentType("image/jpeg");
                                                    documentDTO.setDownloadUrl(uri.toString());
                                                }
                                            });
                                            Toast.makeText(OcrTransactionActivity.this, "Detect Success", Toast.LENGTH_SHORT).show();
                                            updateUI();
                                            KProgressHUDManager.dismiss(OcrTransactionActivity.this, khub);
                                        }
                                    });
                                }else {
                                    Toast.makeText(this, "Invalid Bill", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(this, "Invalid Bill", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (withoutDuplicatedList.size() != 0) {
                                mOcrTransactionAdapter.notifyDataChange(withoutDuplicatedList);
                                lnlMaybeTotal.setVisibility(View.GONE);
                                Toast.makeText(this, "Invalid Bill", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Invalid Bill", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                    break;
                case OCR_CODE:
                    finish();
                    break;
            }
        }
    }

}