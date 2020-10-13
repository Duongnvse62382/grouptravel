package com.fpt.gta.feature.manageplan.activitytype;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

final class HeaderSuggestedPlaceViewHolder extends RecyclerView.ViewHolder {
    final TextView tvTitle;
    final ImageView imgTitleTrip;

    public HeaderSuggestedPlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitleSuggested);
        imgTitleTrip = itemView.findViewById(R.id.imgSectionSuggested);
    }
}
