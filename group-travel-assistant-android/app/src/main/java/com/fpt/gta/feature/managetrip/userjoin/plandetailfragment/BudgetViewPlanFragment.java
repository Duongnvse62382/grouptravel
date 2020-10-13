package com.fpt.gta.feature.managetrip.userjoin.plandetailfragment;

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
import android.widget.EditText;
import android.widget.TextView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.util.BudgetTextWatcher;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.math.BigDecimal;


public class BudgetViewPlanFragment extends Fragment {

    private TextInputEditText edtBudgetGroupActivity, edtBudgetGroupAccommodation, edtBudgetGroupTransportation, edtBudgetGroupFood;
    private TextView txtGroupCurrency1, txtGroupCurrency2, txtGroupCurrency3, txtGroupCurrency4, txtBudgetTotal;
    private View mView;
    private final int MAX_LENGTH = 12;
    private PlanDTO planDTO;
    private CurrencyDTO currencyDTO;
    private Integer memberSize;

    public BudgetViewPlanFragment() {

    }


    public static BudgetViewPlanFragment newInstance(String param1, String param2) {
        BudgetViewPlanFragment fragment = new BudgetViewPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_budget_view_plan, container, false);
        return  mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView();
        initData();
    }

    public void initialView() {

        edtBudgetGroupActivity = mView.findViewById(R.id.edtBudgetGroupActivity);
        edtBudgetGroupAccommodation = mView.findViewById(R.id.edtBudgetGroupAccommodation);
        edtBudgetGroupTransportation = mView.findViewById(R.id.edtBudgetGroupTransportation);
        edtBudgetGroupFood = mView.findViewById(R.id.edtBudgetGroupFood);

        txtGroupCurrency1 = mView.findViewById(R.id.txtCurrencyPerPax);
        txtGroupCurrency2 = mView.findViewById(R.id.txtCurrencyPerPax2);
        txtGroupCurrency3 = mView.findViewById(R.id.txtCurrencyPerPax3);
        txtGroupCurrency4 = mView.findViewById(R.id.txtCurrencyPerPax4);
        txtBudgetTotal = mView.findViewById(R.id.txtBudgetTotal);

        edtBudgetGroupActivity.setEnabled(false);
        edtBudgetGroupAccommodation.setEnabled(false);
        edtBudgetGroupTransportation.setEnabled(false);
        edtBudgetGroupFood.setEnabled(false);

    }





    public void initData() {
        memberSize = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.MEMBERSIZE);
        String strPlanDTO = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.PLANGSON);
        Gson gson = new Gson();
        planDTO = gson.fromJson(strPlanDTO, PlanDTO.class);
        String strCurrencyDTO = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.CURRENCYGSON);
        Gson currencyGson = new Gson();
        currencyDTO = currencyGson.fromJson(strCurrencyDTO, CurrencyDTO.class);
        if(planDTO != null){
            txtGroupCurrency1.setText(currencyDTO.getCode());
            txtGroupCurrency2.setText(currencyDTO.getCode());
            txtGroupCurrency3.setText(currencyDTO.getCode());
            txtGroupCurrency4.setText(currencyDTO.getCode());

            edtBudgetGroupActivity.setText(ChangeValue.formatBigCurrency(planDTO.getActivityBudget()));
            edtBudgetGroupAccommodation.setText(ChangeValue.formatBigCurrency(planDTO.getAccommodationBudget()));
            edtBudgetGroupTransportation.setText(ChangeValue.formatBigCurrency(planDTO.getTransportationBudget()));
            edtBudgetGroupFood.setText(ChangeValue.formatBigCurrency(planDTO.getFoodBudget()));
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


            BigDecimal total = BigDecimal.ZERO;
            total = total.add(planDTO.getActivityBudget()).add(planDTO.getAccommodationBudget()).add(planDTO.getTransportationBudget()).add(planDTO.getFoodBudget());
            txtBudgetTotal.setText("Budget Total: " + ChangeValue.formatBigCurrency(total));
        }

    }

}