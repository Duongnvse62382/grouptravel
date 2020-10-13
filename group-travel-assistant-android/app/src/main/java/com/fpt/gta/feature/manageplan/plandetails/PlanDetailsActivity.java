package com.fpt.gta.feature.manageplan.plandetails;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fpt.gta.R;
import com.fpt.gta.feature.manageplan.activitytype.AccomodationFragment;
import com.fpt.gta.feature.manageplan.activitytype.ActivityFragment;
import com.fpt.gta.feature.manageplan.activitytype.TransportationFragment;
import com.fpt.gta.feature.manageplan.budgeting.BudgetPlanFragment;
import com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity.OverViewSuggestAndVoteCustomAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


public class PlanDetailsActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;
    private FragmentPagerItemAdapter mAdapter;
    private ImageView imgAddPlanBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        initView();
        initData();
    }


    public void initView() {
        imgAddPlanBack = findViewById(R.id.imgAddPlanBack);
        mViewPager = findViewById(R.id.viewpagerActivity);
        mSmartTabLayout = findViewById(R.id.view_pager_tab_activity);
    }

    public void initData() {
        imgAddPlanBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        viewTabAdapter();
    }


    public void viewTabAdapter() {
        FragmentPagerItems.Creator pageCreator = FragmentPagerItems.with(this);

        pageCreator.add(FragmentPagerItem.of("Activity", ActivityFragment.class));
        pageCreator.add(FragmentPagerItem.of("Accommodation", AccomodationFragment.class));
        pageCreator.add(FragmentPagerItem.of("Transportation", TransportationFragment.class));
        pageCreator.add(FragmentPagerItem.of("Budget", BudgetPlanFragment.class));


        FragmentPagerItems fragmentPagerItems = pageCreator.create();
        mAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), fragmentPagerItems);
        OverViewSuggestAndVoteCustomAdapter tabAdapter = new OverViewSuggestAndVoteCustomAdapter(this, fragmentPagerItems);
        mSmartTabLayout.setCustomTabView(tabAdapter);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mSmartTabLayout.setViewPager(mViewPager);
        TextView txtActivity = mSmartTabLayout.getTabAt(0).findViewById(R.id.name_tab);
        TextView txtAccomodation = mSmartTabLayout.getTabAt(1).findViewById(R.id.name_tab);
        TextView txtTransportation = mSmartTabLayout.getTabAt(2).findViewById(R.id.name_tab);
        TextView txtBudgetPlan = mSmartTabLayout.getTabAt(3).findViewById(R.id.name_tab);

        txtActivity.setText("Activity");
        txtActivity.setTextColor(Color.parseColor("#56A8A2"));
        txtAccomodation.setText("Accomodation");
        txtAccomodation.setTextColor(Color.parseColor("#000000"));
        txtTransportation.setText("Transportation");
        txtTransportation.setTextColor(Color.parseColor("#000000"));
        txtBudgetPlan.setText("Budget");
        txtBudgetPlan.setTextColor(Color.parseColor("#000000"));


        mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    txtActivity.setTextColor(Color.parseColor("#56A8A2"));
                    txtAccomodation.setTextColor(Color.parseColor("#000000"));
                    txtTransportation.setTextColor(Color.parseColor("#000000"));
                    txtBudgetPlan.setTextColor(Color.parseColor("#000000"));
                } else if (position == 1) {
                    txtActivity.setTextColor(Color.parseColor("#000000"));
                    txtAccomodation.setTextColor(Color.parseColor("#56A8A2"));
                    txtTransportation.setTextColor(Color.parseColor("#000000"));
                    txtBudgetPlan.setTextColor(Color.parseColor("#000000"));
                } else if (position == 2) {
                    txtActivity.setTextColor(Color.parseColor("#000000"));
                    txtAccomodation.setTextColor(Color.parseColor("#000000"));
                    txtTransportation.setTextColor(Color.parseColor("#56A8A2"));
                    txtBudgetPlan.setTextColor(Color.parseColor("#000000"));
                } else if (position == 3) {
                    txtActivity.setTextColor(Color.parseColor("#000000"));
                    txtAccomodation.setTextColor(Color.parseColor("#000000"));
                    txtTransportation.setTextColor(Color.parseColor("#000000"));
                    txtBudgetPlan.setTextColor(Color.parseColor("#56A8A2"));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


}