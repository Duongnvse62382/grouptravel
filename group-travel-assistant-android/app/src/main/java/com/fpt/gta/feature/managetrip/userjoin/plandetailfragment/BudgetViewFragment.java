package com.fpt.gta.feature.managetrip.userjoin.plandetailfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.BudgetOverViewDTO;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.presenter.PrintAllBudgetElectedPlanPresenter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.BudgetElectedPlanView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class BudgetViewFragment extends Fragment implements BudgetElectedPlanView {
    private View mView;
    private BudgetCityOverViewUserAdapter mBudgetCityOverViewAdapter;
    private PrintAllBudgetElectedPlanPresenter mPrintAllBudgetElectedPlanPresenter;
    private RecyclerView rcvBudgetOverView;
    private List<TripReponseDTO> listTrip;
    private List<BudgetOverViewDTO> mBudgetOverViewDTOList;
    private int groupId;

    public BudgetViewFragment() {

    }


    public static BudgetViewFragment newInstance(String param1, String param2) {
        BudgetViewFragment fragment = new BudgetViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.fragment_budget_view, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadTrip();
        initData();
    }

    private void initData() {
    }

    private void initView() {
        rcvBudgetOverView = mView.findViewById(R.id.rcvBudgetOverViewUser);
        groupId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvBudgetOverView.setLayoutManager(linearLayoutManager);
    }

    private void updateUI() {
        if (mBudgetCityOverViewAdapter == null) {
            mBudgetCityOverViewAdapter = new BudgetCityOverViewUserAdapter(getContext(), mBudgetOverViewDTOList);
            rcvBudgetOverView.setAdapter(mBudgetCityOverViewAdapter);
        } else {
            mBudgetCityOverViewAdapter.notifyChangeData(mBudgetOverViewDTOList);
        }
    }


    public void loadTrip() {
        mPrintAllBudgetElectedPlanPresenter = new PrintAllBudgetElectedPlanPresenter(getContext(), this);
        mPrintAllBudgetElectedPlanPresenter.getAllTripByGroupId(groupId);
    }


    @Override
    public void onSucessFul(List<TripReponseDTO> tripReponseDTOList) {
        if (tripReponseDTOList != null) {
            Toast.makeText(getContext(), "Succesfully", Toast.LENGTH_SHORT).show();
            listTrip = tripReponseDTOList;
            mBudgetOverViewDTOList = new ArrayList<>();
            for (TripReponseDTO tripReponseDTO : listTrip) {
                if (tripReponseDTO.getElectedPlan() != null) {
                    BudgetOverViewDTO budgetOverViewDTO = new BudgetOverViewDTO();
                    BigDecimal total = BigDecimal.ZERO;
//                    budgetOverViewDTO.setBudgetCityName(tripReponseDTO.getStartPlace().getName());
                    budgetOverViewDTO.setActivityBudget(tripReponseDTO.getElectedPlan().getActivityBudget());
                    budgetOverViewDTO.setAccommodationBudget(tripReponseDTO.getElectedPlan().getAccommodationBudget());
                    budgetOverViewDTO.setTransportationBudget(tripReponseDTO.getElectedPlan().getTransportationBudget());
                    total = total.add(tripReponseDTO.getElectedPlan().getActivityBudget()).add(tripReponseDTO.getElectedPlan().getAccommodationBudget()).add(tripReponseDTO.getElectedPlan().getTransportationBudget());
                    budgetOverViewDTO.setActualCost(total);
                    mBudgetOverViewDTOList.add(budgetOverViewDTO);
                }
            }
            updateUI();
        }
    }

    @Override
    public void onFail(String messageFail) {

    }

    @Override
    public void showError(String message) {

    }
}

