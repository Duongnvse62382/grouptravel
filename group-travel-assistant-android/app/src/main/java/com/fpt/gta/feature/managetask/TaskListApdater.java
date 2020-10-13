package com.fpt.gta.feature.managetask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.constant.TaskStatus;
import com.fpt.gta.feature.manageplan.activitydetails.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskListApdater extends RecyclerView.Adapter<TaskListApdater.ViewHolder> implements ItemTouchHelperAdapter {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private Context mContext;
    private List<TaskDTO> taskDTOList;
    private OnItemEditTaskClickListener onItemEditTaskClickListener;
    private OnCheckToDoClickListener onCheckToDoClickListener;
    private OnItemChangePositionTaskClickListener onItemChangePositionTaskClickListener;

    public TaskListApdater(Context mContext, List<TaskDTO> taskDTOList) {
        this.mContext = mContext;
        this.taskDTOList = taskDTOList;
    }

    @NonNull
    @Override
    public TaskListApdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todolist_work, parent, false);
        return new TaskListApdater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListApdater.ViewHolder holder, int position) {
        String nameWork = taskDTOList.get(position).getName();
        holder.txtNameWork.setText(nameWork);
        holder.lnlRowToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEditTaskClickListener != null) {
                    onItemEditTaskClickListener.onItemEditTaskClickListener(taskDTOList.get(position), position);
                }
            }
        });

        holder.lnlRowToDoList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemChangePositionTaskClickListener != null){
                    onItemChangePositionTaskClickListener.onItemChangePositionTaskClickListener(taskDTOList.get(position), position);
                }
                return true;
            }
        });


        Integer taskStatus = taskDTOList.get(position).getIdStatus();
        holder.cbMemberDone.setChecked(taskDTOList.get(position).isSelected());
        if(taskStatus.equals(TaskStatus.DONE)){
            holder.cbMemberDone.setChecked(true);
        }else {
            holder.cbMemberDone.setChecked(false);
        }


        holder.cbMemberDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCheckToDoClickListener!= null){
                    onCheckToDoClickListener.onCheckToDoClickListener(taskDTOList.get(position), position);
                }
            }
        });

        List<TaskDTO.TaskAssignmentDTO> taskAssignmentDTOList;
        taskAssignmentDTOList = taskDTOList.get(position).getTaskAssignmentList();
        MemberAssignmentApdater memberDialogAdapter = new MemberAssignmentApdater(taskAssignmentDTOList, mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recycleViewTaskMember.getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setInitialPrefetchItemCount(taskAssignmentDTOList.size());
        holder.recycleViewTaskMember.setLayoutManager(layoutManager);
        holder.recycleViewTaskMember.setAdapter(memberDialogAdapter);
        holder.recycleViewTaskMember.setRecycledViewPool(viewPool);


    }


    @Override
    public int getItemCount() {
        int count = (taskDTOList != null) ? taskDTOList.size() : 0;
        return count;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(taskDTOList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(taskDTOList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public void onItemDismiss(int position) {
        taskDTOList.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recycleViewTaskMember;
        TextView txtNameWork;
        ImageView imgDeleteTask;
        LinearLayout lnlRowToDoList;
        CheckBox cbMemberDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recycleViewTaskMember = (RecyclerView) itemView.findViewById(R.id.recycleViewTaskMember);
            txtNameWork = itemView.findViewById(R.id.txtNameWork);
            lnlRowToDoList = itemView.findViewById(R.id.lnlRowToDoList);
            cbMemberDone = itemView.findViewById(R.id.cbMemberDone);

        }
    }

    public void notifyDataChange(List<TaskDTO> dtoList) {
        taskDTOList = new ArrayList<>();
        taskDTOList = dtoList;
        notifyDataSetChanged();
    }


    public interface OnItemChangePositionTaskClickListener {
        void onItemChangePositionTaskClickListener(TaskDTO taskDTO, int position);
    }

    public void setOnItemChangePositionTaskClickListener(TaskListApdater.OnItemChangePositionTaskClickListener onItemChangePositionTaskClickListener) {
        this.onItemChangePositionTaskClickListener = onItemChangePositionTaskClickListener;
    }

    public interface OnItemEditTaskClickListener {
        void onItemEditTaskClickListener(TaskDTO taskDTO, int position);
    }

    public void setOnItemEditTaskClickListener(TaskListApdater.OnItemEditTaskClickListener onItemEditTaskClickListener) {
        this.onItemEditTaskClickListener = onItemEditTaskClickListener;
    }

    public interface OnCheckToDoClickListener{
        void onCheckToDoClickListener(TaskDTO taskDTO, int position);
    }

    public void setOnCheckToDoClickListener(TaskListApdater.OnCheckToDoClickListener onCheckToDoClickListener) {
        this.onCheckToDoClickListener = onCheckToDoClickListener;
    }
}
