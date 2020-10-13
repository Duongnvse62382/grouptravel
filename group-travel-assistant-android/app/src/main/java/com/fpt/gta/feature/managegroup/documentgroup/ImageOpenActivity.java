package com.fpt.gta.feature.managegroup.documentgroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.presenter.DeleteDocumentActivityPresenter;
import com.fpt.gta.presenter.DeleteGroupDocumentPresenter;
import com.fpt.gta.presenter.DeleteTransactionDocumentPresenter;
import com.fpt.gta.util.DocumentImageUtil;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.view.DeleteDocumentActivityView;
import com.fpt.gta.view.DeleteGroupDocumentView;
import com.fpt.gta.view.DeleteTransactionDocumentView;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageOpenActivity extends AppCompatActivity implements DeleteGroupDocumentView, DeleteTransactionDocumentView, DeleteDocumentActivityView {
    private DocumentDTO documentDTO;
    private ImageView imgDocumentImage, imgDeleteImage, imgOpenImageBack;
    private DeleteGroupDocumentPresenter mDeleteGroupDocumentPresenter;
    private Integer idDocument;
    private String CALLING_ACTIVITY;
    private DeleteTransactionDocumentPresenter mDeleteTransactionDocumentPresenter;
    private DeleteDocumentActivityPresenter mDeleteDocumentActivityPresenter;
    private boolean valid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_open);
        initView();
        initData();
    }

    private void initData() {
        imgDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteImage();
            }
        });

        imgOpenImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        imgDocumentImage = findViewById(R.id.imgDocumentImage);
        imgOpenImageBack = findViewById(R.id.imgOpenImageBack);
        imgDeleteImage = findViewById(R.id.imgDeleteImage);
        final KProgressHUD khub = KProgressHUDManager.showProgressBar(ImageOpenActivity.this);
        Bundle bundle = getIntent().getExtras();
        CALLING_ACTIVITY = (String) bundle.get("CALLING_ACTIVITY");
        if (bundle.containsKey("DocumentImage")) {
            documentDTO = (DocumentDTO) bundle.getSerializable("DocumentImage");
        } else if (bundle.containsKey(GTABundle.TransactionDetailsDocumentImage)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.TransactionDetailsDocumentImage);
            valid = bundle.getBoolean("valid");
            if (valid == false){
                imgDeleteImage.setVisibility(View.GONE);
            }else {
                imgDeleteImage.setVisibility(View.VISIBLE);
            }
        }else if (bundle.containsKey(GTABundle.GROUP_DOCUMENT_IMAGE)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.GROUP_DOCUMENT_IMAGE);
            valid = bundle.getBoolean("GroupDocumentValid");
            if (valid == false){
                imgDeleteImage.setVisibility(View.GONE);
            }else {
                imgDeleteImage.setVisibility(View.VISIBLE);
            }
        }else if (bundle.containsKey(GTABundle.ADD_TRANSACTION_DOCUMENT_IMAGE)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.ADD_TRANSACTION_DOCUMENT_IMAGE);
        }else if (bundle.containsKey(GTABundle.UPDATE_TRANSACTION_DOCUMENT_IMAGE)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.UPDATE_TRANSACTION_DOCUMENT_IMAGE);
        }else if (bundle.containsKey(GTABundle.DELETE_ACTIVITY_DOCUMENT_IMAGE)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.DELETE_ACTIVITY_DOCUMENT_IMAGE);
        }else if (bundle.containsKey(GTABundle.ACTIVITY_DETAIL_DOCUMENT_IMAGE)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.ACTIVITY_DETAIL_DOCUMENT_IMAGE);
            valid = bundle.getBoolean("ActivityValid");
            if (valid == false){
                imgDeleteImage.setVisibility(View.GONE);
            }else {
                imgDeleteImage.setVisibility(View.VISIBLE);
            }
        }
        idDocument = documentDTO.getId();
        DocumentImageUtil.loadImage(ImageOpenActivity.this, documentDTO, imgDocumentImage);
        KProgressHUDManager.dismiss(ImageOpenActivity.this, khub);
    }



    public void clickDeleteImage() {
        if (CALLING_ACTIVITY.equals("TransactionDetailsActivity")) {
            clickDeleteDetailsImage();
        } else if (CALLING_ACTIVITY.equals("DocumentGroupManageActivity")) {
            clickDeleteGroupImage();
        } else if (CALLING_ACTIVITY.equals("AddTransactionDocumentActivity")) {
            clickDeleteAddTransactionImage();
        }else if (CALLING_ACTIVITY.equals("EditTransactionDocumentActivity")) {
            clickDeleteEditTransactionImage();
        }else if (CALLING_ACTIVITY.equals("ActivityDocumentActivity")) {
            clickDeleteEditDocumentActivityImage();
        }else if (CALLING_ACTIVITY.equals("ActivityPlanDetailActivity")) {
            clickDeleteEditDocumentActivityDetailsImage();
        }

    }

    private void clickDeleteEditTransactionImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this Image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.UPDATE_TRANSACTION_DOCUMENT_IMAGE, (Serializable) documentDTO);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
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
    }

    private void clickDeleteEditDocumentActivityImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this Image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.DELETE_ACTIVITY_DOCUMENT_IMAGE, (Serializable) documentDTO);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
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
    }

    private void clickDeleteEditDocumentActivityDetailsImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this Image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteDocumentActivityPresenter = new DeleteDocumentActivityPresenter(ImageOpenActivity.this, ImageOpenActivity.this);
                mDeleteDocumentActivityPresenter.deleteDocumentActivity(idDocument);
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
    }

    private void clickDeleteAddTransactionImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this Image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.ADD_TRANSACTION_DOCUMENT_IMAGE, (Serializable) documentDTO);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
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
    }

    public void clickDeleteDetailsImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this Image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    mDeleteTransactionDocumentPresenter = new DeleteTransactionDocumentPresenter(ImageOpenActivity.this, ImageOpenActivity.this);
                    mDeleteTransactionDocumentPresenter.deleteTransactionDocument(idDocument);

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
    }
    public void clickDeleteGroupImage(){
        mDeleteGroupDocumentPresenter = new DeleteGroupDocumentPresenter(ImageOpenActivity.this, ImageOpenActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this Image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteGroupDocumentPresenter.deleteGroupDocument(idDocument);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void deleteGroupDocumentSuccess(String messageSuccess) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.GROUP_DOCUMENT_IMAGE, (Serializable) documentDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void deleteGroupDocumentFail(String messageFail) {
        Toast.makeText(this, "Document is already used", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteTransactionDocumentSuccess(String message) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.TransactionDetailsDocumentImage, (Serializable) documentDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void deleteTransactionDocumentFail(String message) {

    }

    @Override
    public void deleteDocumentActivitySuccess(String messageSuccess) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ACTIVITY_DETAIL_DOCUMENT_IMAGE, (Serializable) documentDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void deleteDocumentActivityFail(String messageFail) {

    }
}