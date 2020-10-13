package com.fpt.gta.feature.managetask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.TaskDTO;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAssignmentApdater extends RecyclerView.Adapter<MemberAssignmentApdater.ViewHolder> {

    private List<TaskDTO.TaskAssignmentDTO> taskAssignmentDTOS;
    private Context mContent;

    public MemberAssignmentApdater(List<TaskDTO.TaskAssignmentDTO> taskAssignmentDTOS, Context mContent) {
        this.taskAssignmentDTOS = taskAssignmentDTOS;
        this.mContent = mContent;
    }

    @NonNull
    @Override
    public MemberAssignmentApdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContent).inflate(R.layout.row_recycleview_list_assignment_member, parent,false);
        MemberAssignmentApdater.ViewHolder viewHolder = new MemberAssignmentApdater.ViewHolder(mView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAssignmentApdater.ViewHolder holder, int position) {
        Glide.with(mContent).load(taskAssignmentDTOS.get(position).getMember().getPerson().getPhotoUri()).into(holder.imgMemberProfile);
    }

    @Override
    public int getItemCount() {
        return taskAssignmentDTOS.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgMemberProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMemberProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
        }
    }
}
