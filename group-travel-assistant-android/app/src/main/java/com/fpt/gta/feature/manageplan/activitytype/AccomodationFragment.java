package com.fpt.gta.feature.manageplan.activitytype;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.PlanStatus;
import com.fpt.gta.feature.manageplan.addactivity.AddAccomodationActivity;
import com.fpt.gta.feature.manageplan.addactivity.AddTransportationActivity;
import com.fpt.gta.presenter.PrintAllActivityInPlanPresenter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.PrintAllActivityInPlanView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AccomodationFragment extends Fragment implements PrintAllActivityInPlanView {

    private RecyclerView rcvAccomodation;
    private AccomodationAdapter accomodationAdapter;
    private int planId;
    private PlanDTO planDTO;
    private ImageView imgAddAccomodationActivity;
    private LinearLayout imgAccomodation;
    private List<ActivityDTO> fullActivity = new ArrayList<>();
    private PrintAllActivityInPlanPresenter mPrintAllActivityInPlanPresenter;
    private Integer idGroup;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;
    private ValueEventListener activityValueEventListener;
    private View mView;
    Integer idAdmin;

    public AccomodationFragment() {

    }


    public static AccomodationFragment newInstance(String param1, String param2) {
        AccomodationFragment fragment = new AccomodationFragment();
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
        mView = inflater.inflate(R.layout.fragment_accomodation, container, false);
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
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (activityValueEventListener != null) {
                listenerActivity.removeEventListener(activityValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        rcvAccomodation = (RecyclerView) mView.findViewById(R.id.rcvAccomodation);
        imgAddAccomodationActivity = (ImageView) mView.findViewById(R.id.imgAddAccomodationActivity);
        imgAccomodation = (LinearLayout) mView.findViewById(R.id.imgAccomodation);
        imgAddAccomodationActivity.setVisibility(View.GONE);
        rcvAccomodation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        try {
            String planDto = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.PLANOJECT);
            Gson gson = new Gson();
            planDTO = gson.fromJson(planDto, PlanDTO.class);

        } catch (Exception e) {
            e.getMessage();
        }

        idGroup = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");
    }

    public void initData() {
        planId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.PLANID);
        mPrintAllActivityInPlanPresenter = new PrintAllActivityInPlanPresenter(getContext(), this);
        goneWithNotAdmin();
        imgAddAccomodationActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNewActivty();
            }
        });
    }

    public void loadData() {
        activityValueEventListener = listenerActivity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintAllActivityInPlanPresenter.printAllActivityInPlan(planId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void goneWithNotAdmin() {
        idAdmin = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.ISADMIN);
        String idPerson = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String memberCreate = planDTO.getOwner().getPerson().getFirebaseUid();
        Integer groupStatus = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.GROUPSTATUS);
        if (!groupStatus.equals(GroupStatus.PLANNING)) {
            imgAddAccomodationActivity.setVisibility(View.GONE);
        } else {
            if (planDTO.getIdStatus().equals(PlanStatus.ELECTED)) {
                imgAddAccomodationActivity.setVisibility(View.GONE);
            } else {
                if (memberCreate.equals(idPerson)) {
                    imgAddAccomodationActivity.setVisibility(View.VISIBLE);
                }

            }
        }

    }


    public void onClickNewActivty() {
        Intent intent = new Intent(getContext(), AddAccomodationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ACTIVITYLIST, (Serializable) fullActivity);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public void updateUI() {
        if (accomodationAdapter == null) {
            accomodationAdapter = new AccomodationAdapter(getContext(), fullActivity, planDTO);
            rcvAccomodation.setAdapter(accomodationAdapter);
        } else {
            accomodationAdapter.notifyDataSetChangeActivity(fullActivity);
        }
    }


    @Override
    public void printAllActivitySuccess(List<ActivityDTO> activityDTOList) {
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
    }

    @Override
    public void printAllActivityFail(String messageFail) {

    }
}