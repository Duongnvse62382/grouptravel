package com.fpt.gta.feature.managegroup.groupschedule;

import android.content.Context;
import android.util.Log;
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
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupGoneAdapter extends RecyclerView.Adapter<GroupGoneAdapter.ViewHolder> {
    private List<GroupResponseDTO> listGroup;
    private Context mContext;
    private GroupGoneAdapter.OnItemEditClickListener itemEditClickListener;
    private GroupGoneAdapter.OnItemGroupClickListener onItemGroupClickListener;
    private int i;
    private List<Date> mListDate;

    public GroupGoneAdapter(List<GroupResponseDTO> listGroup, Context mContext) {
        this.listGroup = listGroup;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupGoneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_recycleview_list_group, parent, false);
        GroupGoneAdapter.ViewHolder viewHolder = new GroupGoneAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupGoneAdapter.ViewHolder holder, int position) {
        GroupResponseDTO groupResponseDTO = listGroup.get(position);
        holder.txtGroupName.setText("");
        holder.txtGroupDatePicker.setText("");
        holder.txtGroupDateComeBack.setText("");
        holder.txtCurrencyGroupOverView.setText(listGroup.get(position).getCurrency().getCode());
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.lnlEditGroup.setVisibility(View.GONE);
        for (i = 0; i <= listGroup.get(position).getMemberList().size(); i++) {
            try {
                int roleMember = groupResponseDTO.getMemberList().get(i).getIdRole();
                String member = groupResponseDTO.getMemberList().get(i).getPerson().getFirebaseUid();
                if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                    holder.lnlEditGroup.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.d("GroupManagerAdapter", "Role: " + e.getMessage());
            }
        }

        for (i = 0; i <= listGroup.get(position).getMemberList().size(); i++) {
            try {
                int roleMember = groupResponseDTO.getMemberList().get(i).getIdRole();
                if (roleMember == MemberRole.ADMIN) {
                    holder.txtNameAdmin.setText(groupResponseDTO.getMemberList().get(i).getPerson().getName());
                    Glide.with(mContext).load(groupResponseDTO.getMemberList().get(i).getPerson().getPhotoUri()).into(holder.imgProfileAdmin);
                    break;
                }
            } catch (Exception e) {
                Log.d("GroupManagerAdapter", "Role: " + e.getMessage());
            }
        }


        holder.txtGroupName.setText(listGroup.get(position).getName());
        if (groupResponseDTO.getStartPlace() != null) {
            holder.txtPointStart.setText(listGroup.get(position).getStartPlace().getName());
        }

        String dateGo = ZonedDateTimeUtil.convertDateToStringASIA((listGroup.get(position).getStartAt()));
        String dateEnd = ZonedDateTimeUtil.convertDateToStringASIA((listGroup.get(position).getEndAt()));
        if (groupResponseDTO.getStartAt() != null) {
            try {
                holder.txtGroupDatePicker.setText(dateGo);

            } catch (Exception e) {
                holder.txtGroupDatePicker.setText(listGroup.get(position).getStartAt().toString());
            }
        }
        if (groupResponseDTO.getStartAt() != null) {
            try {
                holder.txtGroupDateComeBack.setText(dateEnd);

            } catch (Exception e) {
                holder.txtGroupDateComeBack.setText(listGroup.get(position).getStartAt().toString());
            }
        }


        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            mListDate = getDatesBetween(myFormat.parse(dateGo), myFormat.parse(dateEnd));
            int activeMemberCount = 0;
            for (GroupResponseDTO.MemberDTO memberDTO : listGroup.get(position).getMemberList()) {
                activeMemberCount = activeMemberCount + 1;
            }

            if (mListDate.size() == 1 && listGroup.get(position).getMemberList().size() == 1) {
                holder.txtPointStart.setText(mListDate.size() + " day start at " + listGroup.get(position).getStartPlace().getName() + " for " + activeMemberCount + " person");

            } else if (mListDate.size() == 1) {
                holder.txtPointStart.setText(mListDate.size() + " day start at " + listGroup.get(position).getStartPlace().getName() + " for " + activeMemberCount + " persons");
            } else if (listGroup.get(position).getMemberList().size() == 1) {
                holder.txtPointStart.setText(mListDate.size() + " days start at " + listGroup.get(position).getStartPlace().getName() + " for " + activeMemberCount + " person");
            } else {
                holder.txtPointStart.setText(mListDate.size() + " days start at " + listGroup.get(position).getStartPlace().getName() + " for " + activeMemberCount + " persons");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ImageLoaderUtil.loadImage(mContext, listGroup.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), holder.imgGroup);
        } catch (Exception e) {
            e.getMessage();
        }


        holder.lnlRecycleViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemGroupClickListener != null) {
                    onItemGroupClickListener.onItemGroupClickListener(listGroup.get(position), position);
                }
            }
        });
        holder.lnlEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemEditClickListener != null) {
                    itemEditClickListener.onItemEditClickListener(listGroup.get(position), position);
                }
            }
        });
    }

    public static List<Date> getDatesBetween(
            Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }


    public void notifyDataChangeGone(List<GroupResponseDTO> dtos) {
        listGroup = new ArrayList<>();
        listGroup = dtos;
        List<GroupResponseDTO> groupGoingGone = new ArrayList<>();
        Date currentDateGoing = new Date(Instant.now().toEpochMilli());
        TimeZone tz = TimeZone.getDefault();
        for (GroupResponseDTO groupResponseDTO : listGroup) {
            Date dateEnd = groupResponseDTO.getEndUtcAt();
            Date dateEndGoing = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEnd, tz.getID());
            if (currentDateGoing.after(dateEndGoing)) {
                groupGoingGone.add(groupResponseDTO);
            }
        }
        listGroup = groupGoingGone;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lnlRecycleViewGroup;
        private TextView txtGroupName;
        private TextView txtPointStart;
        private TextView txtGroupDatePicker;
        private TextView txtGroupDateComeBack;
        private LinearLayout lnlEditGroup;
        private ImageView imgGroup;
        private CircleImageView imgProfileAdmin;
        private TextView txtNameAdmin, txtCurrencyGroupOverView;
        private TextView txtTypeMember;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnlRecycleViewGroup = (LinearLayout) itemView.findViewById(R.id.lnlRowGroupItem);
            txtGroupName = (TextView) itemView.findViewById(R.id.txtGroupName);
            txtPointStart = (TextView) itemView.findViewById(R.id.txtPointStart);
            txtGroupDatePicker = (TextView) itemView.findViewById(R.id.txtGroupDatePicker);
            txtGroupDateComeBack = (TextView) itemView.findViewById(R.id.txtGroupDateComeBack);
            lnlEditGroup = (LinearLayout) itemView.findViewById(R.id.lnl_edit_group);
            imgGroup = (ImageView) itemView.findViewById(R.id.imgGroup);
            imgProfileAdmin = (CircleImageView) itemView.findViewById(R.id.imgProfileAdmin);
            txtNameAdmin = (TextView) itemView.findViewById(R.id.txtNameAdmin);
            txtTypeMember = (TextView) itemView.findViewById(R.id.txtTypeMember);
            txtCurrencyGroupOverView = (TextView) itemView.findViewById(R.id.txtCurrencyGroupOverView);
        }
    }

    public void setOnEditItemClickListener(GroupGoneAdapter.OnItemEditClickListener listener) {
        this.itemEditClickListener = listener;
    }

    public interface OnItemEditClickListener {
        void onItemEditClickListener(GroupResponseDTO groupResponseDTO, int position);
    }

    public void setOnItemGroupClickListener(GroupGoneAdapter.OnItemGroupClickListener onItemGroupClickListener) {
        this.onItemGroupClickListener = onItemGroupClickListener;
    }

    public interface OnItemGroupClickListener {
        void onItemGroupClickListener(GroupResponseDTO groupResponseDTO, int position);
    }

}

