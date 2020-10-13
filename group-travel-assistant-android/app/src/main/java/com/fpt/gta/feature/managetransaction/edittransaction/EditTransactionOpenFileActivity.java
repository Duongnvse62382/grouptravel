package com.fpt.gta.feature.managetransaction.edittransaction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.presenter.DeleteGroupDocumentPresenter;
import com.fpt.gta.util.GTABundle;

public class EditTransactionOpenFileActivity extends AppCompatActivity {
    private DocumentDTO documentDTO;
    private ImageView imgDocumentImage, imgDeleteImage;
    private DeleteGroupDocumentPresenter mDeleteGroupDocumentPresenter;
    private Integer idDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction_open_file);
        initView();
        initData();
    }

    private void initData() {
        Glide.with(EditTransactionOpenFileActivity.this).load(documentDTO.getDownloadThumbnailUrl()).into(imgDocumentImage);
        imgDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteImage();
            }
        });
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        documentDTO = (DocumentDTO) bundle.getSerializable(GTABundle.ADD_TRANSACTION_DOCUMENT_IMAGE);
        imgDocumentImage = findViewById(R.id.imgEditOpenFileTransacionDocument);
        imgDeleteImage = findViewById(R.id.imgEditDeleteTransacionDocument);
    }


    public void clickDeleteImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this Image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.ADD_TRANSACTION_DOCUMENT_IMAGE, documentDTO);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
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

}
