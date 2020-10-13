package com.fpt.gta.feature.managetrip.userjoin.plandetailfragment;

import android.content.Context;
import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;
import com.fpt.gta.ItemClickListener;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.feature.managetrip.userjoin.ActivityDetailViewUserActivity;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccomodationViewAdapter extends RecyclerView.Adapter<AccomodationViewAdapter.ViewHolder> {
    private Context mContext;
    private List<ActivityDTO> activityDTOList;
    private PlanDTO planDTO;
    private String dateGoTripUTC, dateEndTripUTC;


    public AccomodationViewAdapter(Context mContext, List<ActivityDTO> activityDTOList, PlanDTO planDTO) {
        this.mContext = mContext;
        this.activityDTOList = activityDTOList;
        this.planDTO = planDTO;
    }

    @NonNull
    @Override
    public AccomodationViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_accomodation_activity, parent, false);
        return new AccomodationViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccomodationViewAdapter.ViewHolder holder, int position) {
        Date dateStart = activityDTOList.get(position).getStartAt();
        Date dateEnd = activityDTOList.get(position).getEndAt();
        holder.txtNameActivityTAccomodation.setText(activityDTOList.get(position).getName());
        holder.txtAccomodationStatName.setText(activityDTOList.get(position).getStartPlace().getName());
        holder.txtAccomodationDayStart.setText(ZonedDateTimeUtil.convertDateTimeToString(dateStart));
        holder.txtDayAccomodationEnd.setText(ZonedDateTimeUtil.convertDateTimeToString(dateEnd));

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

        Boolean isTooFar = activityDTOList.get(position).getIsTooFar();
        if (isTooFar.equals(true)) {
            holder.txtIsTooFar.setVisibility(View.VISIBLE);
        } else {
            holder.txtIsTooFar.setVisibility(View.GONE);
        }


        if (activityDTOList.get(position).getStartPlace().getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, activityDTOList.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), holder.imgAccomodationPlace);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgAccomodationPlace);
        }

        holder.txtAccomodationDayStart.setBackgroundResource(R.color.colorWhite);
        holder.txtDayAccomodationEnd.setBackgroundResource(R.color.colorWhite);
        holder.txtAccomodationDayStart.setTextColor(Color.parseColor("#56a8a2"));
        holder.txtDayAccomodationEnd.setTextColor(Color.parseColor("#56a8a2"));

        int idActivity = activityDTOList.get(position).getId();
        Date date1 = activityDTOList.get(position).getStartUtcAt();
        Date date2 = activityDTOList.get(position).getEndUtcAt();

        dateGoTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPGOUTC);
        dateEndTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPENDUTC);
        Date dateStartTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTripUTC);
        Date dateEndTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTripUTC);


        if (date1.getTime() < dateStartTripUtc.getTime() || date2.getTime() > dateEndTripUtc.getTime()) {
            holder.txtAccomodationDayStart.setBackgroundResource(R.color.colorRed);
            holder.txtDayAccomodationEnd.setBackgroundResource(R.color.colorRed);
            holder.txtAccomodationDayStart.setText(ZonedDateTimeUtil.convertDateTimeToString(dateStart));
            holder.txtDayAccomodationEnd.setText(ZonedDateTimeUtil.convertDateTimeToString(dateEnd) + " out time" );
        } else {
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
                    holder.txtAccomodationDayStart.setBackgroundResource(R.color.colorRed);
                    holder.txtDayAccomodationEnd.setBackgroundResource(R.color.colorRed);
                    holder.txtAccomodationDayStart.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.txtDayAccomodationEnd.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                }
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
        TextView txtAccomodationStatName, txtTodolist, txtIsTooFar;
        TextView txtAccomodationDayStart, txtDayAccomodationEnd, txtNameActivityTAccomodation;
        ItemClickListener itemClickListener;
        RecyclerView recycleViewTaskInActivity;
        ImageView imgUp, imgDown;
        LinearLayout lnlEditAccomodation;
        ImageView imgEditAccomodation, imgAccomodationPlace;
        ImageView imgDeleteAccomodation;
        LinearLayout lnlPrintTodolist, lnlRowAccomodationOfDay;


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAccomodationStatName = (TextView) itemView.findViewById(R.id.txtAccomodationStartPlaceName);
            txtNameActivityTAccomodation = (TextView) itemView.findViewById(R.id.txtNameActivityAccomodation);
            txtAccomodationDayStart = (TextView) itemView.findViewById(R.id.txtAccomodationDayStart);
            txtDayAccomodationEnd = (TextView) itemView.findViewById(R.id.txtDayAccomodationEnd);
            txtTodolist = (TextView) itemView.findViewById(R.id.txtTodolist);
            recycleViewTaskInActivity = (RecyclerView) itemView.findViewById(R.id.rcvTaskInActivity);
            imgUp = (ImageView) itemView.findViewById(R.id.imgUp);
            imgDown = (ImageView) itemView.findViewById(R.id.imgDown);
            imgEditAccomodation = (ImageView) itemView.findViewById(R.id.imgEditAccomodation);
            imgDeleteAccomodation = (ImageView) itemView.findViewById(R.id.imgDeleteAccomodation);
            imgAccomodationPlace = (ImageView) itemView.findViewById(R.id.imgAccomodationPlace);
            lnlEditAccomodation = (LinearLayout) itemView.findViewById(R.id.lnlEditAccomodation);
            lnlRowAccomodationOfDay = (LinearLayout) itemView.findViewById(R.id.lnlRowAccomodationOfDay);
            lnlPrintTodolist = (LinearLayout) itemView.findViewById(R.id.lnlPrintTodolist);
            txtIsTooFar = (TextView) itemView.findViewById(R.id.txtIsTooFar);
            txtIsTooFar.setVisibility(View.GONE);
            txtAccomodationStatName.setSelected(true);
            txtNameActivityTAccomodation.setSelected(true);
            lnlPrintTodolist.setVisibility(View.GONE);
            lnlEditAccomodation.setVisibility(View.GONE);

            lnlRowAccomodationOfDay.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClickListener(view, getAdapterPosition());
        }


    }


}