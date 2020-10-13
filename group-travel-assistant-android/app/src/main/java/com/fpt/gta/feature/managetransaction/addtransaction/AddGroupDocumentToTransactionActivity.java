package com.fpt.gta.feature.managetransaction.addtransaction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.feature.managegroup.documentgroup.DocumentGroupAdapter;
import com.fpt.gta.presenter.PrintAllGroupDocumentPresenter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.PrintAllGroupDocumentView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddGroupDocumentToTransactionActivity extends AppCompatActivity implements PrintAllGroupDocumentView {
    private RecyclerView rcvAddDocumentGroupToTransaction;
    private int groupId;
    private List<DocumentDTO> mDocumentDTOList;
    private List<DocumentDTO> addDocumentDTOList;
    private int numberOfColumn = 3;
    private ImageView imgGroupDocumentToTransactionBack;
    private DocumentGroupAdapter documentGroupAdapter;
    private PrintAllGroupDocumentPresenter mPrintAllGroupDocumentPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_document_to_transaction);
        initView();
        initData();
    }

    private void initView() {
        groupId = SharePreferenceUtils.getIntSharedPreference(AddGroupDocumentToTransactionActivity.this, GTABundle.IDGROUP);
        Bundle bundle = getIntent().getExtras();
        addDocumentDTOList = (List<DocumentDTO>) bundle.getSerializable(GTABundle.ADD_TRANSACTION_DOCUMENT_GROUP);
        rcvAddDocumentGroupToTransaction = (RecyclerView) findViewById(R.id.rcvAddDocumentGroupToTransaction);
        imgGroupDocumentToTransactionBack = (ImageView) findViewById(R.id.imgGroupDocumentToTransactionBack);
        //RecyclerView
        rcvAddDocumentGroupToTransaction.setLayoutManager(new GridLayoutManager(this, numberOfColumn));

    }

    private void initData() {
        getData();
        imgGroupDocumentToTransactionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUI() {
        if (documentGroupAdapter == null) {
            documentGroupAdapter = new DocumentGroupAdapter(mDocumentDTOList, AddGroupDocumentToTransactionActivity.this);
            rcvAddDocumentGroupToTransaction.setAdapter(documentGroupAdapter);
            documentGroupAdapter.setOnItemImageClickListener(new DocumentGroupAdapter.OnItemImageClickListener() {
                @Override
                public void onItemImageClickListener(DocumentDTO documentDTO, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupDocumentToTransactionActivity.this);
                    builder.setMessage("Are you sure to upload this file to Transaction?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addDocumentDTOList.add(documentDTO);
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(GTABundle.ADD_TRANSACTION_DOCUMENT_GROUP, (Serializable) addDocumentDTOList);
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
            });

        } else {
            documentGroupAdapter.notifyChangeData(mDocumentDTOList);
        }
    }

    private void getData() {
        mPrintAllGroupDocumentPresenter = new PrintAllGroupDocumentPresenter(AddGroupDocumentToTransactionActivity.this, this);
        mPrintAllGroupDocumentPresenter.PrintAllGroupDocument(groupId);
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
}