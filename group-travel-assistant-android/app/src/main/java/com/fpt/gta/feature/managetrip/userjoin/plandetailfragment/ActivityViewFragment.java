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
import com.fpt.gta.data.dto.DayPlanResponseDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.feature.manageplan.plandetails.DayAdapter;
import com.fpt.gta.util.DateManagement;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.gson.Gson;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class ActivityViewFragment extends Fragment {
    private RecyclerView recyclerViewAddDay;
    private DayViewAdapter dayAdapter;
    private LinearLayout lnlImageNoActivity;
    private List<DayPlanResponseDTO> dayPlanResponseDTOList;
    private DayPlanResponseDTO dayPlanResponseDTO;
    private static float numberDay;
    private List<Date> mListDate;
    private List<ActivityDTO> fullActivity;
    private Date dayPlan;
    private PlanDTO planDTO;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();
    private View mView;

    public ActivityViewFragment() {

    }


    public static ActivityViewFragment newInstance(String param1, String param2) {
        ActivityViewFragment fragment = new ActivityViewFragment();
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
        mView =  inflater.inflate(R.layout.fragment_activity_view, container, false);
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
    }

    public void initView() {
        lnlImageNoActivity = (LinearLayout) mView.findViewById(R.id.lnlImageNoActivity);
        recyclerViewAddDay = (RecyclerView) mView.findViewById(R.id.rcvAddDayDetails);
        recyclerViewAddDay.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        lnlImageNoActivity.setVisibility(View.GONE);
    }

    public void initData() {
        String dateGo = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.DATETRIPGO);
        String dateEnd = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.DATETRIPEND);
        String planGson = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.PLANGSON);
        Gson gson = new Gson();
        planDTO = gson.fromJson(planGson, PlanDTO.class);

        if(planDTO != null){
            activityDTOList = planDTO.getActivityList();
            lnlImageNoActivity.setVisibility(View.GONE);
        }else {
            DialogShowErrorMessage.showValidationDialog(getContext(), "No plans have been elected yet!");
            lnlImageNoActivity.setVisibility(View.VISIBLE);
        }

        numberDay = DateManagement.numberOfDayBetween(dateGo, dateEnd);
        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            mListDate = getDatesBetween(myFormat.parse(dateGo), myFormat.parse(dateEnd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        activityFull();
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
            dayAdapter = new DayViewAdapter(getContext(), dayPlanResponseDTOList, planDTO);
            recyclerViewAddDay.setAdapter(dayAdapter);
        } else {
            dayAdapter.dataNotifySetChange(dayPlanResponseDTOList);
        }
    }

    public void activityFull(){
        try {
            fullActivity = new ArrayList<>();
            for (ActivityDTO activityDTO : activityDTOList) {
                if(activityDTO.getIdType().equals(ActivityType.ACTIVITY)){
                    fullActivity.add(activityDTO);
                }
            }
            setData();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}