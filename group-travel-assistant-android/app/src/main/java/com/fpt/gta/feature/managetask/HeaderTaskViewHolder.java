package com.fpt.gta.feature.managetask;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

import de.hdodenhof.circleimageview.CircleImageView;

public final class HeaderTaskViewHolder extends RecyclerView.ViewHolder {
    final TextView tvTitle;
    public CircleImageView ciAddTask;

    public HeaderTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitleTask);
        ciAddTask = itemView.findViewById(R.id.imgSectionTask);
    }
}
