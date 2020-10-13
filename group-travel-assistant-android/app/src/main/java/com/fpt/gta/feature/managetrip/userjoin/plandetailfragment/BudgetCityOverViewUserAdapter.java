package com.fpt.gta.feature.managetrip.userjoin.plandetailfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.BudgetOverViewDTO;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class BudgetCityOverViewUserAdapter extends RecyclerView.Adapter<BudgetCityOverViewUserAdapter.BudgetCityViewHolder> {
    private Context mContext;
    private List<BudgetOverViewDTO> budgetOverViewDTOList;
    private CurrencyDTO currencyDTO;
    private String currencyCode;


    public BudgetCityOverViewUserAdapter(Context mContext, List<BudgetOverViewDTO> budgetOverViewDTOList) {
        this.mContext = mContext;
        this.budgetOverViewDTOList = budgetOverViewDTOList;
    }

    @NonNull
    @Override
    public BudgetCityOverViewUserAdapter.BudgetCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_budget_city_overview, parent, false);
        return new BudgetCityOverViewUserAdapter.BudgetCityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetCityOverViewUserAdapter.BudgetCityViewHolder holder, int position) {
        holder.txtBudgetActivityOverview.setText(ChangeValue.formatBigCurrency(budgetOverViewDTOList.get(position).getActivityBudget()) + " " + currencyDTO.getCode());
        holder.txtBudgetAccommodationOverview.setText(ChangeValue.formatBigCurrency(budgetOverViewDTOList.get(position).getAccommodationBudget()) + " " + currencyDTO.getCode());
        holder.txtBudgetTransportationOverview.setText(ChangeValue.formatBigCurrency(budgetOverViewDTOList.get(position).getTransportationBudget()) + " " + currencyDTO.getCode());
        holder.txtBudgetFoodOverview.setText(ChangeValue.formatBigCurrency(budgetOverViewDTOList.get(position).getFoodBudget()) + " " + currencyDTO.getCode());
//        holder.txtBudgetCityName.setText(budgetOverViewDTOList.get(position).getBudgetCityName());
        holder.txtTotalBudgetCityView.setText(ChangeValue.formatBigCurrency(budgetOverViewDTOList.get(position).getActualCost()) + " " + currencyDTO.getCode());
        holder.txtTotalBudgetCityView.setSelected(true);


    }



    @Override
    public int getItemCount() {
        return budgetOverViewDTOList.size();
    }

    public void notifyChangeData(List<BudgetOverViewDTO> mDtos) {
        budgetOverViewDTOList = new ArrayList<>();
        budgetOverViewDTOList = mDtos;
        notifyDataSetChanged();
    }

    public class BudgetCityViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout lnlRowBudgetOverView;
        public TextView txtBudgetActivityOverview, txtBudgetAccommodationOverview, txtBudgetTransportationOverview,txtBudgetFoodOverview, txtBudgetCityName, txtTotalBudgetCityView;
        public View rootView;

        public BudgetCityViewHolder(@NonNull View itemView) {

            super(itemView);
            rootView = itemView;
            currencyCode = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.CURRENCYGSON);
            Gson gson = new Gson();
            currencyDTO = gson.fromJson(currencyCode, CurrencyDTO.class);
            lnlRowBudgetOverView = itemView.findViewById(R.id.lnlRowBudgetOverView);
            txtBudgetAccommodationOverview = itemView.findViewById(R.id.txtBudgetAccommodationOverview);
            txtBudgetFoodOverview = itemView.findViewById(R.id.txBudgetFoodOverview);
            txtBudgetActivityOverview = itemView.findViewById(R.id.txtBudgetActivityOverview);
            txtBudgetTransportationOverview = itemView.findViewById(R.id.txtBudgetTransportationOverview);
            txtBudgetCityName = itemView.findViewById(R.id.txtBudgetCityName);
            txtTotalBudgetCityView = itemView.findViewById(R.id.txtTotalBudgetCityView);

        }


    }
}