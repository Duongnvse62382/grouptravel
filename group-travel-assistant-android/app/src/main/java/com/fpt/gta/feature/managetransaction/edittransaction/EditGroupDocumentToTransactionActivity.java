package com.fpt.gta.feature.managetransaction.edittransaction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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

public class EditGroupDocumentToTransactionActivity extends AppCompatActivity implements PrintAllGroupDocumentView {
    private RecyclerView rcvEditDocumentGroupToTransaction;
    private int groupId;
    private List<DocumentDTO> mDocumentDTOList;
    private List<DocumentDTO> editDocumentDTOList;
    private List<DocumentDTO> chooseDocumentList = new ArrayList<>();
    private int numberOfColumn = 3;
    private DocumentDTO documentDTO;
    private DocumentGroupAdapter documentGroupAdapter;
    private PrintAllGroupDocumentPresenter mPrintAllGroupDocumentPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_document_to_transaction);
        initView();
        initData();
    }

    private void initView() {
        groupId = SharePreferenceUtils.getIntSharedPreference(EditGroupDocumentToTransactionActivity.this, GTABundle.IDGROUP);
        Bundle bundle = getIntent().getExtras();
        editDocumentDTOList = (List<DocumentDTO>) bundle.getSerializable(GTABundle.UPDATE_DOCUMENT_GROUP_TO_TRANSACTION);
        rcvEditDocumentGroupToTransaction = (RecyclerView) findViewById(R.id.rcvEditDocumentGroupToTransaction);
        //RecyclerView
        rcvEditDocumentGroupToTransaction.setLayoutManager(new GridLayoutManager(this, numberOfColumn));

    }

    private void initData() {
        getData();
    }

    private void updateUI() {
        if (documentGroupAdapter == null) {
            documentGroupAdapter = new DocumentGroupAdapter(mDocumentDTOList, EditGroupDocumentToTransactionActivity.this);
            rcvEditDocumentGroupToTransaction.setAdapter(documentGroupAdapter);
            documentGroupAdapter.setOnItemImageClickListener(new DocumentGroupAdapter.OnItemImageClickListener() {
                @Override
                public void onItemImageClickListener(DocumentDTO documentDTO, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditGroupDocumentToTransactionActivity.this);
                    builder.setMessage("Are you sure to upload this file to Transaction?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editDocumentDTOList.add(documentDTO);
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(GTABundle.UPDATE_DOCUMENT_GROUP_TO_TRANSACTION, (Serializable) editDocumentDTOList);
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
        mPrintAllGroupDocumentPresenter = new PrintAllGroupDocumentPresenter(EditGroupDocumentToTransactionActivity.this, this);
        mPrintAllGroupDocumentPresenter.PrintAllGroupDocument(groupId);
    }


    @Override
    public void printAllGroupDocumentSuccess(List<DocumentDTO> documentDTOList) {
        mDocumentDTOList = new ArrayList<>();
        mDocumentDTOList = documentDTOList;

//        mDocumentDTOList.removeIf(removingDocumentDTO -> {
//            boolean isContain = false;
//            for (DocumentDTO dto : editDocumentDTOList) {
//                if (dto.getDownloadThumbnailUrl().equals(removingDocumentDTO.getDownloadThumbnailUrl())) {
//                    isContain = true;
//                    break;
//                }
//            }
//            return isContain;
//        });

        for (int i = 0; i <mDocumentDTOList.size(); i++) {
            for (int j = 0; j < editDocumentDTOList.size(); j++ ){
                if (editDocumentDTOList.get(j).getId()!=null){
                    if (mDocumentDTOList.get(i).getId().equals(editDocumentDTOList.get(j).getId())){
                        mDocumentDTOList.remove(mDocumentDTOList.get(i));
                    }
                }
            }
        }

        updateUI();
    }

    @Override
    public void printAllGroupDocumentFail(String messageFail) {

    }


}