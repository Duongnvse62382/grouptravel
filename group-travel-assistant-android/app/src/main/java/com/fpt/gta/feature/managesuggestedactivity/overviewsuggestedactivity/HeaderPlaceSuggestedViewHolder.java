package com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

final class HeaderPlaceSuggestedViewHolder extends RecyclerView.ViewHolder {
    final TextView tvTitle;
    final ImageView imgTitleTrip;

    public HeaderPlaceSuggestedViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitlePlaceSuggested);
        imgTitleTrip = itemView.findViewById(R.id.imgSectionPlaceSuggested);
    }
}