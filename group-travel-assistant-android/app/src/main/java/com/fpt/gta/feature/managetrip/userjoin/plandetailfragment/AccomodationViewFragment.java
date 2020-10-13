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
import android.widget.LinearLayout;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AccomodationViewFragment extends Fragment {
    private RecyclerView rcvAccomodation;
    private AccomodationViewAdapter accomodationViewAdapter;
    private PlanDTO planDTO;
    private LinearLayout imgAccomodation;
    private List<ActivityDTO> fullActivity;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();
    private View mView;

    public AccomodationViewFragment() {

    }


    public static AccomodationViewFragment newInstance() {
        AccomodationViewFragment fragment = new AccomodationViewFragment();
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
        mView= inflater.inflate(R.layout.fragment_accomodation_view, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void initView() {
        rcvAccomodation = (RecyclerView) mView.findViewById(R.id.rcvAccomodation);
        imgAccomodation = (LinearLayout) mView.findViewById(R.id.imgAccomodation);
        imgAccomodation.setVisibility(View.GONE);
        rcvAccomodation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void initData() {
        String planDto = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.PLANGSON);
        Gson gson = new Gson();
        planDTO = gson.fromJson(planDto, PlanDTO.class);
        if (planDTO != null) {
            activityDTOList = planDTO.getActivityList();
        } else {
            imgAccomodation.setVisibility(View.VISIBLE);
        }
        activityAccomodation();
    }


    public void updateUI() {
        if (accomodationViewAdapter == null) {
            accomodationViewAdapter = new AccomodationViewAdapter(getContext(), fullActivity, planDTO);
            rcvAccomodation.setAdapter(accomodationViewAdapter);
        } else {
            accomodationViewAdapter.notifyDataSetChangeActivity(fullActivity);
        }
    }


    public void activityAccomodation() {
        try {
            fullActivity = new ArrayList<>();
            for (ActivityDTO activityDTO : activityDTOList) {
                if (activityDTO.getIdType().equals(ActivityType.ACCOMMODATION)) {
                    fullActivity.add(activityDTO);
                }
            }
            Collections.sort(fullActivity, new Comparator<ActivityDTO>() {
                @Override
                public int compare(ActivityDTO o1, ActivityDTO o2) {
                    return o1.getStartUtcAt().compareTo(o2.getStartUtcAt());
                }
            });

            if (fullActivity.size() != 0) {
                imgAccomodation.setVisibility(View.GONE);
            } else {
                imgAccomodation.setVisibility(View.VISIBLE);
            }
            updateUI();
        } catch (Exception e) {

        }

    }
}