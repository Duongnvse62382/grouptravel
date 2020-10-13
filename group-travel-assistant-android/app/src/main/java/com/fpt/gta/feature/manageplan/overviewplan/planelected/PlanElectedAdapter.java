package com.fpt.gta.feature.manageplan.overviewplan.planelected;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.DayPlanResponseDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.PlanStatus;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.feature.manageplan.plandetails.PlanDetailsActivity;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PlanElectedAdapter extends PagerAdapter {

    private Context mContext;
    private List<PlanDTO> planDTOList;
    private List<Date> mListDate;
    private LayoutInflater layoutInflater;
    private CurrencyDTO currencyDTO;
    private String currencyCode;
    private PlanElectedAdapter.OnPlanClickListener onPlanClickListener;

    public PlanElectedAdapter(Context mContext, List<PlanDTO> planDTOList) {
        this.mContext = mContext;
        this.planDTOList = planDTOList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.row_recyclerview_plan, container, false);
        TextView txtNamePersonCreate;
        ImageView imgPlan;
        ImageView imgDelete;
        ImageView imgVotePlan;
        ImageView imgUnVotePlan;
        LinearLayout lnlRcvExtraDayPlan, lnlRowPlan, lnlDayOfPlan;
        TextView txtNumberVotePlan;
        TextView txtTotalBudgetPendingPlan, txtDayName, txtLocation;
        lnlDayOfPlan = (LinearLayout) view.findViewById(R.id.lnlDayOfPlan);
        lnlRowPlan = (LinearLayout) view.findViewById(R.id.lnlRowPlan);
        lnlRcvExtraDayPlan = (LinearLayout) view.findViewById(R.id.lnlRecycleViewDayPlan);
        txtTotalBudgetPendingPlan = (TextView) view.findViewById(R.id.txtTotalBudgetIndividual);
        txtNamePersonCreate = (TextView) view.findViewById(R.id.txtNamePersonCreate);
        txtNumberVotePlan = (TextView) view.findViewById(R.id.txtNumberVotePlan);
        imgDelete = (ImageView) view.findViewById(R.id.imgDeletePlan);
        imgVotePlan = (ImageView) view.findViewById(R.id.imgVotePlan);
        imgUnVotePlan = (ImageView) view.findViewById(R.id.imgUnVotePlan);
        txtTotalBudgetPendingPlan.setVisibility(View.GONE);
        txtNamePersonCreate.setVisibility(View.GONE);
        imgUnVotePlan.setVisibility(View.GONE);
        imgDelete.setVisibility(View.GONE);
        imgVotePlan.setVisibility(View.GONE);
        txtNumberVotePlan.setText("");
        String idPerson = FirebaseAuth.getInstance().getCurrentUser().getUid();

        int i;

        String dateGo = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPGO);
        String dateEnd = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPEND);

        currencyCode = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.GROUP_CURRENCY_SHARE);
        Gson gson = new Gson();
        currencyDTO = gson.fromJson(currencyCode, CurrencyDTO.class);
        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            mListDate = getDatesBetween(myFormat.parse(dateGo), myFormat.parse(dateEnd));
        } catch (Exception e) {
            e.printStackTrace();
        }


        lnlRowPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlanClickListener != null) {
                    onPlanClickListener.onPlanClickListener(planDTOList.get(position), position);
                    Gson gson = new Gson();
                    String planDTOS = gson.toJson(planDTOList.get(position));
                    SharePreferenceUtils.saveStringSharedPreference(mContext, GTABundle.PLANOJECT, planDTOS);
//                    SharePreferenceUtils.saveBooleanSharedPreference(mContext, GTABundle.CHECKELECTED, );
                }
            }
        });
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(planDTOList.get(position).getActivityBudget()).add(planDTOList.get(position).getAccommodationBudget()).add(planDTOList.get(position).getTransportationBudget()).add(planDTOList.get(position).getFoodBudget());
        txtTotalBudgetPendingPlan.setVisibility(View.VISIBLE);
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            txtTotalBudgetPendingPlan.setText("Budget: " + " " + ChangeValue.formatBigCurrency(total) + " " + currencyDTO.getCode());
        }else {
            txtTotalBudgetPendingPlan.setText("Budget: " + " " + 0 + " " + currencyDTO.getCode());
        }
        imgPlan = (ImageView) view.findViewById(R.id.imgPlan);
        String imgTripUri = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.IMAGETRIP);
        ImageLoaderUtil.loadImage(mContext, imgTripUri, imgPlan);
        lnlRcvExtraDayPlan.removeAllViews();


        PlanDTO planDTO = planDTOList.get(position);
        List<DayPlanResponseDTO> dayPlanResponseDTOList = new ArrayList<>();
        planDTO.setDayPlanResponseDTOS(dayPlanResponseDTOList);
        for (Date date : mListDate) {
            DayPlanResponseDTO dayPlanResponseDTO = new DayPlanResponseDTO();
            dayPlanResponseDTO.setDayStart(date);
            dayPlanResponseDTOList.add(dayPlanResponseDTO);
        }

        for (ActivityDTO activityDTO : planDTO.getActivityList()) {
            for (DayPlanResponseDTO dayPlanResponseDTO : dayPlanResponseDTOList) {
                if (ZonedDateTimeUtil.compareTwoDate(activityDTO.getStartAt(), dayPlanResponseDTO.getDayStart())) {
                    dayPlanResponseDTO.getActivityDTOList().add(activityDTO);
                }
            }
        }

        for (DayPlanResponseDTO dayPlanResponseDTO : dayPlanResponseDTOList) {
            dayPlanResponseDTO.getActivityDTOList().sort(new Comparator<ActivityDTO>() {
                @Override
                public int compare(ActivityDTO activityDTO, ActivityDTO t1) {
                    if (activityDTO.getStartUtcAt().getTime() < t1.getStartUtcAt().getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }

        for (i = 0; i < planDTO.getDayPlanResponseDTOS().size(); i++) {
            int count = 0;
            count = i + 1;
            View viewItem = LayoutInflater.from(mContext).inflate(R.layout.row_extra_day, null, false);
            txtDayName = (TextView) viewItem.findViewById(R.id.txtDayNumber);

            Date dayPlan = planDTO.getDayPlanResponseDTOS().get(i).getDayStart();

            Date currentDate = new Date(Instant.now().toEpochMilli());
            TimeZone tz = TimeZone.getDefault();

            String satrtTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPGOUTC);
            String endTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPENDUTC);
            Date dateEndTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(endTripUTC);
            Date convertedDateEndUTC = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndTripUtc, tz.getID());


            Date currentDateGoing = ZonedDateTimeUtil.convertStringToDateOrTime(ZonedDateTimeUtil.convertDateToStringASIA(currentDate));
            Date datePlan = ZonedDateTimeUtil.convertStringToDateOrTime(ZonedDateTimeUtil.convertDateToStringASIA(dayPlan));

            if (currentDate.after(convertedDateEndUTC)) {
                txtDayName.setBackgroundResource(R.drawable.shaperecycleviewdaygrey);
            }

//            if (currentDateGoing.after(datePlan)) {
//                txtDayName.setBackgroundResource(R.drawable.shaperecycleviewdaygrey);
//            }

            txtDayName.setText("Day " + count + " | " + ZonedDateTimeUtil.convertDateToStringASIA(dayPlan));

            LinearLayout mlnlView = viewItem.findViewById(R.id.lnl_extra_suggest_daybyday);
            mlnlView.removeAllViews();
            Collections.sort(planDTO.getDayPlanResponseDTOS().get(i).getActivityDTOList());
            List<ActivityDTO> activityDTOList = new ArrayList<>();
            activityDTOList = planDTO.getDayPlanResponseDTOS().get(i).getActivityDTOList();


            for (int j = 0; j < activityDTOList.size(); j++) {
                Collections.sort(activityDTOList);
                if(activityDTOList.get(j).getIdType().equals(ActivityType.ACTIVITY) || activityDTOList.get(j).getIdType().equals(ActivityType.FOODANDBEVERAGE)){
                    View viewItem1 = LayoutInflater.from(mContext).inflate(R.layout.row_extra_activityday, null, false);
                    txtLocation = (TextView) viewItem1.findViewById(R.id.txtLocation1);
                    String namePlaceStart = activityDTOList.get(j).getStartPlace().getName();
                    txtLocation.setText(namePlaceStart);
                    mlnlView.addView(viewItem1);
                }
            }
            lnlRcvExtraDayPlan.addView(viewItem);
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharePreferenceUtils.saveIntSharedPreference(mContext, GTABundle.PLANID, planDTOList.get(position).getId());
                Intent intent = new Intent(mContext, PlanDetailsActivity.class);
                Gson gson = new Gson();
                String planDTOS = gson.toJson(planDTO);
                SharePreferenceUtils.saveStringSharedPreference(mContext, GTABundle.PLANOJECT, planDTOS);
                mContext.startActivity(intent);
            }
        });
        container.addView(view, 0);

        return view;
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

    public void setOnPlanClickListener(PlanElectedAdapter.OnPlanClickListener onPlanClickListener) {
        this.onPlanClickListener = onPlanClickListener;
    }

    public interface OnPlanClickListener {
        void onPlanClickListener(PlanDTO planDTO, int position);
    }

    public void notifyChangeDataElected(List<PlanDTO> mDtos) {
        planDTOList = new ArrayList<>();
        planDTOList = mDtos;
        List<PlanDTO> planElectedFilter = new ArrayList<>();
        for (PlanDTO planDTO : mDtos) {
            if (planDTO.getIdStatus().equals(PlanStatus.ELECTED)) {
                planElectedFilter.add(planDTO);
            }
        }
        planDTOList = planElectedFilter;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return planDTOList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}

