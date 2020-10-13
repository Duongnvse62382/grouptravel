package com.fpt.gta.feature.managegroup.documentgroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.presenter.DeleteDocumentActivityPresenter;
import com.fpt.gta.presenter.DeleteGroupDocumentPresenter;
import com.fpt.gta.presenter.DeleteTransactionDocumentPresenter;
import com.fpt.gta.util.DocumentImageUtil;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.view.DeleteDocumentActivityView;
import com.fpt.gta.view.DeleteGroupDocumentView;
import com.fpt.gta.view.DeleteTransactionDocumentView;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.Serializable;

public class PdfViewActivity extends AppCompatActivity implements DeleteGroupDocumentView, DeleteTransactionDocumentView, DeleteDocumentActivityView {
    private PDFView pdfViewDocument;
    private DocumentDTO documentDTO;
    private ImageView imgDeletePDF, imgOpenPDFBack;
    private String CALLING_ACTIVITY;
    private DeleteGroupDocumentPresenter mDeleteGroupDocumentPresenter;
    private DeleteTransactionDocumentPresenter mDeleteTransactionDocumentPresenter;
    private DeleteDocumentActivityPresenter mDeleteDocumentActivityPresenter;
    private Integer idDocument;
    private boolean valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        initView();
        initData();
    }

    private void initView() {
        imgDeletePDF = findViewById(R.id.imgDeletePDF);
        imgOpenPDFBack = findViewById(R.id.imgOpenPDFBack);
        pdfViewDocument = (PDFView) findViewById(R.id.pdfViewDocument);
        Bundle bundle = getIntent().getExtras();
        CALLING_ACTIVITY = (String) bundle.get("CALLING_ACTIVITY");
        if (bundle.containsKey("DocumentPDF")) {
            documentDTO = (DocumentDTO) bundle.getSerializable("DocumentPDF");
        } else if (bundle.containsKey(GTABundle.TransactionDetailsDocumentPDF)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.TransactionDetailsDocumentPDF);
            valid = bundle.getBoolean("valid");
            if (valid == false) {
                imgDeletePDF.setVisibility(View.GONE);
            } else {
                imgDeletePDF.setVisibility(View.VISIBLE);
            }
        } else if (bundle.containsKey("UPDATE_DOCUMENT_PDF_TRANSACTION")) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.UPDATE_DOCUMENT_PDF_TRANSACTION);
        } else if (bundle.containsKey(GTABundle.GROUP_DOCUMENT_PDF)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.GROUP_DOCUMENT_PDF);
            valid = bundle.getBoolean("GroupDocumentValid");
            if (valid == false) {
                imgDeletePDF.setVisibility(View.GONE);
            } else {
                imgDeletePDF.setVisibility(View.VISIBLE);
            }
        } else if (bundle.containsKey(GTABundle.UPDATE_TRANSACTION_DOCUMENT_PDF)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.UPDATE_TRANSACTION_DOCUMENT_PDF);
        } else if (bundle.containsKey(GTABundle.DELETE_ACTIVITY_DOCUMENT_PDF)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.DELETE_ACTIVITY_DOCUMENT_PDF);
        } else if (bundle.containsKey(GTABundle.ACTIVITY_DETAIL_DOCUMENT_PDF)) {
            documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.ACTIVITY_DETAIL_DOCUMENT_PDF);
            valid = bundle.getBoolean("ActivityValid");
            if (valid == false) {
                imgDeletePDF.setVisibility(View.GONE);
            } else {
                imgDeletePDF.setVisibility(View.VISIBLE);
            }
        }
        idDocument = documentDTO.getId();
    }

    private void initData() {
        DocumentImageUtil.loadPdf(documentDTO, pdfViewDocument);
        imgDeletePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeletePDF();
            }
        });

        imgOpenPDFBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void clickDeletePDF() {
        if (CALLING_ACTIVITY.equals("TransactionDetailsActivity")) {
            clickDeleteDetailsPDF();
        } else if (CALLING_ACTIVITY.equals("DocumentGroupManageActivity")) {
            clickDeleteGroupPDF();
        } else if (CALLING_ACTIVITY.equals("AddTransactionDocumentActivity")) {
            clickDeleteAddTransactionPDF();
        } else if (CALLING_ACTIVITY.equals("EditTransactionDocumentActivity")) {
            clickDeleteEditTransactionPDF();
        } else if (CALLING_ACTIVITY.equals("ActivityDocumentActivity")) {
            clickDeleteEditActivityPDF();
        } else if (CALLING_ACTIVITY.equals("ActivityPlanDetailActivity")) {
            clickDeleteEditActivityDetailsPDF();
        }

    }

    private void clickDeleteEditTransactionPDF() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this PDF?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.UPDATE_TRANSACTION_DOCUMENT_PDF, (Serializable) documentDTO);
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

    private void clickDeleteEditActivityDetailsPDF() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this PDF?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteDocumentActivityPresenter = new DeleteDocumentActivityPresenter(PdfViewActivity.this, PdfViewActivity.this);
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

    private void clickDeleteEditActivityPDF() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this PDF?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.DELETE_ACTIVITY_DOCUMENT_PDF, (Serializable) documentDTO);
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

    public void clickDeleteDetailsPDF() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this PDF?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteTransactionDocumentPresenter = new DeleteTransactionDocumentPresenter(PdfViewActivity.this, PdfViewActivity.this);
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

    public void clickDeleteGroupPDF() {
        mDeleteGroupDocumentPresenter = new DeleteGroupDocumentPresenter(PdfViewActivity.this, PdfViewActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this PDF?");
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


    public void clickDeleteAddTransactionPDF() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this PDF?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.UPDATE_DOCUMENT_PDF_TRANSACTION, (Serializable) documentDTO);
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

    @Override
    public void deleteGroupDocumentSuccess(String messageSuccess) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.GROUP_DOCUMENT_PDF, (Serializable) documentDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void deleteGroupDocumentFail(String messageFail) {

    }

    @Override
    public void deleteTransactionDocumentSuccess(String message) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.TransactionDetailsDocumentPDF, (Serializable) documentDTO);
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
        bundle.putSerializable(GTABundle.ACTIVITY_DETAIL_DOCUMENT_PDF, (Serializable) documentDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void deleteDocumentActivityFail(String messageFail) {

    }
}