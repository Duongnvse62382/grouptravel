package com.fpt.gta.feature.managetodolist.addtodolist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomeTodolistAdapter extends RecyclerView.Adapter<CustomeTodolistAdapter.ViewHolder> {
    private List<MemberDTO> memberDTOS;
    private OnItemWorkForClickListener onItemWorkForClickListener;
    private Context mContext;
    private String nameMember;


    public CustomeTodolistAdapter(List<MemberDTO> memberDTOS, Context mContext) {
        this.memberDTOS = memberDTOS;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CustomeTodolistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_work_for_who, parent, false);
        return new CustomeTodolistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomeTodolistAdapter.ViewHolder holder, int position) {
        nameMember = memberDTOS.get(position).getPerson().getName();
        holder.txtNameMemberTodoForWho.setText(nameMember);
        if (memberDTOS.get(position).isSelected()) {
            holder.cbMemberTodoForWho.setChecked(true);
        } else {
            holder.cbMemberTodoForWho.setChecked(false);
        }

        holder.cbMemberTodoForWho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemWorkForClickListener != null) {
                    onItemWorkForClickListener.onItemWorkForClickListener(memberDTOS.get(position), position);
                }
            }
        });
        Picasso.get().load(memberDTOS.get(position).getPerson().getPhotoUri()).into(holder.imgMemberTodoForWho);
    }


    @Override
    public int getItemCount() {
        if (memberDTOS != null) {
            return memberDTOS.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbMemberTodoForWho;
        private CircleImageView imgMemberTodoForWho;
        private TextView txtNameMemberTodoForWho;
        private LinearLayout lnlRowCheckBoxWorkfor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMemberTodoForWho = itemView.findViewById(R.id.imgMemberTodoForWho);
            lnlRowCheckBoxWorkfor = itemView.findViewById(R.id.lnlRowCheckBoxPaidBy);
            cbMemberTodoForWho = itemView.findViewById(R.id.cbMemberTodoForWho);
            txtNameMemberTodoForWho = itemView.findViewById(R.id.txtNameMemberTodoForWho);
        }
    }

    public void notifyChangeMember(List<MemberDTO> memberDTOList) {
        memberDTOS = new ArrayList<>();
        memberDTOS = memberDTOList;
        notifyDataSetChanged();
    }

    public interface OnItemWorkForClickListener {
        void onItemWorkForClickListener(MemberDTO memberDTO, int position);
    }

    public void setOnItemWorkForClickListener(CustomeTodolistAdapter.OnItemWorkForClickListener onItemWorkForClickListener) {
        this.onItemWorkForClickListener = onItemWorkForClickListener;
    }
}
