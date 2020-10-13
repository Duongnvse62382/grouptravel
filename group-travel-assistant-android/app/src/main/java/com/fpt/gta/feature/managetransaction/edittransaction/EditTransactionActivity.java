package com.fpt.gta.feature.managetransaction.edittransaction;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.FriendlyMessage;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.constant.MemberStatus;
import com.fpt.gta.data.dto.constant.TransactionCategory;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.data.dto.constant.TransactionType;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.presenter.ConvertCurrencyPresenter;
import com.fpt.gta.presenter.DeleteTransactionPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.presenter.UpdateTransacionPresenter;
import com.fpt.gta.util.BudgetTextWatcher;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.InternetHelper;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.ConvertCurrencyView;
import com.fpt.gta.view.DeleteTransactionView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.fpt.gta.view.UpdateTransactionView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EditTransactionActivity extends AppCompatActivity implements PrintMemberInGroupView, UpdateTransactionView, DeleteTransactionView, ConvertCurrencyView {
    private EditText edtEditExpenseTitle, edtEditAmount, edtEditTransactionDate, edtEditPaidBy, edtEditTransactionType, edtTransactionCategory, edtEditRateCurrency;
    private TextView txtEditAmountGroupExpense;
    private String transactionName, transactionAmount, transactionPaiby, transactionType, rateFinal, valueDialogAmount;
    private List<MemberDTO> membersActiveList;
    private BigDecimal sumAmount;
    public static final String MEMBERS_CHILD = "members";
    private ConvertCurrencyPresenter mConvertCurrencyPresenter;
    private ImageView imgEditpdateActivityBack, imgUpdateTransaction, imgEditDocumentTransaction, imgTransactionCategory, imgDeleteTransaction;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private RecyclerView rcvCheckBoxMember;
    private MemberTransactionDialogAdapter memberTransactionDialogAdapter;
    private CustomeTransactionAdapter customeTransactionAdapter;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart;
    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    private TransactionDTO.TransactionDetailDTO payerDetailDTO = null;
    private TransactionDTO.TransactionDetailDTO groupShareDetailDTO = new TransactionDTO.TransactionDetailDTO();
    private final int MAX_LENGTH = 12;
    private MemberDTO payerDTO = null;
    private List<TransactionDTO.TransactionDetailDTO> participantList;
    private TransactionDTO mTransaction;
    private int idTransaction;
    private boolean check = false;
    private int transactionTypeNumber, transactionTypeCategory, positionTrip;
    private String dateCreateTransaction, transactionCategory;
    private UpdateTransacionPresenter mUpdateTransactionPresenter;
    private DeleteTransactionPresenter mDeleteTransactionPresenter;
    private BigDecimal sumTotal = BigDecimal.ZERO;
    private BigDecimal groupShareAmount = BigDecimal.ZERO;
    private String dialogAmount, defautCurrency, customCurrency, ownerName, strGroupValueAmount;
    private CurrencyDTO currencyDTO;
    private CurrencyDTO groupCurrencyDTO;
    private List<DocumentDTO> documentDTOList;
    private List<DocumentDTO> editDocumentDTOList;
    private String name, type, payer, category, dateTransaction, total, valueAmount;
    private Button btnEditCurrency;
    private DatabaseReference membersRef;
    private DatabaseReference listenerMemberBadges;
    private TextView txEditTransactionError, txtEditRateCurrency, txtEditConvertAmount;
    private BigDecimal defaultCurrencyRate;
    private BigDecimal currencyConvert = BigDecimal.ZERO;
    private BigDecimal bigAmount = BigDecimal.ZERO;
    private BigDecimal groupExpense = BigDecimal.ZERO;
    private BigDecimal finalRateBigdecimal;
    private BigDecimal customCurrencyRate;
    private BigDecimal valueCurrentRate;
    private DatabaseReference datebaseTransaction;
    private DatabaseReference listenerTransaction;
    private LinearLayout lnlEditConvertCurrency;
    private DatabaseReference mFirebaseDatabaseReference;
    private StorageReference mStorage;
    private FirebaseUser user;
    private String mUserId;
    private Long messageTime;
    private int groupId;
    public static final String MESSAGES_CHILD = "messages";
    private String valueNameDL, valueAmountDL;
    private CheckBox chkEditGroupExpense;
    private LinearLayout lnlEditChkGroupExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        initView();
        initData();
    }

    public void initView() {
        btnEditCurrency = findViewById(R.id.btnEditCurrency);
        txtEditConvertAmount = findViewById(R.id.txtEditConvertAmount);
        txtEditRateCurrency = findViewById(R.id.txtEditRateCurrency);
        chkEditGroupExpense = findViewById(R.id.chkEditGroupExpense);
        lnlEditConvertCurrency = findViewById(R.id.lnlEditConvertCurrency);
        lnlEditChkGroupExpense = findViewById(R.id.lnlEditChkGroupExpense);
        edtEditRateCurrency = findViewById(R.id.edtEditRateCurrency);
        lnlEditConvertCurrency.setVisibility(View.GONE);

        imgEditDocumentTransaction = findViewById(R.id.imgEditDocumentTransaction);
        edtEditTransactionType = findViewById(R.id.edtEditTransactionType);
        imgEditpdateActivityBack = findViewById(R.id.imgEditpdateActivityBack);
        txEditTransactionError = findViewById(R.id.txEditTransactionError);
        edtEditTransactionDate = findViewById(R.id.edtEditTransactionDate);
        edtEditAmount = findViewById(R.id.edtEditAmount);
        imgTransactionCategory = findViewById(R.id.imgTransactionCategory);
        edtEditExpenseTitle = findViewById(R.id.edtEditExpenseTitle);
        edtEditPaidBy = findViewById(R.id.edtEditPaidBy);
        imgDeleteTransaction = findViewById(R.id.imgDeleteTransaction);
        txtEditAmountGroupExpense = findViewById(R.id.txtEditAmountGroupExpense);
        rcvCheckBoxMember = findViewById(R.id.rcvCheckBoxMember);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvCheckBoxMember.setLayoutManager(linearLayoutManager);
        imgUpdateTransaction = findViewById(R.id.imgUpdateTransaction);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ownerName = user.getDisplayName();
        mUserId = user.getUid();
        groupId = SharePreferenceUtils.getIntSharedPreference(EditTransactionActivity.this, GTABundle.IDGROUP);

        mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD);
        listenerTransaction = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadTransactionUtc");
        listenerMemberBadges = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadMemberBadges");
        membersRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MEMBERS_CHILD).child(mUserId).child("lastestReadMessage");

        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(EditTransactionActivity.this, EditTransactionActivity.this);
        mConvertCurrencyPresenter = new ConvertCurrencyPresenter(EditTransactionActivity.this, EditTransactionActivity.this);
        mDeleteTransactionPresenter = new DeleteTransactionPresenter(EditTransactionActivity.this, EditTransactionActivity.this);
        loadMember();
    }

    public void loadMember() {
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
    }

    public void updateTransaction() {
        mUpdateTransactionPresenter.updateTransaction(transactionDTOForm(documentDTOList));
    }

    public void deleteTransaction() {
        mDeleteTransactionPresenter.deleteTransaction(idTransaction);
    }

    public void initData() {
        getData();

        btnEditCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTransactionActivity.this, CurrencyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "EditTransactionActivity");
                bundle.putSerializable(GTABundle.EDIT_CURRENCY, (Serializable) currencyDTO);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.EDIT_CURRENCY_CODE);
            }
        });
        imgEditDocumentTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTransactionActivity.this, EditTransactionDocumentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "EditTransactionActivity");
                bundle.putSerializable(GTABundle.EDIT_TRANSACTION_DOCUMENT, (Serializable) documentDTOList);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.EDIT_TRANSACTION_DOCUMENT_CODE);
            }
        });

        chkEditGroupExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEditGroupExpense.isChecked()) {
                    String amount = edtEditAmount.getText().toString().replaceAll(",", "");
                    BigDecimal amountGroup = new BigDecimal(Double.parseDouble(amount));
                    txtEditAmountGroupExpense.setText(ChangeValue.formatBigCurrency(amountGroup));
                    groupShareDetailDTO.setAmount(amountGroup);
                    customeTransactionAdapter.divideAmount();
                    updateUI();
                } else {
                    groupShareDetailDTO.setAmount(BigDecimal.ZERO);
                    txtEditAmountGroupExpense.setText("0");
                    customeTransactionAdapter.divideAmount();
                    updateUI();
                }
            }
        });


        imgEditpdateActivityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edtEditTransactionDate.setFocusable(false);
        edtEditTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTransactionDate();
            }
        });

        txtEditAmountGroupExpense.setFocusable(false);
        txtEditAmountGroupExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAmountGroupExpense();
            }
        });

        imgTransactionCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });


        edtEditAmount.setFocusable(false);
        edtEditAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAmount();
            }
        });

        edtEditPaidBy.setFocusable(false);
        edtEditPaidBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpPaidByDialog();
            }
        });

        edtEditExpenseTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        edtEditTransactionType.setFocusable(false);
        edtEditTransactionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionTypeDialog();
            }
        });

        edtEditRateCurrency.setFocusable(false);
        edtEditRateCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustomeRate();
            }
        });

        imgUpdateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateTransactionPresenter = new UpdateTransacionPresenter(EditTransactionActivity.this, EditTransactionActivity.this);
                if (InternetHelper.isOnline(EditTransactionActivity.this) == false) {
                    DialogShowErrorMessage.showDialogNoInternet(EditTransactionActivity.this, "No Internetion");
                } else {
                    if (isValidTransaction()) {
                        updateTransaction();
                    }
                }
            }
        });

        imgDeleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueNameDL = transactionName;
                valueAmountDL = transactionAmount;
                AlertDialog.Builder builder = new AlertDialog.Builder(EditTransactionActivity.this);
                builder.setMessage("Are you sure to delete this Transaction?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (InternetHelper.isOnline(EditTransactionActivity.this) == false) {
                            DialogShowErrorMessage.showDialogNoInternet(EditTransactionActivity.this, "No Internetion");
                        } else {
                            deleteTransaction();
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
            }
        });
    }

    public void dialogAmountGroupExpense() {
        final Dialog dialog = new Dialog(EditTransactionActivity.this);
        dialog.setContentView(R.layout.dialog_group_expense_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText edtDialogGroupShareAmount = dialog.findViewById(R.id.dialogGroupExpenseTotalAmount);
        Button btnDialogChangeTransaction = dialog.findViewById(R.id.dialog_button_GroupExpenseAmountOK);
        edtDialogGroupShareAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDialogGroupShareAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtDialogGroupShareAmount.addTextChangedListener(new BudgetTextWatcher(edtDialogGroupShareAmount));
        btnDialogChangeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strGroupExpense = edtDialogGroupShareAmount.getText().toString().replaceAll(",", "");
                if (strGroupExpense.matches("")) {
                    edtDialogGroupShareAmount.setError("Invalid Group Amount");
                } else {
                    char ch1 = strGroupExpense.charAt(0);
                    if (strGroupExpense.trim().length() <= 0 || ch1 == '.' || strGroupExpense.trim().equals("")) {
                        edtDialogGroupShareAmount.setError("Invalid Group Amount");
                    } else {
                        groupShareAmount = new BigDecimal(strGroupExpense);
                        chkEditGroupExpense.setChecked(groupShareAmount.compareTo(BigDecimal.ZERO) != 0);
                        BigDecimal isGroupShareAmount = new BigDecimal(Double.parseDouble(strGroupExpense));
                        txtEditAmountGroupExpense.setText(ChangeValue.formatBigCurrency(isGroupShareAmount));
                        groupShareDetailDTO.setAmount(BigDecimal.valueOf(Double.parseDouble(strGroupExpense)));
                        customeTransactionAdapter.divideAmount();
                        updateUI();
                        groupExpense = new BigDecimal(Double.parseDouble(strGroupExpense));
                        dialog.dismiss();
                    }
                }
            }
        });
        Button btnDialogClose = dialog.findViewById(R.id.dialog_button_close_GroupExpenseAmount);
        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void getData() {
        Gson gson = new Gson();
        String groupCurrency = SharePreferenceUtils.getStringSharedPreference(EditTransactionActivity.this, GTABundle.GROUP_CURRENCY_SHARE);
        groupCurrencyDTO = gson.fromJson(groupCurrency, CurrencyDTO.class);
        Bundle bundle = getIntent().getExtras();
        mTransaction = (TransactionDTO) bundle.getSerializable(GTABundle.UPDATETRANSACTION);
        groupShareDetailDTO = TransactionHandler.groupShareDetailOf(mTransaction);
        defaultCurrencyRate = mTransaction.getDefaultCurrencyRate();
        customCurrencyRate = mTransaction.getCustomCurrencyRate();
        documentDTOList = mTransaction.getDocumentList();
        payerDetailDTO = TransactionHandler.payerDetailOf(mTransaction);
        participantList = new ArrayList<>();
        participantList = TransactionHandler.participantListOf(mTransaction);
        currencyDTO = mTransaction.getCurrency();
        transactionAmount = ChangeValue.formatBigCurrency(TransactionHandler.payerDetailOf(mTransaction).getAmount().abs());
        edtEditAmount.setFocusable(false);
        edtEditAmount.setText(transactionAmount);
        valueDialogAmount = edtEditAmount.getText().toString().replaceAll(",", "");
        txtEditAmountGroupExpense.setText(ChangeValue.formatBigCurrency(groupShareDetailDTO.getAmount()));

        if (groupShareDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            chkEditGroupExpense.setChecked(true);
        } else {
            chkEditGroupExpense.setChecked(false);
        }
        if (!currencyDTO.getCode().equals(groupCurrencyDTO.getCode())) {
            lnlEditConvertCurrency.setVisibility(View.VISIBLE);
            BigDecimal valueCurrentTotalAmout = BigDecimal.valueOf(Double.parseDouble(valueDialogAmount));
            if ((customCurrencyRate.compareTo(BigDecimal.ZERO) != 0)) {
                valueCurrentRate = customCurrencyRate;
            } else if (customCurrencyRate.compareTo(BigDecimal.ZERO) == 0) {
                valueCurrentRate = defaultCurrencyRate;
            }
            BigDecimal result = valueCurrentTotalAmout.multiply(valueCurrentRate);
            txtEditConvertAmount.setText(ChangeValue.formatBigCurrency(result));
            txtEditRateCurrency.setText("Rate (1" + " " + currencyDTO.getCode() + " " + "in" + " " + groupCurrencyDTO.getCode() + ")");
            edtEditRateCurrency.setText(ChangeValue.formatRateCurrency(valueCurrentRate));
        }
        payerDTO = TransactionHandler.payerDetailOf(mTransaction).getMember();
        for (int i = 0; i < participantList.size(); i++) {
            participantList.get(i).setSelected(true);
        }
        transactionName = mTransaction.getName();
        edtEditExpenseTitle.setText(transactionName);
        edtEditTransactionDate.setText(ZonedDateTimeUtil.convertDateToStringASIA(mTransaction.getOccurAt()));
        btnEditCurrency.setText(currencyDTO.getCode());
        idTransaction = mTransaction.getId();

        transactionTypeNumber = mTransaction.getIdType();
        if (transactionTypeNumber == 1) {
            edtEditTransactionType.setText("EXPENSE");
            lnlEditChkGroupExpense.setVisibility(View.VISIBLE);
        } else if (transactionTypeNumber == 2) {
            edtEditTransactionType.setText("TRANSFER");
            lnlEditChkGroupExpense.setVisibility(View.GONE);
        }
//        } else {
//            edtEditTransactionType.setText("INCOME");
//            lnlEditChkGroupExpense.setVisibility(View.GONE);
//        }

        transactionTypeCategory = mTransaction.getIdCategory();
        if (transactionTypeCategory == 1) {
            Glide.with(EditTransactionActivity.this).load(R.mipmap.category).into(imgTransactionCategory);
        } else if (transactionTypeCategory == 2) {
            Glide.with(EditTransactionActivity.this).load(R.mipmap.accomodation).into(imgTransactionCategory);
        } else if (transactionTypeCategory == 3) {
            Glide.with(EditTransactionActivity.this).load(R.mipmap.transaportationicon).into(imgTransactionCategory);
        } else if (transactionTypeCategory == 4) {
            Glide.with(EditTransactionActivity.this).load(R.mipmap.fastfood).into(imgTransactionCategory);
        }
        transactionPaiby = TransactionHandler.payerDetailOf(mTransaction).getMember().getPerson().getName();
        edtEditPaidBy.setText(transactionPaiby);
        payerDetailDTO = TransactionHandler.payerDetailOf(mTransaction);
    }

    public void showError(String error) {
        txEditTransactionError.setText(error);
        txEditTransactionError.setVisibility(View.VISIBLE);
    }

    public void dialogCustomeRate() {
        final int MAX_LENGTH = 10;
        final Dialog dialog = new Dialog(EditTransactionActivity.this);
        dialog.setContentView(R.layout.dialog_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText edtDialogAmount = dialog.findViewById(R.id.dialogTotalAmount);
        edtDialogAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDialogAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtDialogAmount.addTextChangedListener(new BudgetTextWatcher(edtDialogAmount, 9));

        Button btnDialogChangeTransaction = dialog.findViewById(R.id.dialog_button_AmountOK);
        btnDialogChangeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal checkBigDecimal;
                String dialogCustomRate = edtDialogAmount.getText().toString().replaceAll(",", "");
                if (dialogCustomRate.matches("")) {
                    edtDialogAmount.setError("Invalid Rate");
                } else {
                    char ch1 = dialogCustomRate.charAt(0);
                    if (dialogCustomRate.trim().length() <= 0 || ch1 == '.' || dialogCustomRate.trim().equals("0") || dialogCustomRate.trim().equals("")) {
                        edtDialogAmount.setError("Invalid Rate");
                    } else {
                        checkBigDecimal = new BigDecimal(dialogCustomRate);
                        if (checkBigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                            edtDialogAmount.setError("Invalid Rate");
                        } else {
                            edtEditRateCurrency.setText(dialogCustomRate);
                            customCurrencyRate = BigDecimal.valueOf(Double.parseDouble(dialogCustomRate));
                            updateConvertedAmountUI();
                            dialog.dismiss();
                        }
                    }
                }
            }
        });


        Button btnDialogClose = dialog.findViewById(R.id.dialog_button_close_Amount);
        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        updateUI();
    }


    public void dialogAmount() {
        final int MAX_LENGTH = 10;
        final Dialog dialog = new Dialog(EditTransactionActivity.this);
        dialog.setContentView(R.layout.dialog_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText edtDialogAmount = dialog.findViewById(R.id.dialogTotalAmount);
        edtDialogAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDialogAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtDialogAmount.addTextChangedListener(new BudgetTextWatcher(edtDialogAmount));
        Button btnDialogChangeTransaction = dialog.findViewById(R.id.dialog_button_AmountOK);
        btnDialogChangeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAmount = edtDialogAmount.getText().toString();
                if (dialogAmount.matches("")) {
                    edtDialogAmount.setError("Invalid Amount");
                } else {
                    char ch1 = dialogAmount.charAt(0);
                    bigAmount = BigDecimal.ZERO;
                    if (dialogAmount.trim().length() <= 0 || ch1 == '.' || dialogAmount.trim().equals("0") || dialogAmount.trim().equals("")) {
                        edtDialogAmount.setError("Invalid Amount");
                    } else {
                        bigAmount = new BigDecimal(dialogAmount.replaceAll(",", ""));
                        if (bigAmount.compareTo(BigDecimal.ZERO) == 0) {
                            edtDialogAmount.setError("Invalid Amount");
                        } else {
                            edtEditAmount.setText(dialogAmount);
                            valueDialogAmount = edtEditAmount.getText().toString();
                            payerDetailDTO.setAmount(BigDecimal.valueOf(Double.parseDouble(dialogAmount.replaceAll(",", ""))));
                            customeTransactionAdapter.divideAmount();
                            updateConvertedAmountUI();
                            dialog.dismiss();
                        }
                    }
                }
            }
        });
        Button btnDialogClose = dialog.findViewById(R.id.dialog_button_close_Amount);
        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void updateConvertedAmountUI() {
        valueAmount = edtEditAmount.getText().toString().replaceAll(",", "");
//        double parsed = Double.parseDouble(valueAmount);
//        String formatted = ChangeValue.formatDecimalPrice(parsed);
        if (lnlEditConvertCurrency.getVisibility() == View.VISIBLE) {
            if (customCurrencyRate.compareTo(BigDecimal.ZERO) == 0) {
                txtEditConvertAmount.setText(ChangeValue.formatBigCurrency(defaultCurrencyRate.multiply(BigDecimal.valueOf(Double.parseDouble(valueAmount)))));
            } else {
                txtEditConvertAmount.setText(ChangeValue.formatBigCurrency(customCurrencyRate.multiply(BigDecimal.valueOf(Double.parseDouble(valueAmount)))));
            }
        }

    }

    private void showTransactionTypeDialog() {
        final Dialog dialog = new Dialog(EditTransactionActivity.this);
        dialog.setContentView(R.layout.row_dialog_transactiontype);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTransfer = dialog.findViewById(R.id.txtTransfer);
        TextView txtExpense = dialog.findViewById(R.id.txtExpense);
//        TextView txtIncome = dialog.findViewById(R.idINCOME.txtIncome);
        ImageView imgDiaLogTransactionTypeBack = dialog.findViewById(R.id.imgDiaLogActivity);
        imgDiaLogTransactionTypeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        txtExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionType = TransactionType.EXPENSE + "";
                edtEditTransactionType.setText("Expense");
                transactionTypeNumber = 1;
                lnlEditChkGroupExpense.setVisibility(View.VISIBLE);
                groupShareDetailDTO.setAmount(BigDecimal.ZERO);
                txtEditAmountGroupExpense.setText("0");
                customeTransactionAdapter.divideAmount();
            }
        });

        txtTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionType = TransactionType.TRANSFER + "";
                edtEditTransactionType.setText("Transfer");
                transactionTypeNumber = 2;
                lnlEditChkGroupExpense.setVisibility(View.GONE);
                groupShareDetailDTO.setAmount(BigDecimal.ZERO);
                txtEditAmountGroupExpense.setText("0");
                customeTransactionAdapter.divideAmount();
            }
        });

//        txtIncome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                transactionType = TransactionType.INCOME + "";
//                edtEditTransactionType.setText("Income");
//                transactionTypeNumber = 3;
//                lnlEditChkGroupExpense.setVisibility(View.GONE);
//                groupShareDetailDTO.setAmount(BigDecimal.ZERO);
//                txtEditAmountGroupExpense.setText("0");
//                customeTransactionAdapter.divideAmount();
//            }
//        });
        dialog.show();
    }

    private boolean isValidTransaction() {
        name = edtEditExpenseTitle.getText().toString();
        type = edtEditTransactionType.getText().toString();
        payer = edtEditPaidBy.getText().toString();
        dateTransaction = edtEditTransactionDate.getText().toString();
        total = edtEditAmount.getText().toString().replaceAll(",", "");
        rateFinal = edtEditRateCurrency.getText().toString().replaceAll(",", "");
        strGroupValueAmount = txtEditAmountGroupExpense.getText().toString().replaceAll(",", "");
        BigDecimal groupExpenseAmount = new BigDecimal(strGroupValueAmount);

        boolean result = true;
        if (participantList.size() < 0) {
            showError("Transaction is not valid");
            result = false;
        }

        BigDecimal checkTotal = BigDecimal.ZERO;

        if (lnlEditConvertCurrency.getVisibility() == View.VISIBLE) {
            if (rateFinal.trim().length() <= 0 || rateFinal.trim().equals("") || rateFinal.trim().equals(".") || rateFinal.trim().equals("0")) {
                showError("Invalid Rate");
                result = false;
            } else {
                finalRateBigdecimal = BigDecimal.valueOf(Double.parseDouble(rateFinal));
                if (finalRateBigdecimal.compareTo(currencyConvert) != 0) {
                    customCurrencyRate = finalRateBigdecimal;
                }
            }
        }

        if (total.trim().length() <= 0) {
            showError("Please Input Total Amount");
            result = false;
        } else {
            checkTotal = new BigDecimal(total);
            if (checkTotal.compareTo(BigDecimal.ZERO) == 0) {
                showError("Please input total Amount > 0");
                result = false;
            }
        }
        if (name.trim().length() <= 0) {
            showError("Please input Transaction Name");
            result = false;
        }

        BigDecimal sumOfParticipant = BigDecimal.ZERO;

        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantList) {
            BigDecimal bigDecimal = BigDecimal.ZERO;
            bigDecimal = transactionDetailDTO.getAmount();
            sumOfParticipant = sumOfParticipant.add(bigDecimal);
            if (bigDecimal.compareTo(BigDecimal.ZERO) <= 0 && transactionDetailDTO.isSelected()) {
                showError("Amount of Each Member must be >0");
                result = false;
            }
        }

        sumOfParticipant = sumOfParticipant.add(groupExpenseAmount);

        if (sumOfParticipant.compareTo(
                BigDecimal.valueOf(Double.parseDouble(total))) != 0) {
            showError("Group Expense Or Individual Expense Not Equal Total Bill");
            result = false;
        }

        //check participant amount > 0
        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantList) {
            BigDecimal amount = transactionDetailDTO.getAmount();
            if (transactionDetailDTO.isSelected()) {
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    showError("Participant amount must > 0");
                    result = false;
                }
            }
        }

        boolean isCurrentPayer = payerDetailDTO.getMember().getPerson().getFirebaseUid().equals(mUserId);
        boolean isInParticipantList = false;
        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantList) {
            if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(mUserId) && transactionDetailDTO.isSelected()) {
                isInParticipantList = true;
                break;
            }
        }

        //check is allow while in transaction
        if (transactionTypeNumber == TransactionType.EXPENSE || transactionTypeNumber == TransactionType.INCOME) {
            //must have in participant list or groupshare >0
            if (!(isInParticipantList || groupShareDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0)) {
                showError("You must be a Participant");
                result = false;
            }
        } else if (transactionTypeNumber == TransactionType.TRANSFER) {
            // 1 trong 2 phai khac nhau
            if (isCurrentPayer == isInParticipantList) {
                showError("Don't transfer to yourself");
                result = false;
            }
        }

        return result;
    }

    public void getTransactionDate() {
        calendarStart = Calendar.getInstance();
        mDay = calendarStart.get(Calendar.DATE);
        mMonth = calendarStart.get(Calendar.MONTH);
        mYear = calendarStart.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int dayOfMonth,
                                          int monthOfYear, int year) {
                        calendarStart.set(dayOfMonth, monthOfYear, year);
                        dateCreateTransaction = format.format(calendarStart.getTime());
                        edtEditTransactionDate.setText(dateCreateTransaction);
                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void updateUI() {
        if (customeTransactionAdapter == null) {
            for (MemberDTO memberDTO : membersActiveList) {
                boolean isDuplicate = false;
                for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantList) {
                    if (transactionDetailDTO.getMember().getId() == memberDTO.getId()) {
                        isDuplicate = true;
                    }

                }
                if (isDuplicate == false) {
                    participantList.add(new TransactionDTO.TransactionDetailDTO(0, BigDecimal.ZERO, memberDTO));
                }
            }

            customeTransactionAdapter = new CustomeTransactionAdapter(participantList, this, payerDetailDTO, groupShareDetailDTO);
            rcvCheckBoxMember.setAdapter(customeTransactionAdapter);
            customeTransactionAdapter.notifyChange(participantList);
            customeTransactionAdapter.setOnItemTransactionClickListener(new CustomeTransactionAdapter.OnItemTransactionClickListener() {
                @Override
                public void onItemTransactionClickListener(TransactionDTO.TransactionDetailDTO transactionDetailDTO, int postion) {
                    dialogParticipantList(participantList, postion);
                }
            });
            customeTransactionAdapter.setOnItemValueMoneyClickListener(new CustomeTransactionAdapter.OnItemValueMoneyClickListener() {
                @Override
                public void onItemValueMoneyClickListener(TransactionDTO.TransactionDetailDTO transactionDetailDTO, int position) {
                    boolean isAllSelected = true;
                    for (TransactionDTO.TransactionDetailDTO detailDTO : participantList) {
                        if (!detailDTO.isSelected()) {
                            isAllSelected = false;
                            break;
                        }
                    }

                }
            });
        } else {
            customeTransactionAdapter.notifyChange(participantList);
        }
    }

    public void dialogParticipantList(List<TransactionDTO.TransactionDetailDTO> mDetailDTOS, int pos) {
        final int MAX_LENGTH = 10;
        final Dialog dialog = new Dialog(EditTransactionActivity.this);
        dialog.setContentView(R.layout.dialog_edit_add_transaction);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView mTxtName = dialog.findViewById(R.id.dialogName);
        EditText edtdialogTransactionMoney = dialog.findViewById(R.id.dialogTransactionMoney);
        Button btnDialogChangeTransaction = dialog.findViewById(R.id.dialog_button_editTransaction);
        Button btnDialogClose = dialog.findViewById(R.id.dialog_button_close_editTransaction);
        mTxtName.setText(mDetailDTOS.get(pos).getMember().getPerson().getName());
        edtdialogTransactionMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtdialogTransactionMoney.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtdialogTransactionMoney.addTextChangedListener(new BudgetTextWatcher(edtdialogTransactionMoney));
        edtdialogTransactionMoney.setText(ChangeValue.formatBigCurrency(mDetailDTOS.get(pos).getAmount()));
        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDialogChangeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moneyInput = edtdialogTransactionMoney.getText().toString();
                if (moneyInput.matches("")) {
                    edtdialogTransactionMoney.setError("Invalid number");
                } else {
                    char ch1 = moneyInput.charAt(0);
                    if (moneyInput.trim().equals("") || moneyInput.trim().length() <= 0 || ch1 == '.') {
                        edtdialogTransactionMoney.setError("Invalid Number");
                    } else {
                        BigDecimal valueMoney = BigDecimal.valueOf(Double.parseDouble(moneyInput.replaceAll(",", "")));
                        mDetailDTOS.get(pos).setAmount(valueMoney);
                        mDetailDTOS.get(pos).setSelected(true);
                        mDetailDTOS.get(pos).setModified(true);
                        dialog.dismiss();
                        customeTransactionAdapter.divideAmount();
                        customeTransactionAdapter.notifyChange(mDetailDTOS);
                    }
                }
            }
        });
        dialog.show();
    }

    private TransactionDTO transactionDTOForm(List<DocumentDTO> documentDTOList) {
        String dateCreate = edtEditTransactionDate.getText().toString();
        payerDetailDTO.setAmount(BigDecimal.valueOf(Double.valueOf(edtEditAmount.getText().toString().replaceAll(",", ""))));
        payerDetailDTO.setMember(payerDTO);
        mTransaction.setIdType(transactionTypeNumber);
        mTransaction.setIdCategory(transactionTypeCategory);
        mTransaction.setDefaultCurrencyRate(defaultCurrencyRate);
        mTransaction.setCustomCurrencyRate(customCurrencyRate);
        mTransaction.setName(edtEditExpenseTitle.getText().toString());
        mTransaction.setOccurAt(ZonedDateTimeUtil.convertStringToDateOrTime(dateCreate));
        mTransaction.setTransactionDetailList(participantList);
        mTransaction.setDocumentList(documentDTOList);
        mTransaction.setCurrency(currencyDTO);
        TransactionHandler.preparedTransactionDTO(mTransaction, payerDetailDTO, groupShareDetailDTO);
        return mTransaction;
    }

    private void showCategoryDialog() {
        final Dialog dialog = new Dialog(EditTransactionActivity.this);
        dialog.setContentView(R.layout.row_dialog_category);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtActivityType, txtAccomodation, txtTransportation, txtFoodAndBeverage;
        txtAccomodation = dialog.findViewById(R.id.txtAccomodation);
        txtTransportation = dialog.findViewById(R.id.txtTransportation);
        txtActivityType = dialog.findViewById(R.id.txtActivityType);
        txtFoodAndBeverage = dialog.findViewById(R.id.txtFandB);

        txtActivityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.ACTIVITY + "";
                Glide.with(EditTransactionActivity.this).load(R.mipmap.category).into(imgTransactionCategory);
                transactionTypeCategory = 1;
            }
        });

        txtAccomodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.ACCOMMODATION + "";
                Glide.with(EditTransactionActivity.this).load(R.mipmap.accomodation).into(imgTransactionCategory);
                transactionTypeCategory = 2;
            }
        });

        txtTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.TRANSPORTATION + "";
                Glide.with(EditTransactionActivity.this).load(R.mipmap.transaportationicon).into(imgTransactionCategory);
                transactionTypeCategory = 3;
            }
        });

        txtFoodAndBeverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.FOOD + "";
                Glide.with(EditTransactionActivity.this).load(R.mipmap.fastfood).into(imgTransactionCategory);
                transactionTypeCategory = 4;
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GTABundle.EDIT_TRANSACTION_DOCUMENT_CODE) {
            if (resultCode == RESULT_OK) {
                documentDTOList = (List<DocumentDTO>) data.getSerializableExtra(GTABundle.EDIT_TRANSACTION_DOCUMENT);
            }
        } else if (requestCode == GTABundle.EDIT_CURRENCY_CODE) {
            if (resultCode == RESULT_OK) {
                currencyDTO = (CurrencyDTO) data.getSerializableExtra(GTABundle.EDIT_CURRENCY);
                btnEditCurrency.setText(currencyDTO.getCode());
                if (!currencyDTO.getName().equalsIgnoreCase(groupCurrencyDTO.getName())) {
                    lnlEditConvertCurrency.setVisibility(View.VISIBLE);
                    loadConvertCurrency();
                } else if (currencyDTO.getName().equalsIgnoreCase(groupCurrencyDTO.getName())) {
                    lnlEditConvertCurrency.setVisibility(View.GONE);
                    customCurrencyRate = BigDecimal.ZERO;
                }

            }
        }
    }

    public void loadConvertCurrency() {
        mConvertCurrencyPresenter.convertCurrencyMoney(currencyDTO.getCode(), groupCurrencyDTO.getCode());

    }


    private void showpPaidByDialog() {
        final Dialog dialog = new Dialog(EditTransactionActivity.this);
        dialog.setContentView(R.layout.row_dialog_paidby);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rcvMemberPaidBy = dialog.findViewById(R.id.rcvCheckBoxPaidByMember);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(dialog.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvMemberPaidBy.setLayoutManager(linearLayoutManager);
        memberTransactionDialogAdapter = new MemberTransactionDialogAdapter(EditTransactionActivity.this, membersActiveList);
        rcvMemberPaidBy.setAdapter(memberTransactionDialogAdapter);
        memberTransactionDialogAdapter.setOnEditItemClickListener(new MemberTransactionDialogAdapter.OnItemEditClickListener() {
            @Override
            public void onItemEditClickListener(MemberDTO memberDTO, int position) {
                dialog.dismiss();
                edtEditPaidBy.setText(membersActiveList.get(position).getPerson().getName() + "");
                payerDTO = memberDTO;
            }
        });
        dialog.show();
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        if (memberDTOList != null) {
            membersActiveList = new ArrayList<>();
            for (MemberDTO memberDTO : memberDTOList) {
                if (memberDTO.getIdStatus().compareTo(MemberStatus.ACTIVE) == 0) {
                    membersActiveList.add(memberDTO);
                }
            }
            updateUI();

        }
    }

    @Override
    public void PrintMemberFail(String message) {
        DialogShowErrorMessage.showDialogNoInternet(this, "No Internet");
    }

    @Override
    public void updateTransactionSuccess(String success) {
        if (groupShareDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            TimeZone tz = TimeZone.getDefault();
            String timez = tz.getID();
            try {
                Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                Long change = dateNoti.getTime();
                listenerTransaction.setValue(change);

                messageTime = dateNoti.getTime();
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(ownerName + " " + "updated transaction name:" + " " + edtEditExpenseTitle.getText().toString() + "." + "\nTotal Amount:" + edtEditAmount.getText().toString(),
                        messageTime,
                        mUserId,
                        null /* no image */);
                mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                membersRef.setValue(messageTime);
                listenerMemberBadges.setValue(listenerMemberBadges);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.UPDATETRANSACTION, transactionDTOForm(documentDTOList));
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void updateTransactionFail(String fail) {
        DialogShowErrorMessage.showValidationDialog(this, fail);
    }

    @Override
    public void deleteTransactionSuccess(String message) {
        if (groupShareDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            TimeZone tz = TimeZone.getDefault();
            String timez = tz.getID();
            try {
                Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                Long change = dateNoti.getTime();
                listenerTransaction.setValue(change);
                messageTime = dateNoti.getTime();
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(ownerName + " " + "deleted transaction name:" + " " + valueNameDL + "." + "\nTotal Amount:" + valueAmountDL,
                        messageTime,
                        mUserId,
                        null /* no image */);
                mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                membersRef.setValue(messageTime);
                listenerMemberBadges.setValue(listenerMemberBadges);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("Update", "EditTransactionActivity");
        intent.putExtras(bundle);
        setResult(GTABundle.RESULT_DELETE, intent);
        finish();
    }

    @Override
    public void deleteTransactionFail(String message) {
        DialogShowErrorMessage.showValidationDialog(this, message);
    }

    @Override
    public void convertCurrencySuccess(BigDecimal bigDecimal) {
        currencyConvert = bigDecimal;
        defaultCurrencyRate = bigDecimal;
        customCurrencyRate = BigDecimal.ZERO;
        edtEditRateCurrency.setText(ChangeValue.formatRateCurrency(currencyConvert));
        BigDecimal result = bigAmount.multiply(currencyConvert);
        updateConvertedAmountUI();
        txtEditRateCurrency.setText("Rate (1" + " " + currencyDTO.getCode() + " " + "in" + " " + groupCurrencyDTO.getCode() + ")");
    }

    @Override
    public void convertCurrencyFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }

}