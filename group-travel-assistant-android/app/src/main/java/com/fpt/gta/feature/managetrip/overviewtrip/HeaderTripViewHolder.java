package com.fpt.gta.feature.managetrip.overviewtrip;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

import de.hdodenhof.circleimageview.CircleImageView;

final class HeaderTripViewHolder extends RecyclerView.ViewHolder {

    final TextView tvTitle;
    final CircleImageView imgTitleTrip;

    public HeaderTripViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitleTrip);
        imgTitleTrip = itemView.findViewById(R.id.imgSectionTrip);
    }
}
