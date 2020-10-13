package com.fpt.gta.feature.managetransaction.transactiontypeoverview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

import de.hdodenhof.circleimageview.CircleImageView;

final class HeaderViewHolder extends RecyclerView.ViewHolder {

    final TextView tvTitle;
    final CircleImageView imgSection;


    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        imgSection = itemView.findViewById(R.id.imgSection);
    }
}