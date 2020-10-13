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

public class GroupOnGoingAdater extends RecyclerView.Adapter<GroupOnGoingAdater.ViewHolder> {
    private List<GroupResponseDTO> listGroup;
    private Context mContext;
    private GroupOnGoingAdater.OnItemEditClickListener itemEditClickListener;
    private GroupOnGoingAdater.OnItemGroupClickListener onItemGroupClickListener;
    private int i;
    private List<Date> mListDate;

    public GroupOnGoingAdater(List<GroupResponseDTO> listGroup, Context mContext) {
        this.listGroup = listGroup;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupOnGoingAdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_recycleview_list_group, parent, false);
        GroupOnGoingAdater.ViewHolder viewHolder = new GroupOnGoingAdater.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupOnGoingAdater.ViewHolder holder, int position) {
        GroupResponseDTO groupResponseDTO = listGroup.get(position);
        holder.txtGroupName.setText("");
        holder.txtCurrencyGroupOverView.setText(listGroup.get(position).getCurrency().getCode());
        holder.txtGroupDatePicker.setText("");
        holder.txtGroupDateComeBack.setText("");
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
            if (mListDate.size() == 1 && listGroup.get(position).getMemberList().size() == 1) {
                holder.txtPointStart.setText(mListDate.size() + " day start at " + listGroup.get(position).getStartPlace().getName() + " for " + listGroup.get(position).getMemberList().size() + " person");

            } else if (mListDate.size() == 1) {
                holder.txtPointStart.setText(mListDate.size() + " day start at " + listGroup.get(position).getStartPlace().getName() + " for " + listGroup.get(position).getMemberList().size() + " persons");
            } else if (listGroup.get(position).getMemberList().size() == 1) {
                holder.txtPointStart.setText(mListDate.size() + " days start at " + listGroup.get(position).getStartPlace().getName() + " for " + listGroup.get(position).getMemberList().size() + " person");
            } else {
                holder.txtPointStart.setText(mListDate.size() + " days start at " + listGroup.get(position).getStartPlace().getName() + " for " + listGroup.get(position).getMemberList().size() + " persons");

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

    public void notifyDataChangeGoing(List<GroupResponseDTO> dtos) {
        listGroup = new ArrayList<>();
        listGroup = dtos;
        List<GroupResponseDTO> groupGoing = new ArrayList<>();
        Date currentDateGoing = new Date(Instant.now().toEpochMilli());
        TimeZone tz = TimeZone.getDefault();
        for (GroupResponseDTO groupResponseDTO : listGroup) {
            Date dateStart = groupResponseDTO.getStartUtcAt();
            Date dateEnd = groupResponseDTO.getEndUtcAt();
            Date dateStartGoing = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateStart, tz.getID());
            Date dateEndGoing = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEnd, tz.getID());
            if((currentDateGoing.getTime() > dateStartGoing.getTime() && currentDateGoing.getTime() < dateEndGoing.getTime())){
                groupGoing.add(groupResponseDTO);
            }
        }
        listGroup = groupGoing;
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
        private TextView txtNameAdmin;
        private TextView txtTypeMember, txtCurrencyGroupOverView;

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

    public void setOnEditItemClickListener(GroupOnGoingAdater.OnItemEditClickListener listener) {
        this.itemEditClickListener = listener;
    }

    public interface OnItemEditClickListener {
        void onItemEditClickListener(GroupResponseDTO groupResponseDTO, int position);
    }

    public void setOnItemGroupClickListener(GroupOnGoingAdater.OnItemGroupClickListener onItemGroupClickListener) {
        this.onItemGroupClickListener = onItemGroupClickListener;
    }

    public interface OnItemGroupClickListener {
        void onItemGroupClickListener(GroupResponseDTO groupResponseDTO, int position);
    }

}
