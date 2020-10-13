package com.fpt.gta.feature.managetransaction.transactiondetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.managegroup.documentgroup.ImageOpenActivity;
import com.fpt.gta.feature.managegroup.documentgroup.PdfViewActivity;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.feature.managetransaction.edittransaction.EditTransactionActivity;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionDetailsActivity extends AppCompatActivity implements PrintMemberInGroupView {
    private String totalName, transactionName;
    private TransactionDetailsAdapter transactionDetailsAdapter;
    private RecyclerView rcvTransactionDetails;
    private RecyclerView rcvImageTransaction;
    private TransactionDTO transactionDTO;
    private ImageView imgBack;
    private List<TransactionDTO.TransactionDetailDTO> participantListOf;
    private final int UPDATE_TRANSACTION = 1995;
    private TextView txtTransactionDetailsTotal, txtTransactionDetailsName, txtEdit, txtTransactionDetailsParticipant, txtTransactionDetailsDate, txtParticipants, txtGroupSharingDetails;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private TransactionDetailsDocumentAdapter mTransactionDetailsDocumentAdapter;
    private List<DocumentDTO> mDocumentDTOList;
    private DocumentDTO documentDTO;
    private LinearLayout lnlAddImageOnTransactionDetails;
    private List<TransactionDTO.TransactionDetailDTO> participantList ;
    private String EDIT_TRANSACTION;
    private int isAdmin;
    private int groupId;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private List<MemberDTO> memberList;
    private String idFribase;
    private boolean valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        initView();
        initData();
    }

    public void initView() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Bundle bundle = getIntent().getExtras();
        transactionDTO = (TransactionDTO) bundle.getSerializable(GTABundle.TRANSACTIONITEMCLICk);
        idFribase = user.getUid();
        participantListOf = TransactionHandler.participantListOf(transactionDTO);
        mDocumentDTOList = transactionDTO.getDocumentList();
        groupId = SharePreferenceUtils.getIntSharedPreference(TransactionDetailsActivity.this, GTABundle.IDGROUP);
        txtTransactionDetailsDate = findViewById(R.id.txtTransactionDetailsDate);
        lnlAddImageOnTransactionDetails = (LinearLayout)findViewById(R.id.lnlAddImageOnTransactionDetails);
        imgBack = findViewById(R.id.imgTransactionDetailsBack);
        txtGroupSharingDetails = findViewById(R.id.txtGroupSharingDetails);
        txtEdit = findViewById(R.id.txtTransactionDetailsEdit);
        txtEdit.setVisibility(View.GONE);
        txtTransactionDetailsName = findViewById(R.id.txtTransactionDetailsName);
        txtParticipants = findViewById(R.id.txtParticipants);
        txtTransactionDetailsName.setText(transactionDTO.getName());
        Date dateOccurAt = transactionDTO.getOccurAt();
        String date = ZonedDateTimeUtil.convertDateToStringASIA(dateOccurAt);
        txtTransactionDetailsDate.setText(date);
        txtTransactionDetailsParticipant = findViewById(R.id.txtTransactionDetailsPaidByMember);
        txtTransactionDetailsParticipant.setText("Paid By " + " " + TransactionHandler.payerDetailOf(transactionDTO).getMember().getPerson().getName());
        txtTransactionDetailsParticipant.setSelected(true);
        txtTransactionDetailsTotal = findViewById(R.id.txtTransactionDetailsTotal);
        txtTransactionDetailsTotal.setSelected(true);
        txtTransactionDetailsTotal.setText(ChangeValue.formatCurrency(TransactionHandler.payerDetailOf(transactionDTO).getAmount().abs()) + " " +transactionDTO.getCurrency().getCode());
        rcvTransactionDetails = findViewById(R.id.rcvTransactionDetails);
        rcvImageTransaction = findViewById(R.id.rcvImageTransaction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    public void loadMemberData(){
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(TransactionDetailsActivity.this, TransactionDetailsActivity.this);
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
    }

    public void initData() {
        loadMemberData();
        lnlAddImageOnTransactionDetails.setVisibility(View.VISIBLE);

        if (mDocumentDTOList.size()>0){
            lnlAddImageOnTransactionDetails.setVisibility(View.GONE);
        }

        int k = 0;
        for (int i = 0; i < participantListOf.size(); i++) {
            participantListOf.get(i).getMember();
            k++;
        }

        TransactionDTO.TransactionDetailDTO groupShareDetailsDTO = new TransactionDTO.TransactionDetailDTO();
        groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(transactionDTO);

        txtGroupSharingDetails.setText(ChangeValue.formatBigCurrency(groupShareDetailsDTO.getAmount()) + " " + transactionDTO.getCurrency().getCode());


        txtParticipants.setText("Individual For" +" "+ k + " participants" + " :");

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEdit(transactionDTO);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvTransactionDetails.setLayoutManager(linearLayoutManager);
        updateUI();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvImageTransaction.setLayoutManager(layoutManager);
        updateTransactionDoucument();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GTABundle.UPDATETRANSACTIONCODE) {
            if (resultCode == RESULT_OK) {
                transactionDTO = (TransactionDTO) data.getSerializableExtra(GTABundle.UPDATETRANSACTION);
                mDocumentDTOList = transactionDTO.getDocumentList();
                txtTransactionDetailsName.setText(transactionDTO.getName().toString());
                Date dateOccured = transactionDTO.getOccurAt();
                txtTransactionDetailsDate.setText(ZonedDateTimeUtil.convertDateToStringASIA(dateOccured));
                txtTransactionDetailsParticipant.setText("Paid By" + transactionDTO.getOwner().getPerson().getName());
                txtTransactionDetailsTotal.setText(ChangeValue.formatCurrency(TransactionHandler.payerDetailOf(transactionDTO).getAmount().abs()));
                participantListOf = TransactionHandler.participantListOf(transactionDTO);
                updateTransactionDoucument();
                updateUI();
                finish();
            }
            if (resultCode == GTABundle.RESULT_DELETE){
                finish();
            }
        } else if (requestCode == GTABundle.TransactionDetailsDocumentImageCode) {
            if (resultCode == RESULT_OK) {
                documentDTO = (DocumentDTO) data.getSerializableExtra(GTABundle.TransactionDetailsDocumentImage);
                mDocumentDTOList.removeIf(removingDocumentDTO -> removingDocumentDTO.getId().compareTo(documentDTO.getId()) == 0);
                updateTransactionDoucument();
            }
        } else if (requestCode == GTABundle.TransactionDetailsDocumentPDFCode) {
            if (resultCode == RESULT_OK) {
                documentDTO = (DocumentDTO) data.getSerializableExtra(GTABundle.TransactionDetailsDocumentPDF);
                mDocumentDTOList.removeIf(removingDocumentDTO -> removingDocumentDTO.getId().compareTo(documentDTO.getId()) == 0);
                updateTransactionDoucument();
            }
        }
    }

    private void updateUI() {
        if (transactionDetailsAdapter == null) {
            transactionDetailsAdapter = new TransactionDetailsAdapter(participantListOf, transactionDTO.getCurrency(), this);
            rcvTransactionDetails.setAdapter(transactionDetailsAdapter);
        } else {
            transactionDetailsAdapter.notifyChange(participantListOf);
        }
    }

    private void updateTransactionDoucument() {
        if (mTransactionDetailsDocumentAdapter == null) {
            mTransactionDetailsDocumentAdapter = new TransactionDetailsDocumentAdapter(TransactionDetailsActivity.this, mDocumentDTOList);
            rcvImageTransaction.setAdapter(mTransactionDetailsDocumentAdapter);
            mTransactionDetailsDocumentAdapter.setOnItemImageClickListenerl(new TransactionDetailsDocumentAdapter.OnItemImageClickListener() {
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
            mTransactionDetailsDocumentAdapter.notifyChange(mDocumentDTOList);
        }
    }

    public void onClickEdit(TransactionDTO transactionDTO) {
        Intent intent = new Intent(TransactionDetailsActivity.this, EditTransactionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.UPDATETRANSACTION, (Serializable) transactionDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.UPDATETRANSACTIONCODE);
    }

    public void onClicPDF(DocumentDTO documentDTO) {
        Intent intent = new Intent(TransactionDetailsActivity.this, PdfViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "TransactionDetailsActivity");
        bundle.putBoolean("valid", valid);
        bundle.putSerializable(GTABundle.TransactionDetailsDocumentPDF, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.TransactionDetailsDocumentPDFCode);
    }

    public void onClickImage(DocumentDTO documentDTO) {
        Intent intent = new Intent(TransactionDetailsActivity.this, ImageOpenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "TransactionDetailsActivity");
        bundle.putBoolean("valid", valid);
        bundle.putSerializable(GTABundle.TransactionDetailsDocumentImage, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.TransactionDetailsDocumentImageCode);
    }

    public boolean checkValidUserTransaction(){
        boolean check = false;

        TransactionDTO.TransactionDetailDTO groupExpenseDetailsDTO = TransactionHandler.groupShareDetailOf(transactionDTO);

        if (groupExpenseDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) > 0){
            check = true;
        }

        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantListOf) {
            if (idFribase.equals(transactionDetailDTO.getMember().getPerson().getFirebaseUid())){
                check = true;
                break;
            }
        }

        if (idFribase.equals(TransactionHandler.payerDetailOf(transactionDTO).getMember().getPerson().getFirebaseUid())){
            check = true;
        }

        if (idFribase.equals(transactionDTO.getOwner().getPerson().getFirebaseUid())){
            check = true;
        }

        return check;
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        if (memberDTOList != null) {
            memberList = new ArrayList<>();
            memberList = memberDTOList;
            boolean valid = checkValidUserTransaction();
            if (valid == true){
                txtEdit.setVisibility(View.VISIBLE);
            }else {
                txtEdit.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void PrintMemberFail(String message) {

    }
}
