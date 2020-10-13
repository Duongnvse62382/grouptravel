package com.fpt.gta.feature.manageplan.activitytype;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.DayPlanResponseDTO;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.feature.manageplan.addactivity.AddActivity;
import com.fpt.gta.feature.manageplan.plandetails.DayAdapter;
import com.fpt.gta.presenter.AddActivityDayPresenter;
import com.fpt.gta.presenter.PrintAllActivityInPlanPresenter;
import com.fpt.gta.presenter.PrintSuggestedActivityPresenter;
import com.fpt.gta.util.DateManagement;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ListUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.AddActivityDayView;
import com.fpt.gta.view.PrintAllActivityInPlanView;
import com.fpt.gta.view.PrintSuggestedActivityView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class ActivityFragment extends Fragment implements PrintAllActivityInPlanView, PrintSuggestedActivityView, AddActivityDayView, SuggestedPlaceFilterSection.ClickListener {

    private RecyclerView recyclerViewAddDay;
    private DayAdapter dayAdapter;
    private List<DayPlanResponseDTO> dayPlanResponseDTOList;
    private DayPlanResponseDTO dayPlanResponseDTO;
    private static float numberDay;
    private List<Date> mListDate;
    private List<ActivityDTO> fullActivity = new ArrayList<>();
    private int planId;
    private PrintAllActivityInPlanPresenter mPrintAllActivityInPlanPresenter;
    private PrintSuggestedActivityPresenter mPrintSuggestedActivityPresenter;
    private AddActivityDayPresenter addActivityDayPresenter;
    private Integer idTrip;
    private Date dayPlan;
    private PlanDTO planDTO;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();

    private List<SuggestedActivityResponseDTO> suggestedLitsDTOS = new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private List<SuggestedActivityResponseDTO> suggestedLitsChoose;
    private List<SuggestedActivityResponseDTO> suggestedLitsNotYetChoose;
    private Button btnAddChbActivity;
    private Button btnAddNewActivity;

    private Integer idGroup;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;
    private ValueEventListener activityValueEventListener;
    private View mView;



    public ActivityFragment() {

    }


    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
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
        mView =  inflater.inflate(R.layout.fragment_activity, container, false);
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
       try {
           if(activityValueEventListener != null ){
               listenerActivity.removeEventListener(activityValueEventListener);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    public void initView() {
        recyclerViewAddDay = (RecyclerView) mView.findViewById(R.id.rcvAddDayDetails);
        recyclerViewAddDay.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
        mPrintSuggestedActivityPresenter = new PrintSuggestedActivityPresenter(getContext(), this);
        mPrintAllActivityInPlanPresenter = new PrintAllActivityInPlanPresenter(getContext(), this);
    }

    public void initData() {
        String dateGo = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.DATETRIPGO);
        String dateEnd = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.DATETRIPEND);
        numberDay = DateManagement.numberOfDayBetween(dateGo, dateEnd);
        idTrip = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDTRIP);
        planId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.PLANID);
        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            mListDate = getDatesBetween(myFormat.parse(dateGo), myFormat.parse(dateEnd));
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        mPrintSuggestedActivityPresenter.getSuggestedActivity(idTrip);

    }

    public static List<Date> getDatesBetween(
            Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    private void setData() {
        if (fullActivity.size() != 0) {
            dayPlanResponseDTOList = new ArrayList<>();
            for (int i = 0; i < mListDate.size(); i++) {
                int count = i + 1;
                dayPlanResponseDTO = new DayPlanResponseDTO();
                dayPlanResponseDTO.setDayName("Day " + count);
                dayPlanResponseDTO.setDayStart(mListDate.get(i));
                dayPlanResponseDTO.setActivityDTOList(new ArrayList<>());
                try {
                    for (int j = 0; j < fullActivity.size(); j++) {
                        if (
                                ZonedDateTimeUtil.convertDateToStringASIA(mListDate.get(i))
                                        .equals(
                                                ZonedDateTimeUtil.convertDateToStringASIA(fullActivity.get(j).getStartAt())
                                        )

                        ) {
                            dayPlanResponseDTO.getActivityDTOList().add(fullActivity.get(j));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dayPlanResponseDTOList.add(dayPlanResponseDTO);
            }
            updateUI();
        } else {
            dayPlanResponseDTOList = new ArrayList<>();
            for (int i = 0; i < mListDate.size(); i++) {
                int count = i + 1;
                dayPlanResponseDTO = new DayPlanResponseDTO();
                dayPlanResponseDTO.setDayName("Day" + count);
                dayPlanResponseDTO.setDayStart(mListDate.get(i));
                List<ActivityDTO> activityDTOList = new ArrayList<>();
                dayPlanResponseDTO.setActivityDTOList(activityDTOList);
                dayPlanResponseDTOList.add(dayPlanResponseDTO);
            }
            updateUI();
        }

    }


    public void updateUI() {
        if (dayAdapter == null) {
            dayAdapter = new DayAdapter(getContext(), dayPlanResponseDTOList, planDTO);
            recyclerViewAddDay.setAdapter(dayAdapter);
            dayAdapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(DayPlanResponseDTO dayPlanResponseDTO, int position) {
                    dayPlan = dayPlanResponseDTO.getDayStart();
                    onItemAddClick();
                }
            });
        } else {
            dayAdapter.dataNotifySetChange(dayPlanResponseDTOList);
        }
    }


    public void onItemAddClick() {
        showSuggestedPlanDialog();
    }


    private void showSuggestedPlanDialog() {
        activityDTOList = new ArrayList<>();
        final Dialog dialog = new Dialog(getContext());
        for (SuggestedActivityResponseDTO suggestedActivityResponseDTO : suggestedLitsDTOS) {
            suggestedActivityResponseDTO.setSelected(false);
        }
        dialog.setContentView(R.layout.layout_dialog_suggested);
        btnAddNewActivity = dialog.findViewById(R.id.btnAddNewActivity);
        btnAddNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNewActivty();
                dialog.dismiss();
            }
        });
        ImageView imgDissmiss = dialog.findViewById(R.id.imgDissmiss);
        imgDissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAddChbActivity = dialog.findViewById(R.id.btnSubmitCheckActivity);
        btnAddChbActivity.setVisibility(View.GONE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rcvMemberDialog = dialog.findViewById(R.id.rcvDialogSuggested);
        rcvMemberDialog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        sectionedAdapter = new SectionedRecyclerViewAdapter();
        suggestedLitsChoose = new ArrayList<>();
        suggestedLitsNotYetChoose = new ArrayList<>();
        for (SuggestedActivityResponseDTO suggestedLitsDTO : suggestedLitsDTOS) {
            String idGoogleStartSuggeted = suggestedLitsDTO.getStartPlace().getGooglePlaceId();
            String idGoogleEndSuggeted = suggestedLitsDTO.getEndPlace().getGooglePlaceId();
            boolean isContaint = false;
            for (ActivityDTO activityDTO : fullActivity) {
                String idGoogleStartAtivity = activityDTO.getStartPlace().getGooglePlaceId();
                String idGoogleEndAtivity = activityDTO.getEndPlace().getGooglePlaceId();
                if (idGoogleStartSuggeted.equals(idGoogleStartAtivity) && idGoogleEndSuggeted.equals(idGoogleEndAtivity)) {
                    isContaint = true;
                    break;
                }
            }
            if (isContaint) {
                suggestedLitsChoose.add(suggestedLitsDTO);
            } else {
                suggestedLitsNotYetChoose.add(suggestedLitsDTO);
            }
        }


        if (suggestedLitsNotYetChoose.size() > 0) {
            sectionedAdapter.addSection(new SuggestedPlaceFilterSection(suggestedLitsNotYetChoose, getContext(), "Place Not Yet Choose", R.mipmap.placelocationbg, this));
        }

        if (suggestedLitsChoose.size() > 0) {
            sectionedAdapter.addSection(new SuggestedPlaceFilterSection(suggestedLitsChoose, getContext(), "Place Chosen", R.mipmap.placelocationbg, this));
        }


        rcvMemberDialog.setAdapter(sectionedAdapter);
        sectionedAdapter.notifyDataSetChanged();

        btnAddChbActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.PLANID);
                addActivityDayPresenter = new AddActivityDayPresenter(getContext(), ActivityFragment.this);
                prepareActivityResponseDTOListFromForm();
                for (ActivityDTO activityDTO : activityDTOList) {
                    addActivityDayPresenter.AddActivity(planId, activityDTO);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void prepareActivityResponseDTOListFromForm() {
        for (SuggestedActivityResponseDTO suggestedActivityResponseDTO : suggestedLitsDTOS) {
            if (suggestedActivityResponseDTO.isSelected()) {
                ActivityDTO activityDTO = new ActivityDTO();
                String nameActivity = suggestedActivityResponseDTO.getName();
                Integer idType = suggestedActivityResponseDTO.getIdType();
                String googlePlaceStartID = suggestedActivityResponseDTO.getStartPlace().getGooglePlaceId();
                String googlePlaceEndID = suggestedActivityResponseDTO.getEndPlace().getGooglePlaceId();
                Boolean isTooFar = suggestedActivityResponseDTO.getIsTooFar();
                activityDTO.setName(nameActivity);
                activityDTO.setIdType(idType);
                activityDTO.setIsTooFar(isTooFar);
                PlaceDTO placeDTOStart = new PlaceDTO();
                placeDTOStart.setGooglePlaceId(googlePlaceStartID);
                activityDTO.setStartPlace(placeDTOStart);
                activityDTO.setStartAt(dayPlan);
                activityDTO.setEndAt(dayPlan);
                PlaceDTO placeDTOEnd = new PlaceDTO();
                placeDTOEnd.setGooglePlaceId(googlePlaceEndID);
                activityDTO.setEndPlace(placeDTOEnd);
                activityDTOList.add(activityDTO);
            }
        }
    }

    public void sendDayActivity(String dayActivity) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATEACTIVITY, dayActivity);
    }

    public void onClickNewActivty() {
        Intent intent = new Intent(getContext(), AddActivity.class);
        String dayActivity = ZonedDateTimeUtil.convertDateToStringASIA(dayPlan);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ACTIVITYLIST, (Serializable) fullActivity);
        sendDayActivity(dayActivity);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void printAllActivitySuccess(List<ActivityDTO> activityDTOList) {
        fullActivity = new ArrayList<>();
        for (ActivityDTO activityDTO : activityDTOList) {
            if(activityDTO.getIdType().equals(ActivityType.ACTIVITY) || activityDTO.getIdType().equals(ActivityType.FOODANDBEVERAGE)){
                fullActivity.add(activityDTO);
            }
        }
        setData();
    }

    @Override
    public void printAllActivityFail(String messageFail) {

    }

    @Override
    public void getSuggestedSuccess(List<SuggestedActivityResponseDTO> suggestedActivityResponseDTOList) {
        suggestedLitsDTOS = new ArrayList<>();
        Set<SuggestedActivityResponseDTO>  set = new HashSet<>();
        suggestedActivityResponseDTOList.forEach(s->{
            set.add(s);
        });

        suggestedLitsDTOS = ListUtil.convertSetToList(set);
        Comparator<SuggestedActivityResponseDTO> suggestedActivityDTOComparator = new Comparator<SuggestedActivityResponseDTO>() {
            @Override
            public int compare(SuggestedActivityResponseDTO o1, SuggestedActivityResponseDTO o2) {
                return Integer.compare(o2.getVotedSuggestedActivityList().size(), o1.getVotedSuggestedActivityList().size());
            }
        };
        Collections.sort(suggestedLitsDTOS, suggestedActivityDTOComparator);


    }

    @Override
    public void getSuggestedFail(String message) {

    }

    @Override
    public void AddActivitySuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerActivity.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void AddActivityFail(String message) {
        DialogShowErrorMessage.showValidationDialog(getContext(), message);
    }

    @Override
    public void onItemRootViewClicked(int itemAdapterPosition, SuggestedActivityResponseDTO suggestedActivityResponseDTO) {
        btnAddChbActivity.setVisibility(View.GONE);
        btnAddNewActivity.setVisibility(View.VISIBLE);
        for (SuggestedActivityResponseDTO responseDTO : suggestedLitsDTOS) {
            if (responseDTO.isSelected()) {
                btnAddChbActivity.setVisibility(View.VISIBLE);
                btnAddNewActivity.setVisibility(View.GONE);
                break;
            }
        }
    }

}