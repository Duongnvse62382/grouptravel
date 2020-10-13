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


public class TransportationViewFragment extends Fragment {

    private RecyclerView rcvTransportation;
    private TransportationViewAdapter transportationAdapter;
    private PlanDTO planDTO;
    private LinearLayout imgTransportation;
    private List<ActivityDTO> fullActivity;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();
    private View mView;

    public TransportationViewFragment() {

    }


    public static TransportationViewFragment newInstance() {
        TransportationViewFragment fragment = new TransportationViewFragment();
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
        mView = inflater.inflate(R.layout.fragment_transportation_view, container, false);
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
        rcvTransportation = (RecyclerView) mView.findViewById(R.id.rcvTransportation);
        imgTransportation = (LinearLayout) mView.findViewById(R.id.imgTransportation);
        imgTransportation.setVisibility(View.GONE);
        rcvTransportation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void initData() {
        String planDto = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.PLANGSON);
        Gson gson = new Gson();
        planDTO = gson.fromJson(planDto, PlanDTO.class);
        if (planDTO != null) {
            activityDTOList = planDTO.getActivityList();
        } else {
            imgTransportation.setVisibility(View.VISIBLE);
        }
        activityTransportation();
    }


    public void updateUI() {
        if (transportationAdapter == null) {
            transportationAdapter = new TransportationViewAdapter(getContext(), fullActivity, planDTO);
            rcvTransportation.setAdapter(transportationAdapter);
        } else {
            transportationAdapter.notifyDataSetChangeActivity(fullActivity);
        }
    }


    public void activityTransportation() {
        try {
            fullActivity = new ArrayList<>();
            for (ActivityDTO activityDTO : activityDTOList) {
                if (activityDTO.getIdType().equals(ActivityType.TRANSPORTATION)) {
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
                imgTransportation.setVisibility(View.GONE);
            } else {
                imgTransportation.setVisibility(View.VISIBLE);
            }
            updateUI();
        } catch (Exception e) {

        }

    }
}