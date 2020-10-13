package com.fpt.gta.feature.manageplan.plansuggested;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.DayPlanResponseDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.presenter.CreatePlanIntTripPresenter;
import com.fpt.gta.util.DateManagement;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreatePlanInTripView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PlanSuggestedActivity extends AppCompatActivity implements View.OnClickListener, CreatePlanInTripView {

    private RecyclerView rcvDayPlanSuggested;

    private RecyclerView rcvIsPlanFalse;
    private LinearLayout lnlNotCalculate;
    private PlanDaySuggestedAdapter planDaySuggestedAdapter;
    private PlanIsFalseAdapter planIsFalseAdapter;
    private List<DayPlanResponseDTO> dayPlanResponseDTOList;
    private DayPlanResponseDTO dayPlanResponseDTO;
    private List<Date> mListDate;
    private List<ActivityDTO> activityDTOList;
    private List<ActivityDTO> activityDTOListPlan = new ArrayList<>();
    private List<ActivityDTO> activityIsPlanFalse = new ArrayList<>();
    private int planId;
    private Integer idTrip;
    private Date dayPlan;
    private ImageView imgCreatePlanSuggted, imgPlanSuggestedBack;
    private CreatePlanIntTripPresenter mCreatePlanIntTripPresenter;

    private Integer idGroup;
    private FirebaseDatabase databasePlan;
    private DatabaseReference listenerPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_suggested);
        initView();
        initData();
    }


    public void initView() {
        rcvDayPlanSuggested = findViewById(R.id.rcvDayPlanSuggested);
        imgPlanSuggestedBack = findViewById(R.id.imgPlanSuggestedBack);
        imgCreatePlanSuggted = findViewById(R.id.imgCreatePlanSuggted);
        lnlNotCalculate = findViewById(R.id.lnlNotCalculate);
        lnlNotCalculate.setVisibility(View.GONE);
        rcvIsPlanFalse = findViewById(R.id.rcvIsPlanFalse);
        rcvDayPlanSuggested.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvIsPlanFalse.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Bundle bundle = getIntent().getExtras();
        activityDTOList = (List<ActivityDTO>) bundle.getSerializable(GTABundle.ACTIVITYOJECT);
        mCreatePlanIntTripPresenter = new CreatePlanIntTripPresenter(this, this);

        for (ActivityDTO activityDTO : activityDTOList) {
            if(activityDTO.getIsInPlan().equals(false)){
                activityIsPlanFalse.add(activityDTO);
            }
        }


        for (ActivityDTO activityDTO : activityDTOList) {
            if(activityDTO.getIsInPlan().equals(true)){
                activityDTOListPlan.add(activityDTO);
            }

        }

        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databasePlan = FirebaseDatabase.getInstance();
        listenerPlan = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlan");
    }

    public void initData() {
        imgCreatePlanSuggted.setOnClickListener(this::onClick);
        imgPlanSuggestedBack.setOnClickListener(this::onClick);
        String dateGo = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPGO);
        String dateEnd = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPEND);
        idTrip = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDTRIP);
        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            mListDate = getDatesBetween(myFormat.parse(dateGo), myFormat.parse(dateEnd));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setData();
        updateUIPlanFlase();
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
        if (activityDTOList.size() != 0) {
            dayPlanResponseDTOList = new ArrayList<>();
            for (int i = 0; i < mListDate.size(); i++) {
                int count = i + 1;
                dayPlanResponseDTO = new DayPlanResponseDTO();
                dayPlanResponseDTO.setDayName("Day " + count);
                dayPlanResponseDTO.setDayStart(mListDate.get(i));
                dayPlanResponseDTO.setActivityDTOList(new ArrayList<>());
                try{
                    for (int j = 0; j < activityDTOList.size(); j++) {
                        if (

                                ZonedDateTimeUtil.convertDateToStringASIA(mListDate.get(i))
                                        .equals(
                                                ZonedDateTimeUtil.convertDateToStringASIA(activityDTOList.get(j).getStartAt())
                                        )

                        ) {
                            dayPlanResponseDTO.getActivityDTOList().add(activityDTOList.get(j));
                        }
                    }
                }catch (Exception e){
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
        if (planDaySuggestedAdapter == null) {
            planDaySuggestedAdapter = new PlanDaySuggestedAdapter(this, dayPlanResponseDTOList);
            rcvDayPlanSuggested.setAdapter(planDaySuggestedAdapter);
        } else {
            planDaySuggestedAdapter.dataNotifySetChange(dayPlanResponseDTOList);
        }
    }


    public void updateUIPlanFlase() {
        if(activityIsPlanFalse.size() > 0){
            DialogShowErrorMessage.showValidationDialog(this, "Some activity not suggested!");
            lnlNotCalculate.setVisibility(View.VISIBLE);
        }
        if (planIsFalseAdapter == null) {
            planIsFalseAdapter = new PlanIsFalseAdapter(this, activityIsPlanFalse);
            rcvIsPlanFalse.setAdapter(planIsFalseAdapter);
        }else {
            planIsFalseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgPlanSuggestedBack:
                finish();
                break;

            case R.id.imgCreatePlanSuggted:
                createPlanWithSuggestedActivity();
                break;
        }
    }

    public void createPlanWithSuggestedActivity() {
        mCreatePlanIntTripPresenter.createPlanIntrip(idTrip, activityDTOListPlan);
    }

    @Override
    public void getIdPlan(PlanDTO planDTO) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerPlan.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void createPlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}