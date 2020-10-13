package com.fpt.gta.feature.managetask;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

public final class TaskViewHolder extends RecyclerView.ViewHolder {
    public RecyclerView recycleViewTaskMember;
    public TextView txtNameWork;
    public LinearLayout lnlRowToDoList;
    public CheckBox cbMemberDone;
    public TextView txtInPlan;
    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        recycleViewTaskMember = (RecyclerView) itemView.findViewById(R.id.recycleViewTaskMember);
        txtNameWork = itemView.findViewById(R.id.txtNameWork);
        txtInPlan = itemView.findViewById(R.id.txtInPlan);
        lnlRowToDoList = itemView.findViewById(R.id.lnlRowToDoList);
        cbMemberDone = itemView.findViewById(R.id.cbMemberDone);
    }
}
