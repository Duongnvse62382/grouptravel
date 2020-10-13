package com.fpt.gta.feature.managetrip.userjoin.plandetailfragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.PlanStatus;
import com.fpt.gta.feature.manageplan.activitydetails.ActivityDetailActivity;
import com.fpt.gta.feature.managetrip.userjoin.ActivityDetailViewUserActivity;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityViewAdapter extends RecyclerView.Adapter<ActivityViewAdapter.ViewHolder>  {

    private Context mContext;
    private List<ActivityDTO> activityDTOList;
    private PlanDTO planDTO;
    private String dateGoTripUTC, dateEndTripUTC;

    public ActivityViewAdapter(Context mContext, List<ActivityDTO> activityDTOList, PlanDTO planDTO) {
        this.mContext = mContext;
        this.activityDTOList = activityDTOList;
        this.planDTO = planDTO;
    }

    @NonNull
    @Override
    public ActivityViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewAdapter.ViewHolder holder, int position) {

        if (activityDTOList.get(position).getStartPlace().getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, activityDTOList.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), holder.imgPlaceStartActivity);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgPlaceStartActivity);
        }

        holder.txtActivityStatName.setText(activityDTOList.get(position).getStartPlace().getName());
        holder.txtTimeActivityStart.setText(ZonedDateTimeUtil.convertDateToStringTime(activityDTOList.get(position).getStartAt()));


        if (!activityDTOList.get(position).getStartPlace().getGooglePlaceId().equals(activityDTOList.get(position).getEndPlace().getGooglePlaceId())) {
            holder.lnlImgPlaceEnd.setVisibility(View.VISIBLE);
            holder.lnlNamePlaceEnd.setVisibility(View.VISIBLE);

            if (activityDTOList.get(position).getEndPlace().getPlaceImageList().size() != 0) {
                ImageLoaderUtil.loadImage(mContext, activityDTOList.get(position).getEndPlace().getPlaceImageList().get(0).getUri(), holder.imgPlaceEndActivity);
            } else {
                Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgPlaceEndActivity);
            }

            holder.txtActivityEndName.setText(activityDTOList.get(position).getEndPlace().getName());
            holder.txtTimeActivityEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(activityDTOList.get(position).getEndAt()));
        } else {
            holder.lnlImgPlaceEnd.setVisibility(View.GONE);
            holder.lnlNamePlaceEnd.setVisibility(View.GONE);
        }


        Boolean isTooFar = activityDTOList.get(position).getIsTooFar();
        if (isTooFar.equals(true)) {
            holder.txtIsTooFar.setVisibility(View.VISIBLE);
        } else {
            holder.txtIsTooFar.setVisibility(View.GONE);
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


        int idActivity = activityDTOList.get(position).getId();
        Date date1 = activityDTOList.get(position).getStartUtcAt();
        Date date2 = activityDTOList.get(position).getEndUtcAt();



        dateGoTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPGOUTC);
        dateEndTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPENDUTC);
        Date dateStartTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTripUTC);
        Date dateEndTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTripUTC);


        if (date1.getTime() < dateStartTripUtc.getTime() || date2.getTime() > dateEndTripUtc.getTime()) {
            holder.txtTimeActivityStart.setBackgroundResource(R.color.colorRed);
            holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorRed);
            holder.txtTimeActivityStart.setText("Out Time");
            holder.txtTimeActivityEnd.setText("Out Time");
        } else {
            for (ActivityDTO activityDTO : activityDTOList) {
                if (activityDTO.getId().equals(idActivity)) {
                    continue;
                }
                Date stratActivity = activityDTO.getStartUtcAt();
                Date endActivity = activityDTO.getEndUtcAt();
                if (date1.getTime() < stratActivity.getTime()
                        && date2.getTime() < stratActivity.getTime()) {
                    holder.txtTimeActivityStart.setBackgroundResource(R.color.colorBackGround);
                    holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorBackGround);
                } else if (date1.getTime() > endActivity.getTime()
                        && date2.getTime() > endActivity.getTime()) {
                    holder.txtTimeActivityStart.setBackgroundResource(R.color.colorBackGround);
                    holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorBackGround);
                } else {
                    holder.txtTimeActivityStart.setBackgroundResource(R.color.colorRed);
                    holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorRed);
                    break;
                }
            }
        }

    }

    public void notifyDataSetChangeActivity(List<ActivityDTO> dtos){
        activityDTOList = new ArrayList<>();
        for (ActivityDTO activityDTO : activityDTOList) {
            if(!activityDTO.getIdType().equals(ActivityType.TRANSPORTATION)){
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
        TextView txtActivityStatName, txtActivityEndName, txtTimeActivityEnd, txtTimeActivityStart, txtTodolist, txtIsTooFar;
        LinearLayout lnlRowActivityOfDay;
        ItemClickListener itemClickListener;
        RecyclerView recycleViewTaskInActivity;
        ImageView imgUp, imgDown;
        ImageView imgPlaceStartActivity;
        ImageView imgPlaceEndActivity;
        LinearLayout lnlImgPlaceEnd, lnlNamePlaceEnd, lnlEditActivity;
        ImageView imgEditActivity;
        ImageView imgDeleteActivity;
        LinearLayout lnlPrintTodolist;
        ImageView imgEditDocumentActivity;


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnlRowActivityOfDay = (LinearLayout) itemView.findViewById(R.id.lnlRowActivityOfDay);
            txtActivityStatName = (TextView) itemView.findViewById(R.id.txtAcitivityStartPlaceName);
            txtActivityEndName = (TextView) itemView.findViewById(R.id.txtAcitivityEndPlaceName);
            txtTodolist = (TextView) itemView.findViewById(R.id.txtTodolist);
            txtTimeActivityStart = (TextView) itemView.findViewById(R.id.txtActivityTimeStart);
            txtTimeActivityEnd = (TextView) itemView.findViewById(R.id.txtTimeActivityEnd);
            lnlRowActivityOfDay.setOnClickListener(this);
            recycleViewTaskInActivity = (RecyclerView) itemView.findViewById(R.id.rcvTaskInActivity);
            imgUp = (ImageView) itemView.findViewById(R.id.imgUp);
            imgDown = (ImageView) itemView.findViewById(R.id.imgDown);
            imgPlaceStartActivity = (ImageView) itemView.findViewById(R.id.imgPlaceStartActivity);
            imgPlaceEndActivity = (ImageView) itemView.findViewById(R.id.imgPlaceEndActivity);
            lnlImgPlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlImgPlaceEnd);
            lnlNamePlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlNamePlaceEnd);
            imgEditActivity = (ImageView) itemView.findViewById(R.id.imgEditActivity);
            imgDeleteActivity = (ImageView) itemView.findViewById(R.id.imgDeleteActivity);
            lnlEditActivity = (LinearLayout) itemView.findViewById(R.id.lnlEditActivity);
            lnlPrintTodolist = (LinearLayout) itemView.findViewById(R.id.lnlPrintTodolist);
            imgEditDocumentActivity = (ImageView) itemView.findViewById(R.id.imgEditDocumentActivity);
            txtIsTooFar = (TextView) itemView.findViewById(R.id.txtIsTooFar);
            txtIsTooFar.setVisibility(View.GONE);
            imgEditDocumentActivity.setVisibility(View.GONE);
            lnlImgPlaceEnd.setVisibility(View.GONE);
            lnlNamePlaceEnd.setVisibility(View.GONE);
            lnlEditActivity.setVisibility(View.INVISIBLE);
            lnlPrintTodolist.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClickListener(view, getAdapterPosition());
        }


    }


}
