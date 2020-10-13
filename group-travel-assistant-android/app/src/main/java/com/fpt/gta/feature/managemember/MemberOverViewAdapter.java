package com.fpt.gta.feature.managemember;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.presenter.DeActivityMemberInGroupPresenter;
import com.fpt.gta.view.DeActivityMemberInGroupView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberOverViewAdapter extends RecyclerView.Adapter<MemberOverViewAdapter.MyViewHolder> implements DeActivityMemberInGroupView {

    private List<MemberDTO> memberResponseDTOList;
    private Context mContent;
    private OnClickMemberDelete onClickMemberDelete;
    private DeActivityMemberInGroupPresenter deActivityMemberInGroupPresenter;
    private DeActivityMemberInGroupView mView;
    private int isAdmin;
    private Integer groupStatus;
    private boolean isCurrentlyAdmin = false;
    private String currentFirebaseUid = "";

    public MemberOverViewAdapter(List<MemberDTO> memberResponseDTOList, Context mContent, int isAdmin, Integer groupStatus) {
        this.memberResponseDTOList = memberResponseDTOList;
        this.mContent = mContent;
        this.isAdmin = isAdmin;
        this.groupStatus = groupStatus;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContent).inflate(R.layout.row_recycleview_list_view_member, parent, false);
        initAdapter();
        return new MyViewHolder(mView, this);

    }

    private void initAdapter() {
        currentFirebaseUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (MemberDTO dto : memberResponseDTOList) {
            if (dto.getIdRole() == MemberRole.ADMIN && currentFirebaseUid.equals(dto.getPerson().getFirebaseUid())) {
                isCurrentlyAdmin = true;
                break;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (memberResponseDTOList.get(position).getPerson().getPhotoUri() != null) {
            Glide.with(mContent).load(memberResponseDTOList.get(position).getPerson().getPhotoUri()).into(holder.imgListMember);
        } else {
            Glide.with(mContent).load(R.mipmap.member).into(holder.imgListMember);
        }
        holder.txtMemberName.setText(memberResponseDTOList.get(position).getPerson().getName());
        holder.txtMemberEmail.setText(memberResponseDTOList.get(position).getPerson().getEmail());
        holder.txtMemberPhone.setText(memberResponseDTOList.get(position).getPerson().getPhoneNumber());
        holder.lnlDeleteMember.setVisibility(View.GONE);

        boolean isAllowDelete =
                isCurrentlyAdmin
                        && !isGroupPending()
                        && !isGroupExecuting()
                        && !(currentFirebaseUid.equals(memberResponseDTOList.get(position).getPerson().getFirebaseUid()));
        if (isAllowDelete) {
            holder.lnlDeleteMember.setVisibility(View.VISIBLE);
        } else {
            holder.lnlDeleteMember.setVisibility(View.GONE);
        }

        holder.lnlDeleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickMemberDelete != null) {
                    onClickMemberDelete.onClickMemberDelete(memberResponseDTOList.get(position), position);
                }
            }
        });

    }


    public boolean isGroupExecuting() {
        return groupStatus.equals(GroupStatus.EXECUTING);
    }

    public boolean isGroupPending() {
        return groupStatus.equals(GroupStatus.PENDING);
    }

//    public void deleteMemberDetail(int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContent);
//        builder.setMessage("Are you sure to kick this member?");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                memberResponseDTOList.remove ( position );
//                notifyItemRemoved ( position );
//                notifyItemRangeChanged ( position, memberResponseDTOList.size ());
//                deActivityMemberInGroupPresenter = new DeActivityMemberInGroupPresenter(mContent, mView);
//                deActivityMemberInGroupPresenter.deActiveMemberInGroup(idGroup ,memberResponseDTOList.get(position).getId());
//                notifyChangeData(memberResponseDTOList);
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }


    @Override
    public int getItemCount() {
        return memberResponseDTOList.size();
    }

    public void notifyChangeData(List<MemberDTO> dtos) {
        memberResponseDTOList = new ArrayList<>();
        memberResponseDTOList = dtos;
        notifyDataSetChanged();
    }

    @Override
    public void deActivityMemberInGroupSuccess(String message) {

    }

    @Override
    public void deActivityMemberInGroupFail(String messageFail) {

    }

    public interface OnClickMemberDelete {
        void onClickMemberDelete(MemberDTO memberDTO, int position);
    }

    public void setOnClickMemberDelete(MemberOverViewAdapter.OnClickMemberDelete onClickMemberDelete) {
        this.onClickMemberDelete = onClickMemberDelete;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //        private MemberOverViewAdapter mAdaper;
//        public RelativeLayout viewBackground, viewForeground;
        private CircleImageView imgListMember;
        private TextView txtMemberName;
        private TextView txtMemberEmail;
        private TextView txtMemberPhone;
        private LinearLayout lnlDeleteMember;

        public MyViewHolder(@NonNull View itemView, MemberOverViewAdapter memberOverViewAdapter) {
            super(itemView);
//            mAdaper = memberOverViewAdapter;
            txtMemberName = (TextView) itemView.findViewById(R.id.txtMemberName);
            txtMemberName.setSelected(true);
            txtMemberEmail = (TextView) itemView.findViewById(R.id.txtMemberMail);
            txtMemberEmail.setSelected(true);
            txtMemberPhone = (TextView) itemView.findViewById(R.id.txtMemberPhone);
            imgListMember = (CircleImageView) itemView.findViewById(R.id.imgListMember);
            lnlDeleteMember = (LinearLayout) itemView.findViewById(R.id.lnlDeleteMember);
//            viewForeground = (RelativeLayout) itemView.findViewById ( R.id.view_foreground );
        }
    }
}
