package com.fpt.gta.feature.manageplan.plansuggested;

import android.content.Context;
import android.util.Log;
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
import com.fpt.gta.util.ZonedDateTimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PlanDaySuggestedAdapter extends RecyclerView.Adapter<PlanDaySuggestedAdapter.ViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    Context mContext;
    List<DayPlanResponseDTO> dayPlanResponseDTOList;

    public PlanDaySuggestedAdapter(Context mContext, List<DayPlanResponseDTO> dayPlanResponseDTOList) {
        this.mContext = mContext;
        this.dayPlanResponseDTOList = dayPlanResponseDTOList;
    }

    @NonNull
    @Override
    public PlanDaySuggestedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adddayplan, parent, false);
        return new PlanDaySuggestedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanDaySuggestedAdapter.ViewHolder holder, int position) {
        DayPlanResponseDTO dayPlanResponseDTO = dayPlanResponseDTOList.get(position);
        String dayName = dayPlanResponseDTO.getDayName();
        Date dayPlan = dayPlanResponseDTOList.get(position).getDayStart();
        String dayEveryPlan = ZonedDateTimeUtil.convertDateToStringASIA(dayPlan);
        holder.txtAddDayCount.setText(dayName + " | " + dayEveryPlan);

        List<ActivityDTO> activityDTOList = dayPlanResponseDTO.getActivityDTOList();
        Collections.sort(activityDTOList, new Comparator<ActivityDTO>() {
            @Override
            public int compare(ActivityDTO o1, ActivityDTO o2) {
                return o1.getStartAt().compareTo(o2.getStartAt());
            }
        });

        PlanSuggestedApdater planSuggestedApdater = new PlanSuggestedApdater(mContext, activityDTOList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerViewPlanSuggested.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(dayPlanResponseDTO.getActivityDTOList().size());
        holder.recyclerViewPlanSuggested.setLayoutManager(layoutManager);
        holder.recyclerViewPlanSuggested.setAdapter(planSuggestedApdater);
        holder.recyclerViewPlanSuggested.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        int count = (dayPlanResponseDTOList != null) ? dayPlanResponseDTOList.size() : 0;
        return count;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewPlanSuggested;
        TextView txtAddDayCount;
        LinearLayout lnlAddDayPlan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAddDayCount = (TextView) itemView.findViewById(R.id.txtAddDayCount);
            recyclerViewPlanSuggested = (RecyclerView) itemView.findViewById(R.id.rcvAddActivity);
            lnlAddDayPlan = (LinearLayout) itemView.findViewById(R.id.lnlAddDayPlan);
            lnlAddDayPlan.setVisibility(View.GONE);
        }
    }


    public void dataNotifySetChange(List<DayPlanResponseDTO> dtos) {
        dayPlanResponseDTOList = new ArrayList<>();
        dayPlanResponseDTOList = dtos;
        notifyDataSetChanged();
    }
}

