package com.fpt.gta.feature.managetransaction.addtransaction;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import com.fpt.gta.feature.managetransaction.edittransaction.CurrencyActivity;
import com.fpt.gta.feature.managetransaction.edittransaction.CustomeTransactionAdapter;
import com.fpt.gta.feature.managetransaction.edittransaction.MemberTransactionDialogAdapter;
import com.fpt.gta.presenter.AddTransactionPresenter;
import com.fpt.gta.presenter.ConvertCurrencyPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.BudgetTextWatcher;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.InternetHelper;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.AddTransactionView;
import com.fpt.gta.view.ConvertCurrencyView;
import com.fpt.gta.view.PrintMemberInGroupView;
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

public class AddTransactionActivity extends AppCompatActivity implements PrintMemberInGroupView, AddTransactionView, ConvertCurrencyView {
    private EditText edtAddExpenseTitle, edtAddTransactionDate, edtAddPaidBy, edtAddTransactionType;
    private ImageView imgTransactionCategory, imgAddDocumentTransaction;
    private List<MemberDTO> membersActiveList;
    private EditText edtAddAmount, edtAddRateCurrency;
    private TextView txtAmountGroupExpense;
    private List<TransactionDTO.TransactionDetailDTO> participantList = new ArrayList<>();
    public static final String MEMBERS_CHILD = "members";
    public static final String MESSAGES_CHILD = "messages";
    private ImageView imgAddTransactionBack, imgSaveTransaction;
    private MemberTransactionDialogAdapter memberTransactionDialogAdapter;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private RecyclerView rcvCheckBoxMember;
    private CustomeTransactionAdapter customeTransactionAdapter;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart;
    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    private String trasanctionType, transactionCategory;
    private int transactionTypeNumber, transactionTypeCategory;
    private MemberDTO payerDTO = null;
    private TransactionDTO.TransactionDetailDTO payerDetailDTO;
    private TransactionDTO.TransactionDetailDTO groupShareDetailDTO = new TransactionDTO.TransactionDetailDTO();
    private AddTransactionPresenter mAddTransactionPresenter;
    private TransactionDTO transactionDTO;
    private BigDecimal sumAmount;
    private BigDecimal finalRateBigdecimal;
    private String dialogAmount;
    private LinearLayout lnlCategory, lnlConvertCurrency;
    private TextView txtError, txtAddConvertAmount, txtAddRateCurrency;
    private BigDecimal bigAmount = BigDecimal.ZERO;
    private BigDecimal currencyConvert = BigDecimal.ZERO;
    private BigDecimal groupShareAmount = BigDecimal.ZERO;
    private boolean check = false;
    private List<DocumentDTO> mDocumentDTOList = new ArrayList<>();
    private DocumentDTO documentDTO;
    private Button btnAddCurrency;
    private CurrencyDTO currencyDTO;
    private ConvertCurrencyPresenter mConvertCurrencyPresenter;
    private CurrencyDTO groupCurrencyDTO;
    private BigDecimal customRate = BigDecimal.ZERO;
    private BigDecimal defaultRate = BigDecimal.ONE;
    private BigDecimal groupExpense = BigDecimal.ZERO;
    private String rateFinal, valueDialogRate = "0", rate, name, dateTransaction, customTotal, type, payer, valueAmount, ocrBill, defaultTotal;
    private DatabaseReference databaseAddTransaction;
    private DatabaseReference listeneTransaction;
    private int groupId;
    private final int MAX_LENGTH = 12;
    private Long messageTime;
    private String CALLING_TRANSACTION;
    private DatabaseReference membersRef;
    private DatabaseReference listenerMemberBadges;
    private String ownerName;
    private CheckBox chkGroupExpense;
    private DatabaseReference mFirebaseDatabaseReference;
    private StorageReference mStorage;
    private FirebaseUser user;
    private String mUserId;
    private FirebaseAuth auth;
    private LinearLayout lnlChkGroupExpense;

    private String strGroupExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        initView();
        initData();
    }

    public void initView() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ownerName = user.getDisplayName();
        mUserId = user.getUid();
        imgAddDocumentTransaction = findViewById(R.id.imgAddDocumentTransaction);
        lnlConvertCurrency = findViewById(R.id.lnlConvertCurrency);
        edtAddRateCurrency = findViewById(R.id.edtAddRateCurrency);
        txtAmountGroupExpense = findViewById(R.id.txtAmountGroupExpense);
        txtAddRateCurrency = findViewById(R.id.txtAddRateCurrency);
        txtAddConvertAmount = findViewById(R.id.txtAddConvertAmount);
        btnAddCurrency = findViewById(R.id.btnAddCurrency);
        chkGroupExpense = findViewById(R.id.chkGroupExpense);
        imgSaveTransaction = findViewById(R.id.imgSaveTransaction);
        lnlCategory = findViewById(R.id.lnlCategory);
        lnlCategory = findViewById(R.id.lnlCategory);
        edtAddTransactionType = findViewById(R.id.edtTransactionType);
        txtError = findViewById(R.id.txtAddTransactionError);
        imgAddTransactionBack = findViewById(R.id.imgAddTransactionBack);
        edtAddTransactionDate = findViewById(R.id.edtAddTransactionDate);
        edtAddAmount = findViewById(R.id.edtAddAmount);
        edtAddAmount.setText("0");
        imgTransactionCategory = findViewById(R.id.edtTransactionCategory);
        edtAddAmount.setFocusable(false);
        edtAddExpenseTitle = findViewById(R.id.edtAddExpenseTitle);
        lnlConvertCurrency.setVisibility(View.GONE);
        edtAddPaidBy = findViewById(R.id.edtAddPaidBy);
        rcvCheckBoxMember = findViewById(R.id.rcvCheckBoxAddMemberTodo);
        lnlChkGroupExpense = findViewById(R.id.lnlChkGroupExpense);
//        spnCity = findViewById(R.id.spnCity);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvCheckBoxMember.setLayoutManager(linearLayoutManager);
    }


    public void initData() {
        Gson gson = new Gson();
        mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD);
        String groupCurrency = SharePreferenceUtils.getStringSharedPreference(AddTransactionActivity.this, GTABundle.GROUP_CURRENCY_SHARE);
        groupCurrencyDTO = gson.fromJson(groupCurrency, CurrencyDTO.class);
        currencyDTO = new CurrencyDTO();
        currencyDTO.setCode(groupCurrencyDTO.getCode());
        currencyDTO.setName(groupCurrencyDTO.getName());
        btnAddCurrency.setText(groupCurrencyDTO.getCode());
        groupId = SharePreferenceUtils.getIntSharedPreference(AddTransactionActivity.this, GTABundle.IDGROUP);
        listeneTransaction = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadTransactionUtc");
        listenerMemberBadges = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadMemberBadges");
        membersRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MEMBERS_CHILD).child(mUserId).child("lastestReadMessage");
        btnAddCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransactionActivity.this, CurrencyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "AddTransactionActivity");
                bundle.putSerializable(GTABundle.ADD_CURRENCY, currencyDTO);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.ADD_CURRENCY_CODE);
            }
        });
        edtAddTransactionType.setText("Expense");
        txtAmountGroupExpense.setText("0");
        transactionTypeNumber = 1;
        Date date = new Date();
        dateTransaction = ZonedDateTimeUtil.convertDateTimeToString(date);
        edtAddTransactionDate.setText(dateTransaction);
        Glide.with(AddTransactionActivity.this).load(R.mipmap.category).into(imgTransactionCategory);
        transactionTypeCategory = 1;
        int groupId = SharePreferenceUtils.getIntSharedPreference(AddTransactionActivity.this, GTABundle.IDGROUP);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(AddTransactionActivity.this, AddTransactionActivity.this);
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);

        imgAddDocumentTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransactionActivity.this, AddTransactionDocumentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.ADD_TRANSACTION_DOCUMENT, (Serializable) mDocumentDTOList);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.ADD_TRANSACTION_DOCUMENT_CODE);
            }
        });

        imgSaveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddTransactionPresenter = new AddTransactionPresenter(AddTransactionActivity.this, AddTransactionActivity.this);
                if (InternetHelper.isOnline(AddTransactionActivity.this) == false) {
                    DialogShowErrorMessage.showDialogNoInternet(AddTransactionActivity.this, "No Internetion");
                } else {
                    if (isValidTransaction()) {
                        transactionDTO.setIdType(transactionTypeNumber);
                        transactionDTO.setIdCategory(transactionTypeCategory);
                        transactionDTO.setName(name);
                        transactionDTO.setOccurAt(ZonedDateTimeUtil.convertStringToDateOrTime(dateTransaction));
                        transactionDTO.setTransactionDetailList(participantList);
                        transactionDTO.setDocumentList(mDocumentDTOList);
                        transactionDTO.setCurrency(currencyDTO);
                        transactionDTO.setDefaultCurrencyRate(defaultRate);
                        transactionDTO.setCustomCurrencyRate(customRate);
                        payerDetailDTO.setAmount(BigDecimal.valueOf(Double.valueOf(customTotal)));
                        payerDetailDTO.setMember(payerDTO);
                        TransactionHandler.preparedTransactionDTO(transactionDTO, payerDetailDTO, groupShareDetailDTO);
                        mAddTransactionPresenter.createTransaction(groupId, transactionDTO);

                    }
                }
            }
        });

        chkGroupExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkGroupExpense.isChecked()) {
                    String amount = edtAddAmount.getText().toString().replaceAll(",", "");
                    BigDecimal amountGroup = new BigDecimal(Double.parseDouble(amount));
                    txtAmountGroupExpense.setText(ChangeValue.formatBigCurrency(amountGroup));
                    groupShareDetailDTO.setAmount(amountGroup);
                    customeTransactionAdapter.divideAmount();
                    updateUI();
                } else {
                    groupShareDetailDTO.setAmount(BigDecimal.ZERO);
                    txtAmountGroupExpense.setText("0");
                    customeTransactionAdapter.divideAmount();
                    updateUI();
                }
            }
        });


        edtAddAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAmount();
            }
        });

        lnlCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });
        edtAddPaidBy.setFocusable(false);
        edtAddPaidBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaidByDialog();
            }
        });

        edtAddExpenseTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        edtAddRateCurrency.setFocusable(false);
        edtAddRateCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustomRate();
            }
        });

        edtAddTransactionDate.setFocusable(false);
        edtAddTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTransactionDate();
            }
        });

        txtAmountGroupExpense.setFocusable(false);
        txtAmountGroupExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAmountGroupExpense();
            }
        });

        imgAddTransactionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edtAddTransactionType.setFocusable(false);
        edtAddTransactionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionTypeDialog();
            }
        });


        updateConvertedAmountUI();

    }


    public void updateUI() {
        if (customeTransactionAdapter == null) {
            if (participantList.size() <= 0) {
                for (MemberDTO memberDTO : membersActiveList) {
                    participantList.add(new TransactionDTO.TransactionDetailDTO(0, BigDecimal.ZERO, memberDTO));
                    customeTransactionAdapter = new CustomeTransactionAdapter(participantList, this, payerDetailDTO, groupShareDetailDTO);
                    rcvCheckBoxMember.setAdapter(customeTransactionAdapter);
                    customeTransactionAdapter.notifyChange(participantList);
                }
            } else {
                customeTransactionAdapter = new CustomeTransactionAdapter(participantList, this, payerDetailDTO, groupShareDetailDTO);
                rcvCheckBoxMember.setAdapter(customeTransactionAdapter);
                customeTransactionAdapter.notifyChange(participantList);
            }

            customeTransactionAdapter.setOnItemTransactionClickListener(new CustomeTransactionAdapter.OnItemTransactionClickListener() {
                @Override
                public void onItemTransactionClickListener(TransactionDTO.TransactionDetailDTO transactionDetailDTO, int postion) {
                    dialogParticipantListOf(participantList, postion);
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

    private void showCategoryDialog() {
        final Dialog dialog = new Dialog(AddTransactionActivity.this);
        dialog.setContentView(R.layout.row_dialog_category);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtActivityType, txtAccomodation, txtTransportation, txtFoodAndBeverage;
        txtActivityType = dialog.findViewById(R.id.txtActivityType);
        txtAccomodation = dialog.findViewById(R.id.txtAccomodation);
        txtTransportation = dialog.findViewById(R.id.txtTransportation);
        txtFoodAndBeverage = dialog.findViewById(R.id.txtFandB);


        txtActivityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.ACTIVITY + "";
                Glide.with(AddTransactionActivity.this).load(R.mipmap.category).into(imgTransactionCategory);
                transactionTypeCategory = 1;
            }
        });

        txtAccomodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.ACCOMMODATION + "";
                Glide.with(AddTransactionActivity.this).load(R.mipmap.accomodation).into(imgTransactionCategory);
                transactionTypeCategory = 2;
            }
        });

        txtTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.TRANSPORTATION + "";
                Glide.with(AddTransactionActivity.this).load(R.mipmap.transaportationicon).into(imgTransactionCategory);
                transactionTypeCategory = 3;
            }
        });

        txtFoodAndBeverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transactionCategory = TransactionCategory.FOOD + "";
                Glide.with(AddTransactionActivity.this).load(R.mipmap.fastfood).into(imgTransactionCategory);
                transactionTypeCategory = 4;
            }
        });
        dialog.show();


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
                        dateTransaction = format.format(calendarStart.getTime());
                        edtAddTransactionDate.setText(dateTransaction);
                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean isValidTransaction() {
        strGroupExpense = txtAmountGroupExpense.getText().toString().replaceAll(",", "");
        name = edtAddExpenseTitle.getText().toString();
        type = edtAddTransactionType.getText().toString();
        payer = edtAddPaidBy.getText().toString();
        dateTransaction = edtAddTransactionDate.getText().toString();
        customTotal = edtAddAmount.getText().toString().replaceAll(",", "");
        rateFinal = edtAddRateCurrency.getText().toString().replaceAll(",", "");


        boolean result = true;

        //check participant size
        if (participantList.size() < 0) {
            showError("Transaction is not valid");
            result = false;
        }


        BigDecimal checkTotal = BigDecimal.ZERO;
        BigDecimal groupCost = BigDecimal.valueOf(Double.parseDouble(strGroupExpense));

        if (lnlConvertCurrency.getVisibility() == View.VISIBLE) {
            if (rateFinal.trim().length() <= 0 || rateFinal.toString().trim().equals(".") || rateFinal.trim().equals("0")) {
                showError("Please Input Rate Amount");
                result = false;
            } else {
                finalRateBigdecimal = BigDecimal.valueOf(Double.parseDouble(rateFinal));
                if (finalRateBigdecimal.compareTo(currencyConvert) != 0) {
                    customRate = finalRateBigdecimal;
                }
            }
        }


        if (customTotal.trim().length() <= 0) {
            showError("Please Input Total Amount");
            result = false;
        } else {
            checkTotal = new BigDecimal(customTotal);
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

        sumOfParticipant = sumOfParticipant.add(groupCost);
        if (sumOfParticipant.compareTo(
                BigDecimal.valueOf(Double.parseDouble(customTotal))) != 0) {
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


    public void showError(String error) {
        txtError.setText(error);
        txtError.setVisibility(View.VISIBLE);
    }

    public void dialogParticipantListOf(List<TransactionDTO.TransactionDetailDTO> mDetailDTOS, int pos) {
        final Dialog dialog = new Dialog(AddTransactionActivity.this);
        dialog.setContentView(R.layout.dialog_edit_add_transaction);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView mTxtName = dialog.findViewById(R.id.dialogName);
        EditText edtDialogTransactionMoney = dialog.findViewById(R.id.dialogTransactionMoney);
        Button btnDialogChangeTransaction = dialog.findViewById(R.id.dialog_button_editTransaction);
        Button btnDialogClose = dialog.findViewById(R.id.dialog_button_close_editTransaction);
        mTxtName.setText(mDetailDTOS.get(pos).getMember().getPerson().getName());
        edtDialogTransactionMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDialogTransactionMoney.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtDialogTransactionMoney.addTextChangedListener(new BudgetTextWatcher(edtDialogTransactionMoney));
        edtDialogTransactionMoney.setText(ChangeValue.formatBigCurrency(mDetailDTOS.get(pos).getAmount()));

        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDialogChangeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moneyInput = edtDialogTransactionMoney.getText().toString();
                if (moneyInput.matches("")) {
                    edtDialogTransactionMoney.setError("Invalid number");
                } else {
                    char ch1 = moneyInput.charAt(0);
                    if (moneyInput.trim().equals("") || moneyInput.length() <= 0 || ch1 == '.') {
                        edtDialogTransactionMoney.setError("Invalid Amount For Member");
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

    public void dialogAmountGroupExpense() {
        final Dialog dialog = new Dialog(AddTransactionActivity.this);
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
                        chkGroupExpense.setChecked(groupShareAmount.compareTo(BigDecimal.ZERO) != 0);
                        BigDecimal isGroupShareAmount = new BigDecimal(Double.parseDouble(strGroupExpense));
                        txtAmountGroupExpense.setText(ChangeValue.formatBigCurrency(isGroupShareAmount));
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


    public void dialogAmount() {
        final Dialog dialog = new Dialog(AddTransactionActivity.this);
        dialog.setContentView(R.layout.dialog_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText edtDialogAmount = dialog.findViewById(R.id.dialogTotalAmount);
        Button btnDialogChangeTransaction = dialog.findViewById(R.id.dialog_button_AmountOK);
        edtDialogAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDialogAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtDialogAmount.addTextChangedListener(new BudgetTextWatcher(edtDialogAmount));
        btnDialogChangeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAmount = edtDialogAmount.getText().toString();
                if (dialogAmount.matches("")) {
                    edtDialogAmount.setError("Invalid Amount");
                } else {
                    char ch1 = dialogAmount.charAt(0);
                    if (dialogAmount.trim().length() <= 0 || ch1 == '.' || dialogAmount.trim().equals("0") || dialogAmount.trim().toString().equals("")) {
                        edtDialogAmount.setError("Invalid Total Amount");
                    } else {
                        bigAmount = new BigDecimal(dialogAmount.replaceAll(",", ""));
                        if (bigAmount.compareTo(BigDecimal.ZERO) == 0) {
                            edtDialogAmount.setError("Please input total Amount > 0");
                        } else {
                            edtAddAmount.setText(dialogAmount);
                            payerDetailDTO.setAmount(BigDecimal.valueOf(Double.parseDouble(dialogAmount.replaceAll(",", ""))));
                            customeTransactionAdapter.divideAmount();
                            updateConvertedAmountUI();
                            updateUI();
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

    public void dialogCustomRate() {
        final Dialog dialog = new Dialog(AddTransactionActivity.this);
        dialog.setContentView(R.layout.dialog_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText edtDialogCustomRate = dialog.findViewById(R.id.dialogTotalAmount);
        edtDialogCustomRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDialogCustomRate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtDialogCustomRate.addTextChangedListener(new BudgetTextWatcher(edtDialogCustomRate, 9));
        Button btnDialogChangeTransaction = dialog.findViewById(R.id.dialog_button_AmountOK);
        btnDialogChangeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal checkBigDecimal;
                String dialogCustomRate = edtDialogCustomRate.getText().toString().replaceAll(",", "");
                if (dialogCustomRate.matches("")) {
                    edtDialogCustomRate.setError("Invalid Rate");
                } else {
                    char ch1 = dialogCustomRate.charAt(0);
                    if (dialogCustomRate.trim().length() <= 0 || ch1 == '.' || dialogCustomRate.trim().equals("0")) {
                        edtDialogCustomRate.setError("Invalid Rate");
                    } else {
                        checkBigDecimal = new BigDecimal(dialogCustomRate);
                        if (checkBigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                            edtDialogCustomRate.setError("Please input rate  > 0");
                        } else {
                            edtAddRateCurrency.setText(dialogCustomRate);
                            customRate = BigDecimal.valueOf(Double.parseDouble(dialogCustomRate));
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
        valueAmount = edtAddAmount.getText().toString().replaceAll(",", "");
        if (lnlConvertCurrency.getVisibility() == View.VISIBLE) {
            if (customRate.compareTo(BigDecimal.ZERO) == 0) {
                txtAddConvertAmount.setText(ChangeValue.formatBigCurrency(defaultRate.multiply(BigDecimal.valueOf(Double.parseDouble(valueAmount)))));
            } else {
                txtAddConvertAmount.setText(ChangeValue.formatBigCurrency(customRate.multiply(BigDecimal.valueOf(Double.parseDouble(valueAmount)))));
            }
        }
    }

    private void showPaidByDialog() {
        final Dialog dialog = new Dialog(AddTransactionActivity.this);
        dialog.setContentView(R.layout.row_dialog_paidby);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rcvMemberPaidBy = dialog.findViewById(R.id.rcvCheckBoxPaidByMember);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(dialog.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvMemberPaidBy.setLayoutManager(linearLayoutManager);
        memberTransactionDialogAdapter = new MemberTransactionDialogAdapter(AddTransactionActivity.this, membersActiveList);
        rcvMemberPaidBy.setAdapter(memberTransactionDialogAdapter);
        memberTransactionDialogAdapter.setOnEditItemClickListener(new MemberTransactionDialogAdapter.OnItemEditClickListener() {
            @Override
            public void onItemEditClickListener(MemberDTO memberDTO, int position) {
                dialog.dismiss();
                edtAddPaidBy.setText(membersActiveList.get(position).getPerson().getName() + "");
                payerDTO = memberDTO;
            }
        });
        dialog.show();
    }

    private void showTransactionTypeDialog() {
        final Dialog dialog = new Dialog(AddTransactionActivity.this);
        dialog.setContentView(R.layout.row_dialog_transactiontype);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTransfer = dialog.findViewById(R.id.txtTransfer);
        TextView txtExpense = dialog.findViewById(R.id.txtExpense);
//        TextView txtIncome = dialog.findViewById(R.id.txtIncome);
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
                trasanctionType = TransactionType.EXPENSE + "";
                edtAddTransactionType.setText("Expense");
                transactionTypeNumber = 1;
                lnlChkGroupExpense.setVisibility(View.VISIBLE);
                groupShareDetailDTO.setAmount(BigDecimal.ZERO);
                txtAmountGroupExpense.setText("0");
                customeTransactionAdapter.divideAmount();

            }
        });

        txtTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                trasanctionType = TransactionType.TRANSFER + "";
                edtAddTransactionType.setText("Transfer");
                transactionTypeNumber = 2;
                lnlChkGroupExpense.setVisibility(View.GONE);
                groupShareDetailDTO.setAmount(BigDecimal.ZERO);
                txtAmountGroupExpense.setText("0");
                customeTransactionAdapter.divideAmount();
            }
        });

//        txtIncome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                trasanctionType = TransactionType.INCOME + "";
//                edtAddTransactionType.setText("Income");
//                transactionTypeNumber = 3;
//                lnlChkGroupExpense.setVisibility(View.GONE);
//                groupShareDetailDTO.setAmount(BigDecimal.ZERO);
//                txtAmountGroupExpense.setText("0");
//                customeTransactionAdapter.divideAmount();
//            }
//        });
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

            String id = auth.getUid();
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey("Calling_Transaction")) {
                    transactionDTO = (TransactionDTO) bundle.getSerializable("PayBackFlow");
                    participantList = TransactionHandler.participantListOf(transactionDTO);
                    payerDetailDTO = TransactionHandler.payerDetailOf(transactionDTO);
                    transactionTypeNumber = 2;
                    payerDTO = payerDetailDTO.getMember();
                    for (int i = 0; i < participantList.size(); i++) {
                        participantList.get(i).setSelected(true);
                    }
                    edtAddPaidBy.setText(payerDTO.getPerson().getName());
                    disableEditText(edtAddPaidBy);
                    edtAddTransactionType.setText("Transfer");
                    lnlChkGroupExpense.setVisibility(View.GONE);
                    disableEditText(edtAddTransactionType);
                    edtAddAmount.setText(ChangeValue.formatBigCurrency(payerDetailDTO.getAmount().abs()));
                } else if (bundle.containsKey("Calling_OcrTransaction")) {
                    transactionDTO = new TransactionDTO();
//                    transactionDTO.setOwner(isMe);
                    ocrBill = bundle.getString("OcrBill");
                    documentDTO = (DocumentDTO) bundle.getSerializable("OcrDocument");
                    mDocumentDTOList.add(documentDTO);
                    BigDecimal ocrAmount = new BigDecimal(String.valueOf(ocrBill.replaceAll(",", "")));
                    edtAddAmount.setText(ChangeValue.formatOcrMoney(ocrAmount));
                    transactionTypeNumber = 1;
                    edtAddTransactionType.setText("Expense");
                    payerDetailDTO = new TransactionDTO.TransactionDetailDTO();
                    payerDetailDTO.setAmount(ocrAmount);
                    updateUI();
                    customeTransactionAdapter.divideAmount();
                    for (int i = 0; i < membersActiveList.size(); i++) {
                        if (membersActiveList.get(i).getPerson().getFirebaseUid().equals(id)) {
                            payerDTO = membersActiveList.get(i);
                            edtAddPaidBy.setText(membersActiveList.get(i).getPerson().getName() + "");
                        }
                    }
                }
            } else {
                transactionDTO = new TransactionDTO();
                payerDetailDTO = new TransactionDTO.TransactionDetailDTO();
                participantList = new ArrayList<>();
                for (int i = 0; i < membersActiveList.size(); i++) {
                    if (membersActiveList.get(i).getPerson().getFirebaseUid().equals(id)) {
                        payerDTO = membersActiveList.get(i);
                        edtAddPaidBy.setText(membersActiveList.get(i).getPerson().getName() + "");
                    }
                }
                payerDetailDTO.setAmount(BigDecimal.ZERO);
                payerDetailDTO.setMember(payerDTO);
            }

            updateUI();
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GTABundle.ADD_TRANSACTION_DOCUMENT_CODE) {
            if (resultCode == RESULT_OK) {
                mDocumentDTOList = (List<DocumentDTO>) data.getSerializableExtra(GTABundle.ADD_TRANSACTION_DOCUMENT);
            }
        } else if (requestCode == GTABundle.ADD_CURRENCY_CODE) {
            if (resultCode == RESULT_OK) {
                currencyDTO = (CurrencyDTO) data.getSerializableExtra(GTABundle.ADD_CURRENCY);
                btnAddCurrency.setText(currencyDTO.getCode());
                if (!currencyDTO.getName().equalsIgnoreCase(groupCurrencyDTO.getName())) {
                    lnlConvertCurrency.setVisibility(View.VISIBLE);
                    mConvertCurrencyPresenter = new ConvertCurrencyPresenter(AddTransactionActivity.this, AddTransactionActivity.this);
                    mConvertCurrencyPresenter.convertCurrencyMoney(currencyDTO.getCode(), groupCurrencyDTO.getCode());
                } else if (currencyDTO.getName().equalsIgnoreCase(groupCurrencyDTO.getName())) {
                    lnlConvertCurrency.setVisibility(View.GONE);
                    customRate = BigDecimal.ZERO;
                }
            }
        }
    }

    @Override
    public void PrintMemberFail(String message) {
        DialogShowErrorMessage.showDialogNoInternet(this, "No Internet");
    }

    @Override
    public void AddTransactionSuccess(String success) {
        if (groupShareDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            TimeZone tz = TimeZone.getDefault();
            String timez = tz.getID();
            try {
                Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                Long change = dateNoti.getTime();
                listeneTransaction.setValue(change);
                messageTime = dateNoti.getTime();
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(ownerName + " " + "created new group transaction name:" + " " + edtAddExpenseTitle.getText().toString() + "." + "\nTotal Amount:" + edtAddAmount.getText().toString(),
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
        bundle.putString("OcrBill", "OK");
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void AddTransactionFail(String fail) {
        DialogShowErrorMessage.showValidationDialog(this, fail);
    }

    @Override
    public void convertCurrencySuccess(BigDecimal bigDecimal) {
        currencyConvert = bigDecimal;
        defaultRate = bigDecimal;
        customRate = BigDecimal.ZERO;
        edtAddRateCurrency.setText(ChangeValue.formatRateCurrency(currencyConvert));
        BigDecimal result = bigAmount.multiply(currencyConvert);
        updateConvertedAmountUI();
        txtAddRateCurrency.setText("Rate (1" + " " + currencyDTO.getCode() + " " + "in" + " " + groupCurrencyDTO.getCode() + ")");
    }


    @Override
    public void convertCurrencyFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }

}
