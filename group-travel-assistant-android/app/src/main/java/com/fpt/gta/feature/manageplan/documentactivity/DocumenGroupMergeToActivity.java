package com.fpt.gta.feature.manageplan.documentactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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

public class DocumenGroupMergeToActivity extends AppCompatActivity implements PrintAllGroupDocumentView {
    private int groupId;
    private RecyclerView rcvDocumentList;
    private List<DocumentDTO> mDocumentDTOList;
    private List<DocumentDTO> editDocumentList;
    private int numberOfColumn = 3;
    private DocumentGroupAdapter documentGroupAdapter;
    private PrintAllGroupDocumentPresenter mPrintAllGroupDocumentPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documen_group);
        initView();
        initData();
    }

    private void initView() {
        groupId = SharePreferenceUtils.getIntSharedPreference(DocumenGroupMergeToActivity.this, GTABundle.IDGROUP);
        Bundle bundle = getIntent().getExtras();
        editDocumentList = (List<DocumentDTO>) bundle.getSerializable(GTABundle.ADD_DOCUMENT_GROUP_TO_ACTIVITY);
        rcvDocumentList = (RecyclerView) findViewById(R.id.rcvDocumentGroupForActivity);
        //RecyclerView
        rcvDocumentList.setLayoutManager(new GridLayoutManager(this, numberOfColumn));
    }

    private void initData() {
        getData();
    }

    private void updateUI() {
        if (documentGroupAdapter == null) {
            documentGroupAdapter = new DocumentGroupAdapter(mDocumentDTOList, DocumenGroupMergeToActivity.this);
            rcvDocumentList.setAdapter(documentGroupAdapter);
            documentGroupAdapter.setOnItemImageClickListener(new DocumentGroupAdapter.OnItemImageClickListener() {
                @Override
                public void onItemImageClickListener(DocumentDTO documentDTO, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DocumenGroupMergeToActivity.this);
                    builder.setMessage("Are you sure to upload this file to Transaction?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editDocumentList.add(documentDTO);
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(GTABundle.ADD_DOCUMENT_GROUP_TO_ACTIVITY, (Serializable) editDocumentList);
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

//            documentGroupAdapter.setOnItemPDFClickListener(new DocumentGroupAdapter.OnItemPDFClickListener() {
//                @Override
//                public void onItemPDFClickListener(DocumentDTO documentDTO, int position) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(DocumentGroupForDialogActivity.this);
//                    builder.setMessage("Are you sure to upload this file to Transaction?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            editDocumentList.add(documentDTO);
//                            Intent intent = new Intent();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(GTABundle.EDIT_TRANSACTION_DOCUMENT_GROUP, (Serializable) editDocumentList);
//                            intent.putExtras(bundle);
//                            setResult(RESULT_OK, intent);
//                            finish();
//                        }
//                    });
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//
//                }
//            });
        } else {
            documentGroupAdapter.notifyChangeData(mDocumentDTOList);
        }
    }

    private void getData() {
        mPrintAllGroupDocumentPresenter = new PrintAllGroupDocumentPresenter(DocumenGroupMergeToActivity.this, this);
        mPrintAllGroupDocumentPresenter.PrintAllGroupDocument(groupId);
    }


    @Override
    public void printAllGroupDocumentSuccess(List<DocumentDTO> documentDTOList) {
        mDocumentDTOList = new ArrayList<>();
        mDocumentDTOList = documentDTOList;
        for (int i = 0; i <mDocumentDTOList.size(); i++) {
            for (int j = 0; j < editDocumentList.size(); j++ ){
                if (editDocumentList.get(j).getId()!=null){
                    if (mDocumentDTOList.get(i).getId().equals(editDocumentList.get(j).getId())){
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