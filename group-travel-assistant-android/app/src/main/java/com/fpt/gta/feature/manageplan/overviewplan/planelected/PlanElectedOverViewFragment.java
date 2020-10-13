package com.fpt.gta.feature.manageplan.overviewplan.planelected;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.presenter.ChangeVoteDeadlinePresenter;
import com.fpt.gta.presenter.PrintPlanInTripPresenter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;


public class PlanElectedOverViewFragment extends Fragment {
    private Integer idGroup;
    private ViewPager viewPager;
    private PlanElectedAdapter planApdater;
    private View mView;
    private List<PlanDTO> plansDTOList;

    public PlanElectedOverViewFragment() {

    }

    public static PlanElectedOverViewFragment newInstance() {
        PlanElectedOverViewFragment fragment = new PlanElectedOverViewFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plan_elected_over_view, container, false );
        return mView;
    }

    public void initView() {
        viewPager = mView.findViewById(R.id.viewPager);

        idGroup = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);

    }

    public void initData() {
        int colors_temp = getResources().getColor(R.color.colorTedarari);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.setBackgroundColor(colors_temp);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public void updateUI() {
        if (planApdater == null) {
            planApdater = new PlanElectedAdapter(getContext(), plansDTOList);
            viewPager.setAdapter(planApdater);
            viewPager.setPadding(90, 0, 120, 0);
            planApdater.setOnPlanClickListener(new PlanElectedAdapter.OnPlanClickListener() {
                @Override
                public void onPlanClickListener(PlanDTO planDTO, int position) {

                }
            });
        } else {
            planApdater.notifyChangeDataElected(plansDTOList);
        }

    }

}