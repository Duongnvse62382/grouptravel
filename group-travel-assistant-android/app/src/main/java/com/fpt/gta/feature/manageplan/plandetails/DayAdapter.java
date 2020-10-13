package com.fpt.gta.feature.manageplan.plandetails;

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
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.PlanStatus;
import com.fpt.gta.feature.manageplan.activitytype.ActivityAdapter;
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

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    Context mContext;
    List<DayPlanResponseDTO> dayPlanResponseDTOList;
    private PlanDTO planDTO;

    private DayAdapter.OnItemClickListener onItemClickListener;

    public DayAdapter(Context mContext, List<DayPlanResponseDTO> dayPlanResponseDTOList, PlanDTO planDTO) {
        this.mContext = mContext;
        this.dayPlanResponseDTOList = dayPlanResponseDTOList;
        this.planDTO = planDTO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adddayplan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

        if (currentDate.after(convertedDateEndUTC)) {
            holder.txtAddDayCount.setBackgroundResource(R.drawable.shaperecycleviewdaygrey);
        }


        holder.lnlAddDayPlan.setVisibility(View.GONE);
        try {
            Integer idAdmin = SharePreferenceUtils.getIntSharedPreference(mContext, GTABundle.ISADMIN);
            String idPerson = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String memberCreate = planDTO.getOwner().getPerson().getFirebaseUid();
            Integer groupStatus = SharePreferenceUtils.getIntSharedPreference(mContext, GTABundle.GROUPSTATUS);

            if (!groupStatus.equals(GroupStatus.PLANNING)) {
                holder.lnlAddDayPlan.setVisibility(View.GONE);
            } else {
                if (planDTO.getIdStatus().equals(PlanStatus.ELECTED)) {
                    holder.lnlAddDayPlan.setVisibility(View.GONE);
//                    if (idAdmin.equals(MemberRole.ADMIN)) {
//                        holder.lnlAddDayPlan.setVisibility(View.GONE);
//                        holder.lnlAddDayPlan.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                onItemClickListener.onItemClickListener(dayPlanResponseDTOList.get(position), position);
//                            }
//                        });
                } else {
                    if (memberCreate.equals(idPerson)) {
                        holder.lnlAddDayPlan.setVisibility(View.VISIBLE);
                        holder.lnlAddDayPlan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onItemClickListener.onItemClickListener(dayPlanResponseDTOList.get(position), position);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }


        List<ActivityDTO> activityDTOList = dayPlanResponseDTO.getActivityDTOList();
        Collections.sort(activityDTOList, new Comparator<ActivityDTO>() {
            @Override
            public int compare(ActivityDTO o1, ActivityDTO o2) {
                return o1.getStartUtcAt().compareTo(o2.getStartUtcAt());
            }
        });


        ActivityAdapter activityAdapter = new ActivityAdapter(mContext, activityDTOList, planDTO);
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
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(DayPlanResponseDTO dayPlanResponseDTO, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void dataNotifySetChange(List<DayPlanResponseDTO> dtos) {
        dayPlanResponseDTOList = new ArrayList<>();
        dayPlanResponseDTOList = dtos;
        notifyDataSetChanged();
    }
}
