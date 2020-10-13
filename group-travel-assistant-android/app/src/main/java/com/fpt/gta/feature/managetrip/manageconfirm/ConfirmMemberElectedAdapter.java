package com.fpt.gta.feature.managetrip.manageconfirm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmMemberElectedAdapter extends RecyclerView.Adapter<ConfirmMemberElectedAdapter.MyViewHolder> {

    private List<MemberDTO> memberResponseDTOList;
    private Context mContent;
    private OnClickConfirmElected mOnClickConfirmElected;
    private OnClickUnConfirmElected mOnClickUnConfirmElected;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId;


    public ConfirmMemberElectedAdapter(List<MemberDTO> memberResponseDTOList, Context mContent) {
        this.memberResponseDTOList = memberResponseDTOList;
        this.mContent = mContent;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContent).inflate(R.layout.row_recycleview_list_member_confirm, parent, false);
        return new MyViewHolder(mView);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();

        if (memberResponseDTOList.get(position).getPerson().getPhotoUri() != null) {
            Glide.with(mContent).load(memberResponseDTOList.get(position).getPerson().getPhotoUri()).into(holder.imgListMember);
        } else {
            Glide.with(mContent).load(R.mipmap.member).into(holder.imgListMember);
        }

        holder.txtMemberName.setText(memberResponseDTOList.get(position).getPerson().getName());
        if (memberResponseDTOList.get(position).getIsReady() == true) {
            holder.imgReady.setVisibility(View.VISIBLE);
            holder.imgUnReady.setVisibility(View.GONE);
        } else {
            holder.imgReady.setVisibility(View.GONE);
            holder.imgUnReady.setVisibility(View.VISIBLE);
        }

        if (userId.equals(memberResponseDTOList.get(position).getPerson().getFirebaseUid())) {
            holder.txtMemberIsMe.setText("Me");
        } else {
            holder.txtMemberIsMe.setText("");
        }

        if (userId.equals(memberResponseDTOList.get(position).getPerson().getFirebaseUid())) {
            holder.imgReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickConfirmElected != null) {
                        mOnClickConfirmElected.onClickConfirmElected(memberResponseDTOList.get(position), position);
//                        holder.imgReady.setVisibility(View.GONE);
//                        holder.imgUnReady.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.imgUnReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickUnConfirmElected != null) {
                        mOnClickUnConfirmElected.onClickUnConfirmElected(memberResponseDTOList.get(position), position);
//                        holder.imgReady.setVisibility(View.VISIBLE);
//                        holder.imgUnReady.setVisibility(View.GONE);
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        int count = (memberResponseDTOList != null) ? memberResponseDTOList.size () : 0;
        return count;
    }

    public void notifyChangeData(List<MemberDTO> dtos) {
        memberResponseDTOList = new ArrayList<>();
        memberResponseDTOList = dtos;
        notifyDataSetChanged();
    }

    public interface OnClickUnConfirmElected {
        void onClickUnConfirmElected(MemberDTO memberDTO, int position);
    }

    public interface OnClickConfirmElected {
        void onClickConfirmElected(MemberDTO memberDTO, int position);
    }

    public void setmOnClickConfirmElected(OnClickConfirmElected mOnClickConfirmElected) {
        this.mOnClickConfirmElected = mOnClickConfirmElected;
    }

    public void setmOnClickUnConfirmElected(OnClickUnConfirmElected mOnClickUnConfirmElected) {
        this.mOnClickUnConfirmElected = mOnClickUnConfirmElected;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgListMember;
        private TextView txtMemberName;
        private TextView txtMemberIsMe;
        private TextView txtMemberPhone;
        private LinearLayout lnlReady;
        private ImageView imgReady, imgUnReady;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMemberName = (TextView) itemView.findViewById(R.id.txtConfirmMemberName);
            txtMemberName.setSelected(true);
            txtMemberIsMe = (TextView) itemView.findViewById(R.id.txtMemberIsMe);
//            txtMemberEmail.setSelected(true);
//            txtMemberPhone = (TextView) itemView.findViewById(R.id.txtMemberConfirmPhone);
            imgReady = (ImageView) itemView.findViewById(R.id.imgReady);
            imgUnReady = (ImageView) itemView.findViewById(R.id.imgUnReady);
            imgListMember = (CircleImageView) itemView.findViewById(R.id.imgListMemberConfirm);
            lnlReady = (LinearLayout) itemView.findViewById(R.id.lnlReadyToGo);
        }
    }
}

