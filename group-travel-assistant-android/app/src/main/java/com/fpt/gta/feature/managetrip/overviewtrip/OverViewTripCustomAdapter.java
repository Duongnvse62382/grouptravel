package com.fpt.gta.feature.managetrip.overviewtrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.fpt.gta.R;
import com.fpt.gta.feature.managebalance.BalanceFragment;
import com.fpt.gta.feature.managebudget.BudgetFragment;
import com.fpt.gta.feature.managetransaction.transactiontypeoverview.TransactionOverViewFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class OverViewTripCustomAdapter implements SmartTabLayout.TabProvider {

    private Context mContext;

    public OverViewTripCustomAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public enum TripActivityPages {
        TAB_1(0, "City", TripManageFragment.newInstance()),
        TAB_2(1, "Expense", TransactionOverViewFragment.newInstance()),
        TAB_3(2, "Balance", BalanceFragment.newInstance()),
        TAB_4(3,"Budget",BudgetFragment.newInstance());


        public int index;
        public String title;
        public Fragment fragment;

        TripActivityPages(int index, String title, Fragment fragment) {
            this.index = index;
            this.title = title;
            this.fragment = fragment;
        }

    }

    @Override
    public View createTabView(ViewGroup viewGroup, int position, PagerAdapter pagerAdapter) {
        View v = LayoutInflater.from(this.mContext).inflate(R.layout.activity_trip_tab, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (v != null) {
            TextView mNameTab = v.findViewById(R.id.name_tab);
            mNameTab.setText(OverViewTripCustomAdapter.TripActivityPages.values()[position].title);
        }
        return v;
    }
}
