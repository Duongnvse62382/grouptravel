package com.fpt.gta.feature.manageplan.plansuggested;

import android.content.Context;
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
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.util.ImageLoaderUtil;
import java.util.List;

public class PlanIsFalseAdapter extends RecyclerView.Adapter<PlanIsFalseAdapter.ViewHolder> {
    private Context mContext;
    List<ActivityDTO> activityDTOList;



    public PlanIsFalseAdapter(Context mContext, List<ActivityDTO> activityDTOList) {
        this.mContext = mContext;
        this.activityDTOList = activityDTOList;
    }

    @NonNull
    @Override
    public PlanIsFalseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_plan_is_false, parent, false);
        return new PlanIsFalseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanIsFalseAdapter.ViewHolder holder, int position) {

        holder.txtActivityStatName.setText(activityDTOList.get(position).getStartPlace().getName());

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
        } else {
            holder.lnlImgPlaceEnd.setVisibility(View.GONE);
            holder.lnlNamePlaceEnd.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        int count = (activityDTOList != null) ? activityDTOList.size() : 0;
        return count;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtActivityStatName, txtActivityEndName;
        LinearLayout lnlRowActivitIsPlanFalse;
        ImageView imgPlaceStartActivity;
        ImageView imgPlaceEndActivity;
        LinearLayout lnlImgPlaceEnd, lnlNamePlaceEnd;
        TextView txtIsplanSuggested;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnlRowActivitIsPlanFalse = (LinearLayout) itemView.findViewById(R.id.lnlRowActivitIsPlanFalse);
            txtActivityStatName = (TextView) itemView.findViewById(R.id.txtAcitivityStartPlaceName);
            txtActivityEndName = (TextView) itemView.findViewById(R.id.txtAcitivityEndPlaceName);
            imgPlaceStartActivity = (ImageView) itemView.findViewById(R.id.imgPlaceStartActivity);
            imgPlaceEndActivity = (ImageView) itemView.findViewById(R.id.imgPlaceEndActivity);
            lnlImgPlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlImgPlaceEnd);
            lnlNamePlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlNamePlaceEnd);
            txtIsplanSuggested = (TextView) itemView.findViewById(R.id.txtIsplanSuggested);
        }




    }


}


