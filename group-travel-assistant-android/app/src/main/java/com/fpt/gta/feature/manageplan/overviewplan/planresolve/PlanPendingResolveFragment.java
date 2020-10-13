package com.fpt.gta.feature.manageplan.overviewplan.planresolve;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DayPlanResponseDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.presenter.PrintHighestVotePlanPresenter;
import com.fpt.gta.presenter.PickHighestVotePlanPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintHighestVotePlanView;
import com.fpt.gta.view.PickHighestVotePlanView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;


public class PlanPendingResolveFragment extends Fragment implements View.OnClickListener, PrintHighestVotePlanView, PickHighestVotePlanView {
    private ViewPager viewPager;
    private PlanResolveAdapter planApdater;
    private View mView;
    private List<DayPlanResponseDTO> dayPlanResponseDTOList;
    private DayPlanResponseDTO dayPlanResponseDTO;
    private List<PlanDTO> plansDTOList;
    private PrintHighestVotePlanPresenter mPrintHighestVotePlanPresenter;
    private PickHighestVotePlanPresenter mPickHighestVotePlanPresenter;
    private Integer iTrip, idGroup;
    private FirebaseDatabase databasePlan;
    private DatabaseReference listenerPlan;
    private DatabaseReference listenerPlanElected;
    private ValueEventListener planValueEventListener;
    private ValueEventListener planElectedValueEventListener;

    public PlanPendingResolveFragment() {
    }


    public static PlanPendingResolveFragment newInstance() {
        PlanPendingResolveFragment fragment = new PlanPendingResolveFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plan_pending_resolve, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
       detachListener();
    }

    private void detachListener(){
        try {
            if (planValueEventListener != null) {
                listenerPlan.removeEventListener(planValueEventListener);
            }
            if (planElectedValueEventListener != null) {
                listenerPlanElected.removeEventListener(planElectedValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        viewPager = mView.findViewById(R.id.viewPager);
        iTrip = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDTRIP);
        idGroup = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        databasePlan = FirebaseDatabase.getInstance();
        idGroup = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        listenerPlan = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlan");
        listenerPlanElected = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlanElected");
        mPickHighestVotePlanPresenter = new PickHighestVotePlanPresenter(getContext(), PlanPendingResolveFragment.this);
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
        mPrintHighestVotePlanPresenter = new PrintHighestVotePlanPresenter(getContext(), this);

        planValueEventListener = listenerPlan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintHighestVotePlanPresenter.getHighestVotePlan(iTrip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void updateUI() {
        if (planApdater == null) {
            planApdater = new PlanResolveAdapter(getContext(), plansDTOList);
            viewPager.setAdapter(planApdater);
            viewPager.setPadding(90, 0, 120, 0);
            planApdater.setOnPlanClickListener(new PlanResolveAdapter.OnPlanClickListener() {
                @Override
                public void onPlanClickListener(PlanDTO planDTO, int position) {

                }
            });

            planApdater.setOnResolveConflicClickListener(new PlanResolveAdapter.OnResolveConflicClickListener() {
                @Override
                public void onResolveConflicClickListener(PlanDTO planDTO, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure to Choose this Plan?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPickHighestVotePlanPresenter.resolveConflictPlan(planDTO.getId());
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else {
            planApdater.notifyChangeDataPendingResolve(plansDTOList);
        }

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

        }
    }


    @Override
    public void printVotePlanSuccess(List<PlanDTO> planDTOList) {
        if (planDTOList != null) {
            this.plansDTOList = new ArrayList<>();
            this.plansDTOList = planDTOList;
            updateUI();
            for (PlanDTO planDTO : plansDTOList) {
                if (planDTO.getIdStatus().equals(4)) {
                    planElectedValueEventListener = listenerPlanElected.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                Long date = snapshot.getValue(Long.class);
                                startActivity(getActivity().getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                getActivity().finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    break;
                }
            }
        }
    }

    @Override
    public void printVotePlanFail(String messageFail) {

    }


    @Override
    public void PickVotePlanSuccess(String messageSS) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        detachListener();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerPlanElected.setValue(change);
            listenerPlan.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(getActivity().getIntent());
        getActivity().finish();
    }

    @Override
    public void PickVotePlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }


}
