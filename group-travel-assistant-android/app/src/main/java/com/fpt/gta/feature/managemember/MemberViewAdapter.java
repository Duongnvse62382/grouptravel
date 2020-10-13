package com.fpt.gta.feature.managemember;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberViewAdapter extends RecyclerView.Adapter<MemberViewAdapter.ViewHolder> {

    private List<MemberDTO> memberDTOList;
    private Context mContent;
    private OnClickIteamMember onClickmember;

    public MemberViewAdapter(List<MemberDTO> memberDTOList, Context mContent) {
        this.memberDTOList = memberDTOList;
        this.mContent = mContent;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContent).inflate(R.layout.row_recycleview_list_member, parent,false);
        ViewHolder viewHolder = new ViewHolder(mView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (memberDTOList.get(position).getPerson().getPhotoUri() !=null) {
            Glide.with(mContent).load(memberDTOList.get(position).getPerson().getPhotoUri()).into(holder.imgMemberProfile);
        }else {
            Glide.with(mContent).load(R.mipmap.member).into(holder.imgMemberProfile);

        }
        holder.lnlRowMemberItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickmember != null){
                    onClickmember.onClickIteam(memberDTOList, position);
                }
            }
        });
    }

    public void notifyChangeData(List<MemberDTO> dtos) {
        memberDTOList = new ArrayList<>();
        memberDTOList = dtos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return memberDTOList.size();
    }

    public interface OnClickIteamMember {
        void onClickIteam(List<MemberDTO> memberDTO, int position);
    }

    public void setOnClickmember(OnClickIteamMember onClickmember) {
        this.onClickmember = onClickmember;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout lnlRowMemberItem;
        private CircleImageView imgMemberProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMemberProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            lnlRowMemberItem = (LinearLayout) itemView.findViewById(R.id.lnlRowMemberItem);
        }
    }
}
