package com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.PlanStatus;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.manageplan.overviewplan.planelected.PlanElectedFragment;
import com.fpt.gta.feature.manageplan.overviewplan.planmanage.PlanManageFragment;
import com.fpt.gta.feature.manageplan.overviewplan.planresolve.PlanPendingResolveFragment;
import com.fpt.gta.presenter.PrintPlanInTripPresenter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.PrintPlanInTripView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

public class SuggestAndVoteActivity extends AppCompatActivity implements View.OnClickListener, PrintPlanInTripView {
    private ViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;
    private FragmentPagerItemAdapter mAdapter;
    private ImageView imgSuggestandVoteBack;
    private TripReponseDTO mTrip;
    private TextView txtPlanItemName;
    private List<PlanDTO> plansDTOList;
    private List<MemberDTO> memberDTOList;
    private PrintPlanInTripPresenter mPrintPlanInTripPresenter;

    private Integer idGroup;
    private FirebaseDatabase databasePlan;
    private DatabaseReference listenerPlan;
    private DatabaseReference listenerPlanElected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_suggest_and_vote);
        initialView();
        initialData();
    }



    private void initialView() {
        mViewPager = findViewById(R.id.viewpager);
        mSmartTabLayout = findViewById(R.id.view_pager_tab);
        imgSuggestandVoteBack = findViewById(R.id.imgSuggestandVoteActivityBack);
        Bundle bundle = getIntent().getExtras();
        mTrip = (TripReponseDTO) bundle.getSerializable(GTABundle.KEYTRIP_onClickItem);
        memberDTOList = (List<MemberDTO>) bundle.getSerializable(GTABundle.KEYOWNER);
        txtPlanItemName = findViewById(R.id.txtTripItemName);
        String nameTrip = mTrip.getStartPlace().getName();
        txtPlanItemName.setText(nameTrip);
        String imageUri = mTrip.getStartPlace().getPlaceImageList().get(0).getUri();
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.NAMETRIP, nameTrip);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.IMAGETRIP, imageUri);

        idGroup = SharePreferenceUtils.getIntSharedPreference (this, GTABundle.IDGROUP );
        databasePlan = FirebaseDatabase.getInstance();
        listenerPlan = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlan");
        listenerPlanElected = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlanElected");
    }


    private void initialData() {
        imgSuggestandVoteBack.setOnClickListener(this::onClick);
        mPrintPlanInTripPresenter = new PrintPlanInTripPresenter(this, this);
        mPrintPlanInTripPresenter.printPlanInTrip(mTrip.getId());
    }


    public void viewTabAdapter() {
        FragmentPagerItems.Creator pageCreator = FragmentPagerItems.with(this);
        boolean isAdmin = false;
        boolean isElected = false;
        for (MemberDTO memberDTO : memberDTOList) {
            String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String memberUid = memberDTO.getPerson().getFirebaseUid();
            Integer idRole = memberDTO.getIdRole();
            if (idFribase.equals(memberUid) && idRole.equals(MemberRole.ADMIN)) {
                isAdmin = true;
                break;
            }
        }

        for (PlanDTO planDTO : plansDTOList) {
            Integer idStatus = planDTO.getIdStatus();
            if (idStatus.equals(PlanStatus.ELECTED)) {
                isElected = true;
                break;
            }
        }

        if (isElected) {
            pageCreator.add(FragmentPagerItem.of("Suggested Activity", SuggestActivityFragment.class));
            pageCreator.add(FragmentPagerItem.of("Plan Elected", PlanElectedFragment.class));
        } else {
            pageCreator.add(FragmentPagerItem.of("Suggested Activity", SuggestActivityFragment.class));
            pageCreator.add(FragmentPagerItem.of("Plan", PlanManageFragment.class));
            if (isAdmin) {
                pageCreator.add(FragmentPagerItem.of("Pending Resolve", PlanPendingResolveFragment.class));
            }
        }

        FragmentPagerItems fragmentPagerItems = pageCreator.create();
        mAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), fragmentPagerItems);
        OverViewSuggestAndVoteCustomAdapter tabAdapter = new OverViewSuggestAndVoteCustomAdapter(SuggestAndVoteActivity.this, fragmentPagerItems);
        mSmartTabLayout.setCustomTabView(tabAdapter);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mSmartTabLayout.setViewPager(mViewPager);
        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            TextView textView = mSmartTabLayout.getTabAt(i).findViewById(R.id.name_tab);
            if (i == 0) {
                textView.setTextColor(Color.parseColor("#56A8A2"));
            } else {
                textView.setTextColor(Color.parseColor("#000000"));
            }
        }


        mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                    TextView textView = mSmartTabLayout.getTabAt(i).findViewById(R.id.name_tab);
                    if (i == position) {
                        textView.setTextColor(Color.parseColor("#56A8A2"));
                    } else {
                        textView.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgSuggestandVoteActivityBack:
                finish();
                break;

        }
    }

    @Override
    public void printPlanSuccess(List<PlanDTO> planDTOList) {
        if (planDTOList != null) {
            plansDTOList = new ArrayList<>();
            plansDTOList = planDTOList;
            viewTabAdapter();
        }
    }

    @Override
    public void printPlanFail(String messageFail) {

    }
}