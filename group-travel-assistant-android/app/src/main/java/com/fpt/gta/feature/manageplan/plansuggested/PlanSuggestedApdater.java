package com.fpt.gta.feature.manageplan.plansuggested;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.fpt.gta.feature.manageplan.activitydetails.ActivityDetailActivity;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.ZonedDateTimeUtil;
import java.util.List;

public class PlanSuggestedApdater extends RecyclerView.Adapter<PlanSuggestedApdater.ViewHolder> {
    private Context mContext;
    List<ActivityDTO> activityDTOList;

    public PlanSuggestedApdater(Context mContext, List<ActivityDTO> activityDTOList) {
        this.mContext = mContext;
        this.activityDTOList = activityDTOList;
    }

    @NonNull
    @Override
    public PlanSuggestedApdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_plan_suggested, parent, false);
        return new PlanSuggestedApdater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanSuggestedApdater.ViewHolder holder, int position) {

        holder.txtActivityStatName.setText(activityDTOList.get(position).getStartPlace().getName());
        holder.txtTimeActivityStart.setText(ZonedDateTimeUtil.convertDateToStringTime(activityDTOList.get(position).getStartAt()));

        if (activityDTOList.get(position).getStartPlace().getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, activityDTOList.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), holder.imgPlaceStartActivity);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgPlaceStartActivity);
        }

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

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int postion) {
                Intent intent = new Intent(mContext, CalculatedPlaceDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DTO, activityDTOList.get(position));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });



        Boolean isAdded = activityDTOList.get(position).getIsAdded();

        if(isAdded.equals(true)){
            holder.txtIsplanSuggested.setText("System Suggested");
        }

        Boolean isToFarS = activityDTOList.get(position).getIsTooFar();
        if(isToFarS.equals(true)){
            holder.txtIsplanSuggested.setText("Place too far");
            holder.txtIsplanSuggested.setBackgroundResource(R.color.colorYellow);
        }

        Boolean isToFarE = activityDTOList.get(position).getIsTooFar();
        if(isToFarE.equals(true)){
            holder.txtIsplanSuggested.setText("Place too far");
            holder.txtIsplanSuggested.setBackgroundResource(R.color.colorYellow);
        }

    }


    @Override
    public int getItemCount() {
        int count = (activityDTOList != null) ? activityDTOList.size() : 0;
        return count;
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtActivityStatName, txtActivityEndName, txtTimeActivityEnd, txtTimeActivityStart;
        LinearLayout lnlRowActivityOfDay;
        ImageView imgPlaceStartActivity;
        ImageView imgPlaceEndActivity;
        LinearLayout lnlImgPlaceEnd, lnlNamePlaceEnd;
        TextView txtIsplanSuggested;
        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnlRowActivityOfDay = (LinearLayout) itemView.findViewById(R.id.lnlRowActivityOfDay);
            txtActivityStatName = (TextView) itemView.findViewById(R.id.txtAcitivityStartPlaceName);
            txtActivityEndName = (TextView) itemView.findViewById(R.id.txtAcitivityEndPlaceName);
            txtTimeActivityStart = (TextView) itemView.findViewById(R.id.txtActivityTimeStart);
            txtTimeActivityEnd = (TextView) itemView.findViewById(R.id.txtTimeActivityEnd);
            imgPlaceStartActivity = (ImageView) itemView.findViewById(R.id.imgPlaceStartActivity);
            imgPlaceEndActivity = (ImageView) itemView.findViewById(R.id.imgPlaceEndActivity);
            lnlImgPlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlImgPlaceEnd);
            lnlNamePlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlNamePlaceEnd);
            txtIsplanSuggested = (TextView) itemView.findViewById(R.id.txtIsplanSuggested);
            lnlRowActivityOfDay.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClickListener(view, getAdapterPosition());
        }


    }


}

