package com.fpt.gta.feature.managetask;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.data.dto.constant.TaskStatus;


import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public final class TaskSection extends Section {

    private List<TaskDTO> taskDTOList;
    private Context mContext;
    private final String title;
    private TripReponseDTO tripReponseDTO;
    private final ClickListener clickListener;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();



    public TaskSection(List<TaskDTO> taskDTOList, Context mContext, String title, TripReponseDTO tripReponseDTO, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.iteam_todolistjourney)
                .headerResourceId(R.layout.section_task_header)
                .build());
        this.taskDTOList = taskDTOList;
        this.mContext = mContext;
        this.title = title;
        this.tripReponseDTO = tripReponseDTO;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return taskDTOList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final TaskViewHolder itemHolder = (TaskViewHolder) holder;
        String nameWork = taskDTOList.get(position).getName();
        itemHolder.txtNameWork.setText(nameWork);
        itemHolder.lnlRowToDoList.setOnClickListener(v -> clickListener.onItemRootViewClicked(this, position, taskDTOList.get(position)));

        if(taskDTOList.get(position).getActivity() != null){
            itemHolder.txtInPlan.setText(taskDTOList.get(position).getActivity().getName());
        }

        Integer taskStatus = taskDTOList.get(position).getIdStatus();
        itemHolder.cbMemberDone.setChecked(taskDTOList.get(position).isSelected());
        if(taskStatus.equals(TaskStatus.DONE)){
            itemHolder.cbMemberDone.setChecked(true);
        }else {
            itemHolder.cbMemberDone.setChecked(false);
        }

        itemHolder.cbMemberDone.setOnClickListener(v -> clickListener.onItemCheckClicked(this, position, taskDTOList.get(position)));
        List<TaskDTO.TaskAssignmentDTO> taskAssignmentDTOList;
        taskAssignmentDTOList = taskDTOList.get(position).getTaskAssignmentList();
        MemberAssignmentApdater memberDialogAdapter = new MemberAssignmentApdater(taskAssignmentDTOList, mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(itemHolder.recycleViewTaskMember.getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setInitialPrefetchItemCount(taskAssignmentDTOList.size());
        itemHolder.recycleViewTaskMember.setLayoutManager(layoutManager);
        itemHolder.recycleViewTaskMember.setAdapter(memberDialogAdapter);
        itemHolder.recycleViewTaskMember.setRecycledViewPool(viewPool);
    }



    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderTaskViewHolder(view);
    }


    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderTaskViewHolder headerHolder = (HeaderTaskViewHolder) holder;
        headerHolder.tvTitle.setText(title);
        headerHolder.ciAddTask.setOnClickListener(v -> clickListener.onClickAddTask(this, tripReponseDTO));
    }

    interface ClickListener {
        void onItemRootViewClicked(@NonNull final TaskSection section, final int itemAdapterPosition, TaskDTO taskDTO);
        void onItemCheckClicked(@NonNull final TaskSection section, final int itemAdapterPosition, TaskDTO taskDTO);
        void onClickAddTask(final  TaskSection section,  TripReponseDTO tripReponseDTO);
    }

}


