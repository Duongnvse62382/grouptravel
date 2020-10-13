package com.fpt.gta.feature.managetrip.userjoin.plandetailfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.DayPlanResponseDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.PlanStatus;
import com.fpt.gta.feature.manageplan.activitytype.ActivityAdapter;
import com.fpt.gta.feature.manageplan.plandetails.DayAdapter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DayViewAdapter extends RecyclerView.Adapter<DayViewAdapter.ViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context mContext;
    List<DayPlanResponseDTO> dayPlanResponseDTOList;
    private PlanDTO planDTO;

    private DayAdapter.OnItemClickListener onItemClickListener;

    public DayViewAdapter(Context mContext, List<DayPlanResponseDTO> dayPlanResponseDTOList, PlanDTO planDTO) {
        this.mContext = mContext;
        this.dayPlanResponseDTOList = dayPlanResponseDTOList;
        this.planDTO = planDTO;
    }

    @NonNull
    @Override
    public DayViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adddayplan, parent, false);
        return new DayViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewAdapter.ViewHolder holder, int position) {
        DayPlanResponseDTO dayPlanResponseDTO = dayPlanResponseDTOList.get(position);
        String dayName = dayPlanResponseDTO.getDayName();
        Date dayPlan = dayPlanResponseDTOList.get(position).getDayStart();
        String dayEveryPlan = ZonedDateTimeUtil.convertDateToStringASIA(dayPlan);
        holder.txtAddDayCount.setText(dayName + " | " + dayEveryPlan);

        Date currentDate = new Date(Instant.now().toEpochMilli());

        Date currentDateGoing = ZonedDateTimeUtil.convertStringToDateOrTime(ZonedDateTimeUtil.convertDateToStringASIA(currentDate));
        Date datePlan = ZonedDateTimeUtil.convertStringToDateOrTime(ZonedDateTimeUtil.convertDateToStringASIA(dayPlan));
//        if(currentDateGoing.after(datePlan)){
//            holder.txtAddDayCount.setBackgroundResource(R.drawable.shaperecycleviewdaygrey);
//        }



        TimeZone tz = TimeZone.getDefault();

        String satrtTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPGOUTC);
        String endTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPENDUTC);
        Date dateEndTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(endTripUTC);
        Date convertedDateEndUTC = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndTripUtc, tz.getID());

        if(currentDate.after(convertedDateEndUTC)){
            holder.txtAddDayCount.setBackgroundResource(R.drawable.shaperecycleviewdaygrey);
        }


        List<ActivityDTO> activityDTOList = dayPlanResponseDTO.getActivityDTOList();
        Collections.sort(activityDTOList, new Comparator<ActivityDTO>() {
            @Override
            public int compare(ActivityDTO o1, ActivityDTO o2) {
                return o1.getStartUtcAt().compareTo(o2.getStartUtcAt());
            }
        });


        ActivityViewAdapter activityAdapter = new ActivityViewAdapter(mContext, activityDTOList, planDTO);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerViewAddActivity.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(dayPlanResponseDTO.getActivityDTOList().size());
        holder.recyclerViewAddActivity.setLayoutManager(layoutManager);
        holder.recyclerViewAddActivity.setAdapter(activityAdapter);
        holder.recyclerViewAddActivity.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        int count = (dayPlanResponseDTOList != null) ? dayPlanResponseDTOList.size() : 0;
        return count;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewAddActivity;
        TextView txtAddDayCount;
        LinearLayout lnlAddDayPlan;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAddDayCount = (TextView) itemView.findViewById(R.id.txtAddDayCount);
            recyclerViewAddActivity = (RecyclerView) itemView.findViewById(R.id.rcvAddActivity);
            lnlAddDayPlan = (LinearLayout) itemView.findViewById(R.id.lnlAddDayPlan);
            lnlAddDayPlan.setVisibility(View.GONE);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(DayPlanResponseDTO dayPlanResponseDTO, int position);
    }

    public void setOnItemClickListener(DayAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void dataNotifySetChange(List<DayPlanResponseDTO> dtos) {
        dayPlanResponseDTOList = new ArrayList<>();
        dayPlanResponseDTOList = dtos;
        notifyDataSetChanged();
    }

}
