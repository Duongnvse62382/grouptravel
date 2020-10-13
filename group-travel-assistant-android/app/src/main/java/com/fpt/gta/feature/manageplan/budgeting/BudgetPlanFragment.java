package com.fpt.gta.feature.manageplan.budgeting;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.presenter.SetBudgetPlanPresenter;
import com.fpt.gta.util.BudgetTextWatcher;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.SetBudgetPlanView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.math.BigDecimal;

public class BudgetPlanFragment extends Fragment implements SetBudgetPlanView {
    private Button btnSubmitGroupBudget;
    private TextInputEditText edtBudgetGroupActivity, edtBudgetGroupAccommodation, edtBudgetGroupTransportation, edtBudgetGroupFood;
    private TextView txtGroupCurrency1, txtGroupCurrency2, txtGroupCurrency3, txtGroupCurrency4;
    private View mView;
    private final int MAX_LENGTH = 12;
    private SetBudgetPlanPresenter mSetBudgetPlanPresenter;
    private Integer planID;
    private BigDecimal budgetActivity = BigDecimal.ZERO;
    private BigDecimal budgetAccommodation = BigDecimal.ZERO;
    private BigDecimal budgetTransportation = BigDecimal.ZERO;
    private BigDecimal budgetFood = BigDecimal.ZERO;
    private PlanDTO planDTO;
    private CurrencyDTO currencyDTO;
    private int memberSize;
    private int groupId;
    private int planStaus;
    private String isAdminPlan;
    private String idAdminJourney;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Integer groupStatus;
    private Integer idTrip;
    private String strBudgetGroupActivity, strBudgetGroupAccommodation, strBudgetGroupTransportation, strBudgetGroupFood;


    public BudgetPlanFragment() {
    }


    public static BudgetPlanFragment newInstance() {
        BudgetPlanFragment fragment = new BudgetPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_budget_plan, container, false);
        return mView;
    }

    public void initialView() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        groupId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        idTrip = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDTRIP);
        idAdminJourney = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.ISIDADMIN);
        String strPlanDTO = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.PLANOJECT);
        groupStatus = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.GROUPSTATUS);
        Gson gson = new Gson();
        planDTO = gson.fromJson(strPlanDTO, PlanDTO.class);
        isAdminPlan = planDTO.getOwner().getPerson().getFirebaseUid();
        planStaus = planDTO.getIdStatus();
        String strCurrencyDTO = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.GROUP_CURRENCY_SHARE);
        Gson currencyGson = new Gson();
        currencyDTO = currencyGson.fromJson(strCurrencyDTO, CurrencyDTO.class);
        planID = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.PLANID);


        btnSubmitGroupBudget = mView.findViewById(R.id.btnSubmitGroupBudget);
        btnSubmitGroupBudget.setVisibility(View.GONE);

        edtBudgetGroupActivity = mView.findViewById(R.id.edtBudgetGroupActivity);
        edtBudgetGroupAccommodation = mView.findViewById(R.id.edtBudgetGroupAccommodation);
        edtBudgetGroupTransportation = mView.findViewById(R.id.edtBudgetGroupTransportation);
        edtBudgetGroupFood = mView.findViewById(R.id.edtBudgetGroupFood);

        txtGroupCurrency1 = mView.findViewById(R.id.txtCurrencyPerPax);
        txtGroupCurrency2 = mView.findViewById(R.id.txtCurrencyPerPax2);
        txtGroupCurrency3 = mView.findViewById(R.id.txtCurrencyPerPax3);
        txtGroupCurrency4 = mView.findViewById(R.id.txtCurrencyPerPax4);

        disableEditText(edtBudgetGroupActivity);
        disableEditText(edtBudgetGroupAccommodation);
        disableEditText(edtBudgetGroupTransportation);
        disableEditText(edtBudgetGroupFood);

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
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView();
        initData();
    }


    public boolean checkStatusPlan(String userId, String isAdminPlan, String isAdminJourney, int planStatus) {

        if (planStatus != 4) {
            if (userId.equals(isAdminPlan)) {
                return true;
            }
        }
        return false;
    }

    public void initData() {
        String userId = user.getUid();
        boolean checkAdmin = checkStatusPlan(userId, isAdminPlan, idAdminJourney, planStaus);
        if (!groupStatus.equals(GroupStatus.PLANNING)) {
            disableEditText(edtBudgetGroupActivity);
            disableEditText(edtBudgetGroupAccommodation);
            disableEditText(edtBudgetGroupTransportation);
            disableEditText(edtBudgetGroupFood);
            btnSubmitGroupBudget.setVisibility(View.GONE);
        } else {
            if (checkAdmin == true) {
                enableEditText(edtBudgetGroupActivity);
                enableEditText(edtBudgetGroupAccommodation);
                enableEditText(edtBudgetGroupTransportation);
                enableEditText(edtBudgetGroupFood);
                btnSubmitGroupBudget.setVisibility(View.VISIBLE);
            } else {
                disableEditText(edtBudgetGroupActivity);
                disableEditText(edtBudgetGroupAccommodation);
                disableEditText(edtBudgetGroupTransportation);
                disableEditText(edtBudgetGroupFood);
                btnSubmitGroupBudget.setVisibility(View.GONE);
            }
        }

        txtGroupCurrency1.setText(currencyDTO.getCode());
        txtGroupCurrency2.setText(currencyDTO.getCode());
        txtGroupCurrency3.setText(currencyDTO.getCode());
        txtGroupCurrency4.setText(currencyDTO.getCode());

        edtBudgetGroupActivity.setText(ChangeValue.formatBigCurrency(planDTO.getActivityBudget()));
        edtBudgetGroupAccommodation.setText(ChangeValue.formatBigCurrency(planDTO.getAccommodationBudget()));
        edtBudgetGroupTransportation.setText(ChangeValue.formatBigCurrency(planDTO.getTransportationBudget()));
        edtBudgetGroupFood.setText(ChangeValue.formatBigCurrency(planDTO.getFoodBudget()));


        btnSubmitGroupBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidBudgetPlan()) {
                    setBudgetPlan(planID, budgetActivity, budgetAccommodation, budgetTransportation, budgetFood);
                }
            }
        });

    }

    public boolean isValidBudgetPlan() {
        strBudgetGroupActivity = edtBudgetGroupActivity.getText().toString().replaceAll(",", "");
        strBudgetGroupAccommodation = edtBudgetGroupAccommodation.getText().toString().replaceAll(",", "");
        strBudgetGroupTransportation = edtBudgetGroupTransportation.getText().toString().replaceAll(",", "");
        strBudgetGroupFood = edtBudgetGroupFood.getText().toString().replaceAll(",", "");

        boolean result = true;
        if (strBudgetGroupActivity.matches("")){
            edtBudgetGroupActivity.setError("Invalid number");
            result = false;
        }else {
            char ch1 = strBudgetGroupActivity.charAt(0);
            if (strBudgetGroupActivity.trim().length() <= 0 || strBudgetGroupActivity.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupActivity.setError("Invalid number");
                result = false;
            } else {
                budgetActivity = BigDecimal.valueOf(Double.parseDouble(strBudgetGroupActivity));
            }
        }


        if (strBudgetGroupAccommodation.matches("")){
            edtBudgetGroupAccommodation.setError("Invalid number");
            result = false;
        }else {
            char ch1 = strBudgetGroupAccommodation.charAt(0);
            if (strBudgetGroupAccommodation.trim().length() <= 0 || strBudgetGroupAccommodation.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupAccommodation.setError("Invalid number");
                result = false;
            } else {
                budgetAccommodation = BigDecimal.valueOf(Double.parseDouble(strBudgetGroupAccommodation));
            }
        }


        if (strBudgetGroupTransportation.matches("")){
            edtBudgetGroupTransportation.setError("Invalid number");
            result = false;

        }else {
            char ch1 = strBudgetGroupTransportation.charAt(0);
            if (strBudgetGroupTransportation.trim().length() <= 0 || strBudgetGroupTransportation.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupTransportation.setError("Invalid number");
                result = false;
            } else {
                budgetTransportation = BigDecimal.valueOf(Double.parseDouble(strBudgetGroupTransportation));
            }
        }

        if (strBudgetGroupFood.matches("")){
            edtBudgetGroupFood.setError("Invalid number");
            result = false;
        }else {
            char ch1 =strBudgetGroupFood.charAt(0);
            if (strBudgetGroupFood.trim().length() <= 0 || strBudgetGroupFood.toString().trim().equals(".") || ch1 == '.') {
                edtBudgetGroupFood.setError("Invalid number");
                result = false;
            } else {
                budgetFood = BigDecimal.valueOf(Double.parseDouble(strBudgetGroupFood));
            }
        }


        return result;

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

    public void setBudgetPlan(Integer idPlan, BigDecimal activityBudget, BigDecimal accommodationBudget, BigDecimal transportationBudget, BigDecimal foodBudget) {
        mSetBudgetPlanPresenter = new SetBudgetPlanPresenter(getContext(), this);
        mSetBudgetPlanPresenter.setBudgetPlan(idPlan, activityBudget, accommodationBudget, transportationBudget, foodBudget);
    }

    @Override
    public void setBudgetPlanSuccess(String messageSuccess) {
        Toast.makeText(getContext(), "Set Budget Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setBudgetPlanFail(String message) {
        Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
    }

}