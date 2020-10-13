package com.fpt.gta.feature.managetransaction.transactiontypeoverview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fpt.gta.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetSortTransaction extends BottomSheetDialogFragment {
    private LinearLayout mLnlDate;
    private LinearLayout mLnlAmount;
    private LinearLayout mLnlStatistics;
    private LinearLayout mLnlIndividualMode;
    private LinearLayout mLnlGroupMode;
    private LinearLayout mLnlBudgetingMode;
    private boolean isSelected;

    private BottomSheetOptionsListenerCallback mListener;

    public void setBottomSheetListenerCallback(BottomSheetOptionsListenerCallback mListener) {
        this.mListener = mListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_sort_transaction, container, false);
        mLnlDate = view.findViewById(R.id.lnl_sort_by_Date);
        mLnlStatistics = view.findViewById(R.id.lnl_statistics);
        mLnlIndividualMode = view.findViewById(R.id.lnl_personal_mode);
        mLnlGroupMode = view.findViewById(R.id.lnl_group_mode);

        mLnlDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int datePosition = 0;
                getDialog().dismiss();
                mListener.onRowClick(datePosition);
            }
        });

        mLnlStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int statisticsPosition = 1;
                getDialog().dismiss();
                mListener.onRowClick(statisticsPosition);

            }
        });
        mLnlIndividualMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int personalModePosition = 2;
                getDialog().dismiss();
                mListener.onRowClick(personalModePosition);
            }
        });

        mLnlGroupMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int groupModePosition = 3;
                getDialog().dismiss();
                mListener.onRowClick(groupModePosition);
            }
        });

        return view;
    }

    public interface BottomSheetOptionsListenerCallback {
        void onRowClick(int selectedPosition);
    }


}
