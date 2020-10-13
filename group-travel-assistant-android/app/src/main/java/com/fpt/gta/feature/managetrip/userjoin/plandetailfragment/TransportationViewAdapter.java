package com.fpt.gta.feature.managetrip.userjoin.plandetailfragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fpt.gta.ItemClickListener;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.PlanStatus;
import com.fpt.gta.feature.managetrip.userjoin.ActivityDetailViewUserActivity;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransportationViewAdapter extends RecyclerView.Adapter<TransportationViewAdapter.ViewHolder> {
    private Context mContext;
    private List<ActivityDTO> activityDTOList;
    private PlanDTO planDTO;

    public TransportationViewAdapter(Context mContext, List<ActivityDTO> activityDTOList, PlanDTO planDTO) {
        this.mContext = mContext;
        this.activityDTOList = activityDTOList;
        this.planDTO = planDTO;
    }

    @NonNull
    @Override
    public TransportationViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transpotation_activity, parent, false);
        return new TransportationViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransportationViewAdapter.ViewHolder holder, int position) {
        Date dateStartUTC = activityDTOList.get(position).getStartUtcAt();
        Date dateEndUTC = activityDTOList.get(position).getEndUtcAt();
        long distanceTime = dateEndUTC.getTime() - dateStartUTC.getTime();
        long diffMinutes = distanceTime / (60 * 1000) % 60;
        long diffHours = distanceTime / (60 * 60 * 1000) % 24;
        if(diffMinutes != 0 ){
            holder.txtDistanceTime.setText(diffHours+ "h " + diffMinutes +"p " );
        }else {
            holder.txtDistanceTime.setText(diffHours+ "h ");
        }
        Date dateStart = activityDTOList.get(position).getStartAt();
        Date dateEnd = activityDTOList.get(position).getEndAt();
        holder.txtNameActivityTransportation.setText(activityDTOList.get(position).getName());
        holder.txtActivityStatName.setText(activityDTOList.get(position).getStartPlace().getName());
        holder.txtTimeActivityStart.setText(ZonedDateTimeUtil.convertDateToStringTime(dateStart));
        holder.txtActivityDayStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(dateStart));
        holder.txtActivityEndName.setText(activityDTOList.get(position).getEndPlace().getName());
        holder.txtTimeActivityEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(dateEnd));
        if(ZonedDateTimeUtil.convertStringToDateOrTime(ZonedDateTimeUtil.convertDateToStringASIA(dateStart)).getTime() == (ZonedDateTimeUtil.convertStringToDateOrTime(ZonedDateTimeUtil.convertDateToStringASIA(dateEnd))).getTime()){
            holder.txtDayActivityEnd.setVisibility(View.GONE);
            holder.lnlDayNext.setVisibility(View.GONE);
        }else {
            holder.txtDayActivityEnd.setVisibility(View.VISIBLE);
            holder.lnlDayNext.setVisibility(View.VISIBLE);
            holder.txtDayActivityEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(dateEnd));
        }


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int postion) {
                Intent intent = new Intent(mContext, ActivityDetailViewUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.ACTIVITYDTOVIEW, activityDTOList.get(position));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });



        holder.txtTimeActivityStart.setBackgroundResource(R.color.colorWhite);
        holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorWhite);
        holder.txtTimeActivityStart.setTextColor(Color.parseColor("#56a8a2"));
        holder.txtTimeActivityEnd.setTextColor(Color.parseColor("#56a8a2"));

        int idActivity = activityDTOList.get(position).getId();
        Date date1 = activityDTOList.get(position).getStartUtcAt();
        Date date2 = activityDTOList.get(position).getEndUtcAt();

        for (ActivityDTO activityDTO : activityDTOList) {
            if (activityDTO.getId().equals(idActivity)) {
                continue;
            }
            Date stratActivity = activityDTO.getStartUtcAt();
            Date endActivity = activityDTO.getEndUtcAt();
            if (date1.getTime() < stratActivity.getTime()
                    && date2.getTime() < stratActivity.getTime()) {

            } else if (date1.getTime() > endActivity.getTime()
                    && date2.getTime() > endActivity.getTime()) {
            } else {
                holder.txtTimeActivityStart.setBackgroundResource(R.color.colorRed);
                holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorRed);
                holder.txtTimeActivityStart.setTextColor(Color.parseColor("#FFFFFF"));
                holder.txtTimeActivityEnd.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            }
        }

    }

    public void notifyDataSetChangeActivity(List<ActivityDTO> dtos) {
        activityDTOList = new ArrayList<>();
        for (ActivityDTO activityDTO : activityDTOList) {
            if (activityDTO.getIdType().equals(ActivityType.TRANSPORTATION)) {
                activityDTOList.add(activityDTO);
            }
        }
        activityDTOList = dtos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int count = (activityDTOList != null) ? activityDTOList.size() : 0;
        return count;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtActivityStatName, txtActivityEndName, txtTimeActivityEnd, txtTimeActivityStart, txtTodolist;
        TextView txtActivityDayStart, txtDayActivityEnd, txtNameActivityTransportation, txtDistanceTime;
        LinearLayout lnlRowActivityOfDay;
        LinearLayout lnlDayNext;
        ItemClickListener itemClickListener;
        RecyclerView recycleViewTaskInActivity;
        ImageView imgUp, imgDown;
        LinearLayout lnlEditActivity;
        ImageView imgEditActivity;
        ImageView imgDeleteActivity;
        LinearLayout lnlPrintTodolist;


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnlRowActivityOfDay = (LinearLayout) itemView.findViewById(R.id.lnlRowActivityOfDay);
            txtActivityStatName = (TextView) itemView.findViewById(R.id.txtAcitivityStartPlaceName);
            txtActivityEndName = (TextView) itemView.findViewById(R.id.txtAcitivityEndPlaceName);
            txtNameActivityTransportation = (TextView) itemView.findViewById(R.id.txtNameActivityTransportation);
            txtActivityDayStart = (TextView) itemView.findViewById(R.id.txtActivityDayStart);
            txtDayActivityEnd = (TextView) itemView.findViewById(R.id.txtDayActivityEnd);
            txtTodolist = (TextView) itemView.findViewById(R.id.txtTodolist);
            txtTimeActivityStart = (TextView) itemView.findViewById(R.id.txtActivityTimeStart);
            txtTimeActivityEnd = (TextView) itemView.findViewById(R.id.txtTimeActivityEnd);
            txtDistanceTime = (TextView) itemView.findViewById(R.id.txtDistanceTime);
            lnlRowActivityOfDay.setOnClickListener(this);
            recycleViewTaskInActivity = (RecyclerView) itemView.findViewById(R.id.rcvTaskInActivity);
            imgUp = (ImageView) itemView.findViewById(R.id.imgUp);
            imgDown = (ImageView) itemView.findViewById(R.id.imgDown);
            imgEditActivity = (ImageView) itemView.findViewById(R.id.imgEditActivity);
            imgDeleteActivity = (ImageView) itemView.findViewById(R.id.imgDeleteActivity);
            lnlEditActivity = (LinearLayout) itemView.findViewById(R.id.lnlEditActivity);
            lnlDayNext = (LinearLayout) itemView.findViewById(R.id.lnlDayNext);
            lnlPrintTodolist = (LinearLayout) itemView.findViewById(R.id.lnlPrintTodolist);
            lnlEditActivity.setVisibility(View.INVISIBLE);
            txtActivityStatName.setSelected(true);
            txtActivityEndName.setSelected(true);
            txtNameActivityTransportation.setSelected(true);
            lnlPrintTodolist.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClickListener(view, getAdapterPosition());
        }


    }


}

