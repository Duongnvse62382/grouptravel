package com.fpt.gta.feature.managebudget;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.presenter.MakePendingPresenter;
import com.fpt.gta.presenter.PrintGroupByIdPresenter;
import com.fpt.gta.presenter.UpdateGroupBudgetPresenter;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.CurrencyTextWatcher;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.InternetHelper;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.MakePendingView;
import com.fpt.gta.view.PrintGroupByIdView;
import com.fpt.gta.view.UpdateGroupBudgetView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;

public class ConfirmBudgetGroupActivity extends AppCompatActivity implements PrintGroupByIdView, MakePendingView {
    private ImageView imgBackConfirmBudgetGroup;
    private Button btnConfirmGroupBudget, btnMakePending;
    private TextInputEditText txtConfirmGroupActivityBudget, txtConfirmGroupAccommodationBudget, txtConfirmGroupTransportationBudget, txtConfirmGroupFoodBudget;
    private TextView txtConfirmCurrencyPerPax1, txtConfirmCurrencyPerPax2, txtConfirmCurrencyPerPax3, txtConfirmCurrencyPerPax4;
    private final int MAX_LENGTH = 12;
    private GroupResponseDTO mGroupResponseDTO;
//    private UpdateGroupBudgetPresenter mUpdateGroupBudgetPresenter;
    private PrintGroupByIdPresenter mPrintGroupByIdPresenter;
    private MakePendingPresenter mMakePendingPresenter;
    private int groupId;
    private DatabaseReference reloadGroupStatus;
    private DatabaseReference mFirebaseDatabaseReference;
    private BigDecimal bigDecimalActivity = BigDecimal.ZERO;
    private BigDecimal bigDecimalAccommodation = BigDecimal.ZERO;
    private BigDecimal bigDecimalTransportation = BigDecimal.ZERO;
    private BigDecimal bigDecimalFood = BigDecimal.ZERO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_budget_group);
        initView();
    }

    private void initView() {
        imgBackConfirmBudgetGroup = findViewById(R.id.imgBackConfirmBudgetGroup);
        DialogShowErrorMessage.showValidationDialog(ConfirmBudgetGroupActivity.this, "Please Confirm Budget Group Before Start Journey");
//        btnConfirmGroupBudget = findViewById(R.id.btnConfirmSubmitGroupBudget);
        btnMakePending = findViewById(R.id.btnMakePending);
        txtConfirmCurrencyPerPax1 = findViewById(R.id.txtConfirmCurrencyPerPax);
        txtConfirmCurrencyPerPax2 = findViewById(R.id.txtConfirmCurrencyPerPax2);
        txtConfirmCurrencyPerPax3 = findViewById(R.id.txtConfirmCurrencyPerPax3);
        txtConfirmCurrencyPerPax4 = findViewById(R.id.txtConfirmCurrencyPerPax4);
        txtConfirmGroupActivityBudget = findViewById(R.id.edtConfirmBudgetGroupActivity);
        txtConfirmGroupAccommodationBudget = findViewById(R.id.edtConfirmBudgetGroupAccommodation);
        txtConfirmGroupTransportationBudget = findViewById(R.id.edtConfirmBudgetGroupTransportation);
        txtConfirmGroupFoodBudget = findViewById(R.id.edtConfirmBudgetGroupFood);
        mPrintGroupByIdPresenter = new PrintGroupByIdPresenter(ConfirmBudgetGroupActivity.this, this);
//        mUpdateGroupBudgetPresenter = new UpdateGroupBudgetPresenter(ConfirmBudgetGroupActivity.this, this);
        groupId = SharePreferenceUtils.getIntSharedPreference(ConfirmBudgetGroupActivity.this, GTABundle.IDGROUP);
        mMakePendingPresenter = new MakePendingPresenter(ConfirmBudgetGroupActivity.this, this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        reloadGroupStatus = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadGroupStatus");
        loadGroup();
    }

    public void loadGroup() {
        mPrintGroupByIdPresenter.getGroupById(groupId);
    }

    public void makePending() {
        mMakePendingPresenter.makePending(groupId, mGroupResponseDTO);
    }

//    public void updateGroupBudget() {
//        mUpdateGroupBudgetPresenter.updateGroupBudgetPresenter(groupId, mGroupResponseDTO);
//    }

    private void initData() {
        txtConfirmGroupActivityBudget.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getActivityBudget()));
        txtConfirmGroupAccommodationBudget.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getAccommodationBudget()));
        txtConfirmGroupTransportationBudget.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getTransportationBudget()));
        txtConfirmGroupFoodBudget.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getFoodBudget()));

        txtConfirmCurrencyPerPax1.setText(mGroupResponseDTO.getCurrency().getCode());
        txtConfirmCurrencyPerPax2.setText(mGroupResponseDTO.getCurrency().getCode());
        txtConfirmCurrencyPerPax3.setText(mGroupResponseDTO.getCurrency().getCode());
        txtConfirmCurrencyPerPax4.setText(mGroupResponseDTO.getCurrency().getCode());

        imgBackConfirmBudgetGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtConfirmGroupActivityBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtConfirmGroupActivityBudget.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        txtConfirmGroupActivityBudget.addTextChangedListener(new CurrencyTextWatcher(txtConfirmGroupActivityBudget));

        txtConfirmGroupAccommodationBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtConfirmGroupAccommodationBudget.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        txtConfirmGroupAccommodationBudget.addTextChangedListener(new CurrencyTextWatcher(txtConfirmGroupAccommodationBudget));

        txtConfirmGroupTransportationBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtConfirmGroupTransportationBudget.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        txtConfirmGroupTransportationBudget.addTextChangedListener(new CurrencyTextWatcher(txtConfirmGroupTransportationBudget));

        txtConfirmGroupFoodBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtConfirmGroupFoodBudget.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        txtConfirmGroupFoodBudget.addTextChangedListener(new CurrencyTextWatcher(txtConfirmGroupFoodBudget));


        btnMakePending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidGroupBudget()) {
                    mGroupResponseDTO.setActivityBudget(bigDecimalActivity);
                    mGroupResponseDTO.setAccommodationBudget(bigDecimalAccommodation);
                    mGroupResponseDTO.setTransportationBudget(bigDecimalTransportation);
                    mGroupResponseDTO.setFoodBudget(bigDecimalFood);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmBudgetGroupActivity.this);
                    builder.setMessage("Are you sure to make pending journey?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (InternetHelper.isOnline(ConfirmBudgetGroupActivity.this) == false) {
                                DialogShowErrorMessage.showDialogNoInternet(ConfirmBudgetGroupActivity.this, "No Connection");
                            } else {
                                makePending();
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
            }
        });

//        btnConfirmGroupBudget.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isValidGroupBudget()) {
//                    mGroupResponseDTO.setActivityBudget(bigDecimalActivity);
//                    mGroupResponseDTO.setAccommodationBudget(bigDecimalAccommodation);
//                    mGroupResponseDTO.setTransportationBudget(bigDecimalTransportation);
//                    mGroupResponseDTO.setFoodBudget(bigDecimalFood);
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmBudgetGroupActivity.this);
//                    builder.setMessage("Are you sure to update group budget?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (InternetHelper.isOnline(ConfirmBudgetGroupActivity.this) == false) {
//                                DialogShowErrorMessage.showDialogNoInternet(ConfirmBudgetGroupActivity.this, "No Connection");
//                            } else {
//                                updateGroupBudget();
//                            }
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
//                }
//            }
//        });
    }

    public boolean isValidGroupBudget() {
        String valueActivity = txtConfirmGroupActivityBudget.getText().toString().replaceAll(",", "");
        String valueAccommodation = txtConfirmGroupAccommodationBudget.getText().toString().replaceAll(",", "");
        String valueTransportation = txtConfirmGroupTransportationBudget.getText().toString().replaceAll(",", "");
        String valueFood = txtConfirmGroupFoodBudget.getText().toString().replaceAll(",", "");


        boolean result = true;

        if (valueActivity.matches("")) {
            txtConfirmGroupActivityBudget.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueActivity.charAt(0);
            if (valueActivity.trim().length() <= 0 || valueActivity.toString().trim().equals(".") || ch1 == '.') {
                txtConfirmGroupActivityBudget.setError("Invalid number");
                result = false;
            } else {
                bigDecimalActivity = BigDecimal.valueOf(Double.parseDouble(valueActivity));
            }
        }


        if (valueAccommodation.matches("")) {
            txtConfirmGroupAccommodationBudget.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueAccommodation.charAt(0);
            if (valueAccommodation.trim().length() <= 0 || valueAccommodation.toString().trim().equals(".") || ch1 == '.') {
                txtConfirmGroupAccommodationBudget.setError("Invalid number");
                result = false;
            } else {
                bigDecimalAccommodation = BigDecimal.valueOf(Double.parseDouble(valueAccommodation));
            }
        }


        if (valueTransportation.matches("")) {
            txtConfirmGroupTransportationBudget.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueTransportation.charAt(0);
            if (valueTransportation.trim().length() <= 0 || valueTransportation.toString().trim().equals(".") || ch1 == '.') {
                txtConfirmGroupTransportationBudget.setError("Invalid number");
                result = false;
            } else {
                bigDecimalTransportation = BigDecimal.valueOf(Double.parseDouble(valueTransportation));
            }

        }

        if (valueFood.matches("")) {
            txtConfirmGroupFoodBudget.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueFood.charAt(0);
            if (valueFood.trim().length() <= 0 || valueFood.toString().trim().equals(".") || ch1 == '.') {
                txtConfirmGroupFoodBudget.setError("Invalid number");
                result = false;
            } else {
                bigDecimalFood = BigDecimal.valueOf(Double.parseDouble(valueFood));
            }
        }


        return result;

    }


    @Override
    public void printGroupByIdSS(GroupResponseDTO groupResponseDTO) {
        if (groupResponseDTO != null) {
            mGroupResponseDTO = groupResponseDTO;
        }
        initData();
    }


    @Override
    public void printGroupByIdFail(String messageFail) {

    }

    @Override
    public void makePendingSuccess(String messageSuccess) {
        Toast.makeText(this, messageSuccess, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void makePendingFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(ConfirmBudgetGroupActivity.this, messageFail);
    }
}