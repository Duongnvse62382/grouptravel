package com.fpt.gta.feature.managebudget;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.fpt.gta.R;

import com.fpt.gta.data.dto.BudgetOverViewDTO;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.TransactionDTO;

import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.MemberStatus;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.presenter.GetMyBudgetPresenter;
import com.fpt.gta.presenter.PrintAllTransactionPresenter;
import com.fpt.gta.presenter.PrintGroupByIdPresenter;
import com.fpt.gta.presenter.UpdateGroupBudgetPresenter;
import com.fpt.gta.presenter.UpdateMyBudgetPresenter;
import com.fpt.gta.util.BudgetTextWatcher;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.InternetHelper;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.GetMyBudgetView;
import com.fpt.gta.view.PrintAllTransactionView;
import com.fpt.gta.view.PrintGroupByIdView;
import com.fpt.gta.view.UpdateGroupBudgetView;
import com.fpt.gta.view.UpdateMyBudgetView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment implements PrintAllTransactionView, UpdateGroupBudgetView, UpdateMyBudgetView, PrintGroupByIdView, GetMyBudgetView {
    private View mView;
    private int groupId;
    private String userId;
    private TextView txtCurrencyPerPax1, txtCurrencyPerPax2, txtCurrencyPerPax3, txtCurrencyPerPax4;
    private TextView txtCurrencyIndividualActivity, txtCurrencyIndividualAccommodation, txtCurrencyIndividualTransportation, txtCurrencyIndividualFood;

    private TextView txtBudgetGroupActivityOverload, txtBudgetGroupAccommodationOverload, txtBudgetGroupTransportationOverload, txtBudgetGroupFoodOverload;

    private TextView txtMyBudgetActivityPerPaxOverload, txtMyBudgetAccommodationPerPaxOverload, txtMyBudgetTransportationPerPaxOverload, txtMyBudgetFoodPerPaxOverload;

    private TextView txtBudgetMyIndividualActivityOverload, txtBudgetMyIndividualAccommodationOverload, txtBudgetMyIndividualTransportationOverload, txtBudgetMyIndividualFoodOverload;

    private IconRoundCornerProgressBar progressBarGroupActivity, progressBarGroupAccommodation, progressBarGroupTransportation, progressBarGroupFood;

    private IconRoundCornerProgressBar progressBarMyBudgetActivityPerPax, progressBarMyBudgetAccommodationPerPax, progressBarMyBudgetTransportationPerPax, progressBarMyBudgetFoodPerPax;

    private IconRoundCornerProgressBar progressBarMyIndividualActivity, progressBarMyIndividualAccommodation, progressBarMyIndividualTransportation, progressBarMyIndividualFood;

    private TextView txtValueGroupActivityProcess, txtValueGroupAccommodationProcess, txtValueGroupTransportationProcess, txtValueGroupFoodProcess;

    private TextView txtValueMyBudgetActivityPerPaxProcess, txtValueMyBudgetAccommodationPerPaxProcess, txtValueMyBudgetTransportationPerPaxProcess, txtValueMyBudgetFoodPerPaxProcess;
    private TextView txtMyActualPerPaxCost, txtMyIndividualActualCost, txtGroupActualCost;

    private TextView txtValueIndividualActivityProcess, txtValueIndividualAccommodationProcess, txtValueIndividualTransportationProcess, txtValueIndividualFoodProcess;

    private CheckBox chkEditBudget;
    private EditText edtBudgetGroupActivity, edtBudgetGroupAccommodation, edtBudgetGroupTransportation, edtBudgetGroupFood;
    private EditText edtBudgetIndividualActivity, edtBudgetIndividualAccommodation, edtBudgetIndividualTransportation, edtBudgetIndividualFood;
    private LinearLayout lnlGroupBudget, lnlMyBudget;
    private PrintAllTransactionPresenter mPrintAllTransactionPresenter;
    private UpdateGroupBudgetPresenter mUpdateGroupBudgetPresenter;
    private UpdateMyBudgetPresenter myBudgetPresenter;
    private Integer idTrip;
    private BudgetOverViewDTO budgetOverViewGroupCost;
    private BudgetOverViewDTO budgetOverViewMyImpactInGroupCost;
    private BudgetOverViewDTO budgetOverViewMyIndividualCost;

    private Button btnSubmitGroupBudget, btnSubmitMyBudget;
    private MemberDTO memberCurrent = new MemberDTO();
    private GroupResponseDTO mGroupResponseDTO = new GroupResponseDTO();
    private final int MAX_LENGTH = 12;
    private PrintGroupByIdPresenter mPrintGroupByIdPresenter;
    private GetMyBudgetPresenter getMyBudgetPresenter;
    private List<TransactionDTO> groupTransactionList;
    private List<TransactionDTO> myTransactionList;

    private BigDecimal bigDecimalMyActivity = BigDecimal.ZERO;
    private BigDecimal bigDecimalMyAccommodation = BigDecimal.ZERO;
    private BigDecimal bigDecimalMyTransportation = BigDecimal.ZERO;
    private BigDecimal bigDecimalMyFood = BigDecimal.ZERO;

    private BigDecimal bigDecimalGroupBudgetActivity = BigDecimal.ZERO;
    private BigDecimal bigDecimalGroupBudgetAccommodation = BigDecimal.ZERO;
    private BigDecimal bigDecimalGroupBudgetTransportation = BigDecimal.ZERO;
    private BigDecimal bigDecimalGroupBudgetFood = BigDecimal.ZERO;
    private int memberSize;
    private FirebaseDatabase databaseTransaction;
    private DatabaseReference listenerTransaction;
    private ValueEventListener transactionValueEventListener;
    private ImageView imgRefreshBudget;

    public BudgetFragment() {
    }

    public static BudgetFragment newInstance() {
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_budget, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGroup();
    }

    public void initView() {
        imgRefreshBudget= mView.findViewById(R.id.imgRefreshBudget);
        chkEditBudget = mView.findViewById(R.id.chkEditBudget);
        lnlMyBudget = mView.findViewById(R.id.lnlMyBudget);
        lnlGroupBudget = mView.findViewById(R.id.lnlGroupBudget);

        txtGroupActualCost = mView.findViewById(R.id.txtGroupActualCost);

        txtMyActualPerPaxCost = mView.findViewById(R.id.txtActualCostPerPax);

        txtMyIndividualActualCost = mView.findViewById(R.id.txtMyIndividualActualCost);

        //overload group budget
        txtBudgetGroupActivityOverload = mView.findViewById(R.id.txtBudgetGroupActivityOverload);
        txtBudgetGroupAccommodationOverload = mView.findViewById(R.id.txtBudgetGroupAccommodationOverload);
        txtBudgetGroupTransportationOverload = mView.findViewById(R.id.txtBudgetGroupTransportationOverload);
        txtBudgetGroupFoodOverload = mView.findViewById(R.id.txtBudgetGroupFoodOverload);

        //overload my budget per pax
        txtMyBudgetActivityPerPaxOverload = mView.findViewById(R.id.txtBudgetPerPaxActivityOverload);
        txtMyBudgetAccommodationPerPaxOverload = mView.findViewById(R.id.txtBudgetPerPaxAccommodationOverload);
        txtMyBudgetTransportationPerPaxOverload = mView.findViewById(R.id.txtBudgePerPaxTransportationOverload);
        txtMyBudgetFoodPerPaxOverload = mView.findViewById(R.id.txtBudgetPerPaxFoodOverload);

        //overload my individual
        txtBudgetMyIndividualActivityOverload = mView.findViewById(R.id.txtBudgetMyIndividualActivityOverload);
        txtBudgetMyIndividualAccommodationOverload = mView.findViewById(R.id.txtBudgetMyIndividualAccommodationOverload);
        txtBudgetMyIndividualTransportationOverload = mView.findViewById(R.id.txtBudgetMyIndividualTransportationOverload);
        txtBudgetMyIndividualFoodOverload = mView.findViewById(R.id.txtBudgetMyIndividualFoodOverload);

        //Group Cost
        progressBarGroupActivity = mView.findViewById(R.id.progressBarGroupActivity);
        progressBarGroupAccommodation = mView.findViewById(R.id.progressBarGroupAccommodation);
        progressBarGroupTransportation = mView.findViewById(R.id.progressBarGroupTransportation);
        progressBarGroupFood = mView.findViewById(R.id.progressBarGroupFood);

        //Individual For Group Cost
        progressBarMyBudgetActivityPerPax = mView.findViewById(R.id.progressBarPerPaxActivity);
        progressBarMyBudgetAccommodationPerPax = mView.findViewById(R.id.progressBarPerPaxAccommodation);
        progressBarMyBudgetTransportationPerPax = mView.findViewById(R.id.progressBarPerPaxTransportation);
        progressBarMyBudgetFoodPerPax = mView.findViewById(R.id.progressBarPerPaxFood);

        //Individual
        progressBarMyIndividualActivity = mView.findViewById(R.id.progressBarMyIndividualActivity);
        progressBarMyIndividualAccommodation = mView.findViewById(R.id.progressBarMyIndividualAccommodation);
        progressBarMyIndividualTransportation = mView.findViewById(R.id.progressBarMyIndividualTransportation);
        progressBarMyIndividualFood = mView.findViewById(R.id.progressBarMyIndividualFood);

        //group process
        txtValueGroupActivityProcess = mView.findViewById(R.id.txtValueGroupActivityProcess);
        txtValueGroupAccommodationProcess = mView.findViewById(R.id.txtValueGroupAccommodationProcess);
        txtValueGroupTransportationProcess = mView.findViewById(R.id.txtValueGroupTransportationProcess);
        txtValueGroupFoodProcess = mView.findViewById(R.id.txtValueGroupFoodProcess);

        //per pax process
        txtValueMyBudgetActivityPerPaxProcess = mView.findViewById(R.id.txtValuePerPaxActivityProcess);
        txtValueMyBudgetAccommodationPerPaxProcess = mView.findViewById(R.id.txtValuePerPaxAccommodationProcess);
        txtValueMyBudgetTransportationPerPaxProcess = mView.findViewById(R.id.txtValuePerPaxTransportationProcess);
        txtValueMyBudgetFoodPerPaxProcess = mView.findViewById(R.id.txtValuePerPaxFoodProcess);

        //individual process
        txtValueIndividualActivityProcess = mView.findViewById(R.id.txtValueIndividualActivityProcess);
        txtValueIndividualAccommodationProcess = mView.findViewById(R.id.txtValueIndividualAccommodationProcess);
        txtValueIndividualTransportationProcess = mView.findViewById(R.id.txtValueIndividualTransportationProcess);
        txtValueIndividualFoodProcess = mView.findViewById(R.id.txtValueIndividualFoodProcess);


        txtCurrencyPerPax1 = mView.findViewById(R.id.txtCurrencyPerPax);
        txtCurrencyPerPax2 = mView.findViewById(R.id.txtCurrencyPerPax2);
        txtCurrencyPerPax3 = mView.findViewById(R.id.txtCurrencyPerPax3);
        txtCurrencyPerPax4 = mView.findViewById(R.id.txtCurrencyPerPax4);

        txtCurrencyIndividualActivity = mView.findViewById(R.id.txtCurrencyIndividualActivity);
        txtCurrencyIndividualAccommodation = mView.findViewById(R.id.txtCurrencyIndividualAccommodation);
        txtCurrencyIndividualTransportation = mView.findViewById(R.id.txtCurrencyIndividualTransportation);
        txtCurrencyIndividualFood = mView.findViewById(R.id.txtCurrencyIndividualFood);

        btnSubmitMyBudget = mView.findViewById(R.id.btnSubmitMyBudget);
        btnSubmitGroupBudget = mView.findViewById(R.id.btnSubmitGroupBudget);

        edtBudgetGroupActivity = mView.findViewById(R.id.edtBudgetGroupActivity);
        edtBudgetGroupAccommodation = mView.findViewById(R.id.edtBudgetGroupAccommodation);
        edtBudgetGroupTransportation = mView.findViewById(R.id.edtBudgetGroupTransportation);
        edtBudgetGroupFood = mView.findViewById(R.id.edtBudgetGroupFood);

        edtBudgetIndividualActivity = mView.findViewById(R.id.edtBudgetIndividualActivity);
        edtBudgetIndividualAccommodation = mView.findViewById(R.id.edtBudgetIndividualAccommodation);
        edtBudgetIndividualTransportation = mView.findViewById(R.id.edtBudgetIndividualTransportation);
        edtBudgetIndividualFood = mView.findViewById(R.id.edtBudgetIndividualFood);

        disableEditText(edtBudgetGroupActivity);
        disableEditText(edtBudgetGroupAccommodation);
        disableEditText(edtBudgetGroupTransportation);
        disableEditText(edtBudgetGroupFood);

        btnSubmitGroupBudget.setVisibility(View.GONE);
        lnlGroupBudget.setVisibility(View.GONE);
        lnlMyBudget.setVisibility(View.GONE);

        edtBudgetGroupActivity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetGroupActivity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetGroupActivity.addTextChangedListener(new BudgetTextWatcher(edtBudgetGroupActivity));

        edtBudgetGroupAccommodation.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetGroupAccommodation.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetGroupAccommodation.addTextChangedListener(new BudgetTextWatcher(edtBudgetGroupAccommodation));

        edtBudgetGroupTransportation.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetGroupTransportation.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetGroupTransportation.addTextChangedListener(new BudgetTextWatcher(edtBudgetGroupTransportation));

        edtBudgetGroupFood.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetGroupFood.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetGroupFood.addTextChangedListener(new BudgetTextWatcher(edtBudgetGroupFood));

        edtBudgetIndividualActivity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetIndividualActivity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetIndividualActivity.addTextChangedListener(new BudgetTextWatcher(edtBudgetIndividualActivity));

        edtBudgetIndividualAccommodation.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetIndividualAccommodation.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetIndividualAccommodation.addTextChangedListener(new BudgetTextWatcher(edtBudgetIndividualAccommodation));

        edtBudgetIndividualTransportation.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetIndividualTransportation.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetIndividualTransportation.addTextChangedListener(new BudgetTextWatcher(edtBudgetIndividualTransportation));

        edtBudgetIndividualFood.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBudgetIndividualFood.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        edtBudgetIndividualFood.addTextChangedListener(new BudgetTextWatcher(edtBudgetIndividualFood));

        groupId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);

        mPrintAllTransactionPresenter = new PrintAllTransactionPresenter(getContext(), this);
        mUpdateGroupBudgetPresenter = new UpdateGroupBudgetPresenter(getContext(), this);
        myBudgetPresenter = new UpdateMyBudgetPresenter(getContext(), this);
        mPrintGroupByIdPresenter = new PrintGroupByIdPresenter(getContext(), this);
        getMyBudgetPresenter = new GetMyBudgetPresenter(getContext(), this);
        databaseTransaction = FirebaseDatabase.getInstance();
        listenerTransaction = databaseTransaction.getReference(String.valueOf(groupId)).child("listener").child("reloadTransactionUtc");

    }


    public void initData() {
        edtBudgetGroupActivity.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getActivityBudget()));
        edtBudgetGroupAccommodation.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getAccommodationBudget()));
        edtBudgetGroupTransportation.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getTransportationBudget()));
        edtBudgetGroupFood.setText(ChangeValue.formatBigCurrency(mGroupResponseDTO.getFoodBudget()));

        edtBudgetIndividualActivity.setText(ChangeValue.formatBigCurrency(memberCurrent.getActivityBudget()));
        edtBudgetIndividualAccommodation.setText(ChangeValue.formatBigCurrency(memberCurrent.getAccommodationBudget()));
        edtBudgetIndividualTransportation.setText(ChangeValue.formatBigCurrency(memberCurrent.getTransportationBudget()));
        edtBudgetIndividualFood.setText(ChangeValue.formatBigCurrency(memberCurrent.getFoodBudget()));

        txtCurrencyPerPax1.setText(mGroupResponseDTO.getCurrency().getCode());
        txtCurrencyPerPax2.setText(mGroupResponseDTO.getCurrency().getCode());
        txtCurrencyPerPax3.setText(mGroupResponseDTO.getCurrency().getCode());
        txtCurrencyPerPax4.setText(mGroupResponseDTO.getCurrency().getCode());

        txtCurrencyIndividualActivity.setText(mGroupResponseDTO.getCurrency().getCode());
        txtCurrencyIndividualAccommodation.setText(mGroupResponseDTO.getCurrency().getCode());
        txtCurrencyIndividualTransportation.setText(mGroupResponseDTO.getCurrency().getCode());
        txtCurrencyIndividualFood.setText(mGroupResponseDTO.getCurrency().getCode());


        btnSubmitMyBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidMyBudget()) {
                    memberCurrent.setActivityBudget(bigDecimalMyActivity);
                    memberCurrent.setAccommodationBudget(bigDecimalMyAccommodation);
                    memberCurrent.setTransportationBudget(bigDecimalMyTransportation);
                    memberCurrent.setFoodBudget(bigDecimalMyFood);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure to update your budget?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (InternetHelper.isOnline(getContext()) == false) {
                                DialogShowErrorMessage.showValidationDialog(getContext(), "No Connection");
                            } else {
                                myBudgetPresenter.updateMyBudgetPresenter(groupId, memberCurrent);
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


        boolean check = isAdminGroup();
        if (check == true) {

            btnSubmitGroupBudget.setVisibility(View.VISIBLE);

            enableEditText(edtBudgetGroupActivity);
            enableEditText(edtBudgetGroupAccommodation);
            enableEditText(edtBudgetGroupTransportation);
            enableEditText(edtBudgetGroupFood);


            btnSubmitGroupBudget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValidGroupBudget()) {
                        mGroupResponseDTO.setActivityBudget(bigDecimalGroupBudgetActivity);
                        mGroupResponseDTO.setAccommodationBudget(bigDecimalGroupBudgetAccommodation);
                        mGroupResponseDTO.setTransportationBudget(bigDecimalGroupBudgetTransportation);
                        mGroupResponseDTO.setFoodBudget(bigDecimalGroupBudgetFood);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure to update group budget?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (InternetHelper.isOnline(getContext()) == false) {
                                    DialogShowErrorMessage.showValidationDialog(getContext(), "No Connection");
                                } else {
                                    mUpdateGroupBudgetPresenter.updateGroupBudgetPresenter(groupId, mGroupResponseDTO);
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
        }

        chkEditBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEditBudget.isChecked()) {
                    if (isAdminGroup()){
                        lnlGroupBudget.setVisibility(View.VISIBLE);
                    }
                    lnlMyBudget.setVisibility(View.VISIBLE);
                } else {
                    lnlGroupBudget.setVisibility(View.GONE);
                    lnlMyBudget.setVisibility(View.GONE);
                }
            }
        });

        imgRefreshBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGroup();
            }
        });

        initGroupBudget();
        initPerPax();
        initIndividual();
    }

    public void reloadFragment() {
        transactionValueEventListener = listenerTransaction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    loadGroup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public boolean isValidMyBudget() {
        String valueActivity = edtBudgetIndividualActivity.getText().toString().replaceAll(",", "");
        String valueAccommodation = edtBudgetIndividualAccommodation.getText().toString().replaceAll(",", "");
        String valueTransportation = edtBudgetIndividualTransportation.getText().toString().replaceAll(",", "");
        String valueFood = edtBudgetIndividualFood.getText().toString().replaceAll(",", "");


        boolean result = true;

        if (valueActivity.matches("")) {
            edtBudgetIndividualActivity.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueActivity.charAt(0);
            if (valueActivity.trim().length() <= 0 || valueActivity.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetIndividualActivity.setError("Invalid number");
                result = false;
            } else {
                bigDecimalMyActivity = BigDecimal.valueOf(Double.parseDouble(valueActivity));
            }
        }


        if (valueAccommodation.matches("")) {
            edtBudgetIndividualAccommodation.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueAccommodation.charAt(0);
            if (valueAccommodation.trim().length() <= 0 || valueAccommodation.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetIndividualAccommodation.setError("Invalid number");
                result = false;
            } else {
                bigDecimalMyAccommodation = BigDecimal.valueOf(Double.parseDouble(valueAccommodation));
            }
        }


        if (valueTransportation.matches("")) {
            edtBudgetIndividualTransportation.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueTransportation.charAt(0);
            if (valueTransportation.trim().length() <= 0 || valueTransportation.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetIndividualTransportation.setError("Invalid number");
                result = false;
            } else {
                bigDecimalMyTransportation = BigDecimal.valueOf(Double.parseDouble(valueTransportation));
            }
        }


        if (valueFood.matches("")) {
            edtBudgetIndividualFood.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueFood.charAt(0);
            if (valueFood.trim().length() <= 0 || valueFood.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetIndividualFood.setError("Invalid number");
                result = false;
            } else {
                bigDecimalMyFood = BigDecimal.valueOf(Double.parseDouble(valueFood));
            }
        }


        return result;

    }

    public boolean isValidGroupBudget() {
        String valueActivity = edtBudgetGroupActivity.getText().toString().replaceAll(",", "");
        String valueAccommodation = edtBudgetGroupAccommodation.getText().toString().replaceAll(",", "");
        String valueTransportation = edtBudgetGroupTransportation.getText().toString().replaceAll(",", "");
        String valueFood = edtBudgetGroupFood.getText().toString().replaceAll(",", "");


        boolean result = true;

        if (valueActivity.matches("")) {
            edtBudgetGroupActivity.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueActivity.charAt(0);
            if (valueActivity.trim().length() <= 0 || valueActivity.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupActivity.setError("Invalid number");
                result = false;
            } else {
                bigDecimalGroupBudgetActivity = BigDecimal.valueOf(Double.parseDouble(valueActivity));
            }
        }


        if (valueAccommodation.matches("")) {
            edtBudgetGroupAccommodation.setError("Invalid number");
            result = false;

        } else {
            char ch1 = valueAccommodation.charAt(0);
            if (valueAccommodation.trim().length() <= 0 || valueAccommodation.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupAccommodation.setError("Invalid number");
                result = false;
            } else {
                bigDecimalGroupBudgetAccommodation = BigDecimal.valueOf(Double.parseDouble(valueAccommodation));
            }
        }


        if (valueTransportation.matches("")) {
            edtBudgetGroupTransportation.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueTransportation.charAt(0);
            if (valueTransportation.trim().length() <= 0 || valueTransportation.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupTransportation.setError("Invalid number");
                result = false;
            } else {
                bigDecimalGroupBudgetTransportation = BigDecimal.valueOf(Double.parseDouble(valueTransportation));
            }
        }

        if (valueFood.matches("")) {
            edtBudgetGroupFood.setError("Invalid number");
            result = false;
        } else {
            char ch1 = valueFood.charAt(0);
            if (valueFood.trim().length() <= 0 || valueFood.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupFood.setError("Invalid number");
                result = false;
            } else {
                bigDecimalGroupBudgetFood = BigDecimal.valueOf(Double.parseDouble(valueFood));
            }
        }


        return result;

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (transactionValueEventListener != null) {
                listenerTransaction.removeEventListener(transactionValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPerPax() {

        progressBarMyBudgetActivityPerPax.setMax(1);
        progressBarMyBudgetAccommodationPerPax.setMax(1);
        progressBarMyBudgetTransportationPerPax.setMax(1);
        progressBarMyBudgetFoodPerPax.setMax(1);

        BigDecimal numberMember = new BigDecimal(memberSize);

        BigDecimal myActivity = mGroupResponseDTO.getActivityBudget().divide(numberMember, RoundingMode.HALF_UP);
        BigDecimal myAccommodation = mGroupResponseDTO.getAccommodationBudget().divide(numberMember, RoundingMode.HALF_UP);
        BigDecimal myTransportation = mGroupResponseDTO.getTransportationBudget().divide(numberMember, RoundingMode.HALF_UP);
        BigDecimal myFood = mGroupResponseDTO.getFoodBudget().divide(numberMember, RoundingMode.HALF_UP);
        BigDecimal percentageActivity = BigDecimal.ZERO;
        BigDecimal percentageAccommodation = BigDecimal.ZERO;
        BigDecimal percentageTransportation = BigDecimal.ZERO;
        BigDecimal percentageFood = BigDecimal.ZERO;

        txtMyActualPerPaxCost.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getActualCost()) + " " + mGroupResponseDTO.getCurrency().getCode());

        if (budgetOverViewMyImpactInGroupCost.getActivityBudget().compareTo(BigDecimal.ZERO) >= 0 && myActivity.compareTo(BigDecimal.ZERO) > 0) {
            percentageActivity = budgetOverViewMyImpactInGroupCost.getActivityBudget().divide(myActivity, BigDecimal.ROUND_DOWN);
            txtValueMyBudgetActivityPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getActivityBudget()) + "/" + ChangeValue.formatBigCurrency(myActivity) + " (" + percentageActivity.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyBudgetActivityPerPax.setProgress(percentageActivity.floatValue());
        } else {
            txtValueMyBudgetActivityPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getActivityBudget()) + "/" + ChangeValue.formatBigCurrency(myActivity));
            progressBarMyBudgetActivityPerPax.setProgress(1);
        }

        if (budgetOverViewMyImpactInGroupCost.getAccommodationBudget().compareTo(BigDecimal.ZERO) >= 0 && myAccommodation.compareTo(BigDecimal.ZERO) > 0) {
            percentageAccommodation = budgetOverViewMyImpactInGroupCost.getAccommodationBudget().divide(myAccommodation, BigDecimal.ROUND_DOWN);
            txtValueMyBudgetAccommodationPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getAccommodationBudget()) + "/" + ChangeValue.formatBigCurrency(myAccommodation) + " (" + percentageAccommodation.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyBudgetAccommodationPerPax.setProgress(percentageAccommodation.floatValue());
        } else {
            txtValueMyBudgetAccommodationPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getAccommodationBudget()) + "/" + ChangeValue.formatBigCurrency(myAccommodation));
            progressBarMyBudgetAccommodationPerPax.setProgress(1);
        }

        if (budgetOverViewMyImpactInGroupCost.getTransportationBudget().compareTo(BigDecimal.ZERO) >= 0 && myTransportation.compareTo(BigDecimal.ZERO) > 0) {
            percentageTransportation = budgetOverViewMyImpactInGroupCost.getTransportationBudget().divide(myTransportation, BigDecimal.ROUND_DOWN);
            txtValueMyBudgetTransportationPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getTransportationBudget()) + "/" + ChangeValue.formatBigCurrency(myTransportation) + " (" + percentageTransportation.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyBudgetTransportationPerPax.setProgress(percentageTransportation.floatValue());
        } else {
            txtValueMyBudgetTransportationPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getTransportationBudget()) + "/" + ChangeValue.formatBigCurrency(myTransportation));
            progressBarMyBudgetTransportationPerPax.setProgress(1);
        }

        if (budgetOverViewMyImpactInGroupCost.getFoodBudget().compareTo(BigDecimal.ZERO) >= 0 && myFood.compareTo(BigDecimal.ZERO) > 0) {
            percentageFood = budgetOverViewMyImpactInGroupCost.getFoodBudget().divide(myFood, BigDecimal.ROUND_DOWN);
            txtValueMyBudgetFoodPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getFoodBudget()) + "/" + ChangeValue.formatBigCurrency(myFood) + " (" + percentageFood.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyBudgetFoodPerPax.setProgress(percentageFood.floatValue());
        } else {
            txtValueMyBudgetFoodPerPaxProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyImpactInGroupCost.getFoodBudget()) + "/" + ChangeValue.formatBigCurrency(myFood));
            progressBarMyBudgetFoodPerPax.setProgress(1);
        }


        if (budgetOverViewMyImpactInGroupCost.getActivityBudget().compareTo(myActivity) > 0) {
            txtMyBudgetActivityPerPaxOverload.setText("OverBudget");
        } else {
            txtMyBudgetActivityPerPaxOverload.setText("");
        }

        if (budgetOverViewMyImpactInGroupCost.getAccommodationBudget().compareTo(myAccommodation) > 0) {
            txtMyBudgetAccommodationPerPaxOverload.setText("OverBudget");
        } else {
            txtMyBudgetAccommodationPerPaxOverload.setText("");
        }

        if (budgetOverViewMyImpactInGroupCost.getTransportationBudget().compareTo(myTransportation) > 0) {
            txtMyBudgetTransportationPerPaxOverload.setText("OverBudget");
        } else {
            txtMyBudgetTransportationPerPaxOverload.setText("");
        }

        if (budgetOverViewMyImpactInGroupCost.getFoodBudget().compareTo(myFood) > 0) {
            txtMyBudgetFoodPerPaxOverload.setText("OverBudget");
        } else {
            txtMyBudgetFoodPerPaxOverload.setText("");
        }
    }

    public void initIndividual() {

        progressBarMyIndividualActivity.setMax(1);
        progressBarMyIndividualAccommodation.setMax(1);
        progressBarMyIndividualTransportation.setMax(1);
        progressBarMyIndividualFood.setMax(1);

        txtMyIndividualActualCost.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getActualCost()) + " " + mGroupResponseDTO.getCurrency().getCode());
        BigDecimal myActivity = memberCurrent.getActivityBudget();
        BigDecimal myAccommodation = memberCurrent.getAccommodationBudget();
        BigDecimal myTransportation = memberCurrent.getTransportationBudget();
        BigDecimal myFood = memberCurrent.getFoodBudget();
        BigDecimal totalBudget = BigDecimal.ZERO;
        totalBudget = totalBudget.add(myActivity).add(myAccommodation).add(myTransportation).add(myFood);

        BigDecimal percentageActivity = BigDecimal.ZERO;
        BigDecimal percentageAccommodation = BigDecimal.ZERO;
        BigDecimal percentageTransportation = BigDecimal.ZERO;
        BigDecimal percentageFood = BigDecimal.ZERO;

        if (budgetOverViewMyIndividualCost.getActivityBudget().compareTo(BigDecimal.ZERO) >= 0 && myActivity.compareTo(BigDecimal.ZERO) > 0) {
            percentageActivity = budgetOverViewMyIndividualCost.getActivityBudget().divide(myActivity, BigDecimal.ROUND_DOWN);
            txtValueIndividualActivityProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getActivityBudget()) + "/" + ChangeValue.formatBigCurrency(myActivity) + " (" + percentageActivity.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyIndividualActivity.setProgress(percentageActivity.floatValue());
        } else {
            txtValueIndividualActivityProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getActivityBudget()) + "/" + ChangeValue.formatBigCurrency(myActivity));
            progressBarMyIndividualActivity.setProgress(1);
        }

        if (budgetOverViewMyIndividualCost.getAccommodationBudget().compareTo(BigDecimal.ZERO) >= 0 && myAccommodation.compareTo(BigDecimal.ZERO) > 0) {
            percentageAccommodation = budgetOverViewMyIndividualCost.getAccommodationBudget().divide(myAccommodation, BigDecimal.ROUND_DOWN);
            txtValueIndividualAccommodationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getAccommodationBudget()) + "/" + ChangeValue.formatBigCurrency(myAccommodation) + " (" + percentageAccommodation.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyIndividualAccommodation.setProgress(percentageAccommodation.floatValue());
        } else {
            txtValueIndividualAccommodationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getAccommodationBudget()) + "/" + ChangeValue.formatBigCurrency(myAccommodation));
            progressBarMyIndividualAccommodation.setProgress(1);
        }

        if (budgetOverViewMyIndividualCost.getTransportationBudget().compareTo(BigDecimal.ZERO) >= 0 && myTransportation.compareTo(BigDecimal.ZERO) > 0) {
            percentageTransportation = budgetOverViewMyIndividualCost.getTransportationBudget().divide(myTransportation, BigDecimal.ROUND_DOWN);
            txtValueIndividualTransportationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getTransportationBudget()) + "/" + ChangeValue.formatBigCurrency(myTransportation) + " (" + percentageTransportation.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyIndividualTransportation.setProgress(percentageTransportation.floatValue());
        } else {
            txtValueIndividualTransportationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getTransportationBudget()) + "/" + ChangeValue.formatBigCurrency(myTransportation));
            progressBarMyIndividualTransportation.setProgress(1);
        }

        if (budgetOverViewMyIndividualCost.getFoodBudget().compareTo(BigDecimal.ZERO) >= 0 && myFood.compareTo(BigDecimal.ZERO) > 0) {
            percentageFood = budgetOverViewMyIndividualCost.getFoodBudget().divide(myFood, BigDecimal.ROUND_DOWN);
            txtValueIndividualFoodProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getFoodBudget()) + "/" + ChangeValue.formatBigCurrency(myFood) + " (" + percentageFood.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarMyIndividualFood.setProgress(percentageFood.floatValue());
        } else {
            txtValueIndividualFoodProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getFoodBudget()) + "/" + ChangeValue.formatBigCurrency(myFood));
            progressBarMyIndividualFood.setProgress(1);
        }


        if (budgetOverViewMyIndividualCost.getActivityBudget().compareTo(myActivity) > 0) {
            txtBudgetMyIndividualActivityOverload.setText("OverBudget");
        } else {
            txtBudgetMyIndividualActivityOverload.setText("");
        }

        if (budgetOverViewMyIndividualCost.getAccommodationBudget().compareTo(myAccommodation) > 0) {
            txtBudgetMyIndividualAccommodationOverload.setText("OverBudget");
        } else {
            txtBudgetMyIndividualAccommodationOverload.setText("");
        }

        if (budgetOverViewMyIndividualCost.getTransportationBudget().compareTo(myTransportation) > 0) {
            txtBudgetMyIndividualTransportationOverload.setText("OverBudget");
        } else {
            txtBudgetMyIndividualTransportationOverload.setText("");
        }

        if (budgetOverViewMyIndividualCost.getFoodBudget().compareTo(myFood) > 0) {
            txtBudgetMyIndividualFoodOverload.setText("OverBudget");
        } else {
            txtBudgetMyIndividualFoodOverload.setText("");
        }

        txtMyIndividualActualCost.setText(ChangeValue.formatBigCurrency(budgetOverViewMyIndividualCost.getActualCost()) + " " + mGroupResponseDTO.getCurrency().getCode());
    }


    public void initGroupBudget() {
        progressBarGroupActivity.setMax(1);
        progressBarGroupAccommodation.setMax(1);
        progressBarGroupTransportation.setMax(1);
        progressBarGroupFood.setMax(1);

        txtGroupActualCost.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getActualCost()) + " " + mGroupResponseDTO.getCurrency().getCode());
        BigDecimal activityGroup = mGroupResponseDTO.getActivityBudget();
        BigDecimal accommodationGroup = mGroupResponseDTO.getAccommodationBudget();
        BigDecimal transportationGroup = mGroupResponseDTO.getTransportationBudget();
        BigDecimal foodGroup = mGroupResponseDTO.getFoodBudget();
        BigDecimal totalBudget = BigDecimal.ZERO;
        totalBudget = totalBudget.add(activityGroup).add(accommodationGroup).add(transportationGroup).add(foodGroup);

        BigDecimal percentageActivity = BigDecimal.ZERO;
        BigDecimal percentageAccommodation = BigDecimal.ZERO;
        BigDecimal percentageTransportation = BigDecimal.ZERO;
        BigDecimal percentageFood = BigDecimal.ZERO;

        if (budgetOverViewGroupCost.getActivityBudget().compareTo(BigDecimal.ZERO) >= 0 && activityGroup.compareTo(BigDecimal.ZERO) > 0) {
            percentageActivity = budgetOverViewGroupCost.getActivityBudget().divide(activityGroup, BigDecimal.ROUND_DOWN);
            txtValueGroupActivityProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getActivityBudget()) + "/" + ChangeValue.formatBigCurrency(activityGroup) + " (" + percentageActivity.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarGroupActivity.setProgress(percentageActivity.floatValue());
        } else {
            txtValueGroupActivityProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getActivityBudget()) + "/" + ChangeValue.formatBigCurrency(activityGroup));
            progressBarGroupActivity.setProgress(1);
        }

        if (budgetOverViewGroupCost.getAccommodationBudget().compareTo(BigDecimal.ZERO) >= 0 && accommodationGroup.compareTo(BigDecimal.ZERO) > 0) {
            percentageAccommodation = budgetOverViewGroupCost.getAccommodationBudget().divide(accommodationGroup, BigDecimal.ROUND_DOWN);
            txtValueGroupAccommodationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getAccommodationBudget()) + "/" + ChangeValue.formatBigCurrency(accommodationGroup) + " (" + percentageAccommodation.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarGroupAccommodation.setProgress(percentageAccommodation.floatValue());
        } else {
            txtValueGroupAccommodationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getAccommodationBudget()) + "/" + ChangeValue.formatBigCurrency(accommodationGroup));
            progressBarGroupAccommodation.setProgress(1);

        }

        if (budgetOverViewGroupCost.getTransportationBudget().compareTo(BigDecimal.ZERO) >= 0 && transportationGroup.compareTo(BigDecimal.ZERO) > 0) {
            percentageTransportation = budgetOverViewGroupCost.getTransportationBudget().divide(transportationGroup, BigDecimal.ROUND_DOWN);
            txtValueGroupTransportationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getTransportationBudget()) + "/" + ChangeValue.formatBigCurrency(transportationGroup) + " (" + percentageTransportation.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarGroupTransportation.setProgress(percentageTransportation.floatValue());
        } else {
            txtValueGroupTransportationProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getTransportationBudget()) + "/" + ChangeValue.formatBigCurrency(transportationGroup));
            progressBarGroupTransportation.setProgress(1);
        }

        if (budgetOverViewGroupCost.getFoodBudget().compareTo(BigDecimal.ZERO) >= 0 && foodGroup.compareTo(BigDecimal.ZERO) > 0) {
            percentageFood = budgetOverViewGroupCost.getFoodBudget().divide(foodGroup, BigDecimal.ROUND_DOWN);
            txtValueGroupFoodProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getFoodBudget()) + "/" + ChangeValue.formatBigCurrency(foodGroup) + " (" + percentageFood.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%)");
            progressBarGroupFood.setProgress(percentageFood.floatValue());
        } else {
            txtValueGroupFoodProcess.setText(ChangeValue.formatBigCurrency(budgetOverViewGroupCost.getFoodBudget()) + "/" + ChangeValue.formatBigCurrency(foodGroup));
            progressBarGroupFood.setProgress(1);
        }


        if (budgetOverViewGroupCost.getActivityBudget().compareTo(activityGroup) > 0) {
            txtBudgetGroupActivityOverload.setText("OverBudget");
        } else {
            txtBudgetGroupActivityOverload.setText("");
        }

        if (budgetOverViewGroupCost.getAccommodationBudget().compareTo(accommodationGroup) > 0) {
            txtBudgetGroupAccommodationOverload.setText("OverBudget");
        } else {
            txtBudgetGroupAccommodationOverload.setText("");
        }

        if (budgetOverViewGroupCost.getTransportationBudget().compareTo(transportationGroup) > 0) {
            txtBudgetGroupTransportationOverload.setText("OverBudget");
        } else {
            txtBudgetGroupTransportationOverload.setText("");
        }

        if (budgetOverViewGroupCost.getFoodBudget().compareTo(foodGroup) > 0) {
            txtBudgetGroupFoodOverload.setText("OverBudget");
        } else {
            txtBudgetGroupFoodOverload.setText("");
        }
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }


    public void loadGroup() {
        if (InternetHelper.isOnline(getContext()) == false) {
            DialogShowErrorMessage.showValidationDialog(getContext(), "No Connection");
        } else {
            mPrintGroupByIdPresenter.getGroupById(groupId);
        }
    }

    public void loadMyBudget() {
        if (InternetHelper.isOnline(getContext()) == false) {
            DialogShowErrorMessage.showValidationDialog(getContext(), "No Connection");
        } else {
            getMyBudgetPresenter.GetMyBudgetPresenter(groupId);
        }
    }

    public void loadTransaction() {
        if (InternetHelper.isOnline(getContext()) == false) {
            DialogShowErrorMessage.showValidationDialog(getContext(), "No Connection");
        } else {
            mPrintAllTransactionPresenter.printAllTransaction(groupId);
        }
    }

    public boolean isAdminGroup() {
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            for (int i = 0; i < mGroupResponseDTO.getMemberList().size(); i++) {
                int roleMember = mGroupResponseDTO.getMemberList().get(i).getIdRole();
                String member = mGroupResponseDTO.getMemberList().get(i).getPerson().getFirebaseUid();
                if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.d("CityOverviewActivity", "Role: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void printAllTransactionSuccess(List<TransactionDTO> transactionDTOList) {
        if (transactionDTOList != null) {
            groupTransactionList = TransactionHandler.getGroupTransaction(transactionDTOList);

            myTransactionList = TransactionHandler.getIndividualTransactionList(transactionDTOList, userId);

            budgetOverViewGroupCost = new BudgetOverViewDTO();
            budgetOverViewMyImpactInGroupCost = new BudgetOverViewDTO();
            budgetOverViewMyIndividualCost = new BudgetOverViewDTO();

            budgetOverViewGroupCost = TransactionHandler.getGroupDetailsOfTransaction(groupTransactionList);

            budgetOverViewMyImpactInGroupCost = TransactionHandler.getMyImpactOnGroup(groupTransactionList, memberSize);

            budgetOverViewMyIndividualCost = TransactionHandler.getMyIndividualActual(myTransactionList, userId);
        }
        loadMyBudget();
    }

    @Override
    public void printAllTransactionFail(String messageFail) {

    }

    @Override
    public void updateGroupBudgetSuccess(String message) {
        Toast.makeText(getContext(), "Update Budget Group Successfully", Toast.LENGTH_SHORT).show();
//        lnlGroupBudget.setVisibility(View.GONE);
        loadGroup();
    }

    @Override
    public void updateGroupBudgetFail(String message) {
        Toast.makeText(getContext(), "Update Budget Group Fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateMyBudgetSuccessfully(String messgae) {
        Toast.makeText(getContext(), "Update Your Budget Successfully", Toast.LENGTH_SHORT).show();
//        lnlMyBudget.setVisibility(View.GONE);
        loadGroup();
    }

    @Override
    public void updateMyBudgetFail(String messgae) {
        Toast.makeText(getContext(), "Update Your Budget Fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void printGroupByIdSS(GroupResponseDTO groupResponseDTO) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (groupResponseDTO != null) {
            mGroupResponseDTO = groupResponseDTO;
            List<GroupResponseDTO.MemberDTO> memberDTOList = new ArrayList<>();
            for (int i = 0; i < mGroupResponseDTO.getMemberList().size(); i++) {
                if (mGroupResponseDTO.getMemberList().get(i).getIdStatus().equals(MemberStatus.ACTIVE)) {
                    memberDTOList.add(mGroupResponseDTO.getMemberList().get(i));
                }
            }
            memberSize = memberDTOList.size();
        }
        loadTransaction();
    }

    @Override
    public void printGroupByIdFail(String messageFail) {

    }

    @Override
    public void getMyBudgetSuccess(MemberDTO memberDTO) {
        if (memberDTO != null) {
            memberCurrent = memberDTO;
        }
        initData();
    }

    @Override
    public void getMyBudgetFail(String messageFail) {

    }
}