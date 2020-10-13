package com.fpt.gta.feature.manageplan.activitytype;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.fpt.gta.ItemClickListener;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.PlanStatus;
import com.fpt.gta.feature.manageplan.activitydetails.ActivityDetailActivity;
import com.fpt.gta.feature.manageplan.updateactivity.UpdateActivity;
import com.fpt.gta.feature.manageplan.updateactivity.UpdateActivityDocumentActivity;
import com.fpt.gta.feature.managetask.TaskListApdater;;
import com.fpt.gta.presenter.DeleteActivityPresenter;
import com.fpt.gta.presenter.UpdateTaskPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.DeleteActivityView;
import com.fpt.gta.view.UpdateTaskview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> implements UpdateTaskview, DeleteActivityView {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    private Context mContext;
    private List<ActivityDTO> activityDTOList;
    private PlanDTO planDTO;
    private List<TaskDTO> tasksDTOListOren = new ArrayList<>();
    private UpdateTaskPresenter mUpdateTaskPresenter;
    private DeleteActivityPresenter mDeleteActivityPresenter;
    private Integer idGroup;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;
    private DatabaseReference listenerTask;
    private String dateGoTripUTC, dateEndTripUTC;


    public ActivityAdapter(Context mContext, List<ActivityDTO> activityDTOList, PlanDTO planDTO) {
        this.mContext = mContext;
        this.activityDTOList = activityDTOList;
        this.planDTO = planDTO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        dateGoTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPGOUTC);
        dateEndTripUTC = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.DATETRIPENDUTC);

        idGroup = SharePreferenceUtils.getIntSharedPreference(mContext, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");
        listenerTask = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadTask");

        if (activityDTOList.get(position).getStartPlace().getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, activityDTOList.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), holder.imgPlaceStartActivity);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgPlaceStartActivity);
        }

        Boolean isTooFar = activityDTOList.get(position).getIsTooFar();
        if (isTooFar.equals(true)) {
            holder.txtIsTooFar.setVisibility(View.VISIBLE);
        } else {
            holder.txtIsTooFar.setVisibility(View.GONE);
        }

        holder.txtActivityStatName.setText(activityDTOList.get(position).getStartPlace().getName());

        holder.txtTimeActivityStart.setText(ZonedDateTimeUtil.convertDateToStringTime(activityDTOList.get(position).getStartAt()));


        if (!activityDTOList.get(position).getStartPlace().getGooglePlaceId().equals(activityDTOList.get(position).getEndPlace().getGooglePlaceId())) {
            holder.lnlImgPlaceEnd.setVisibility(View.VISIBLE);
            holder.lnlNamePlaceEnd.setVisibility(View.VISIBLE);

            if (activityDTOList.get(position).getEndPlace().getPlaceImageList().size() != 0) {
                ImageLoaderUtil.loadImage(mContext, activityDTOList.get(position).getEndPlace().getPlaceImageList().get(0).getUri(), holder.imgPlaceEndActivity);
            } else {
                Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgPlaceEndActivity);
            }
            holder.txtActivityEndName.setText(activityDTOList.get(position).getEndPlace().getName());
            holder.txtTimeActivityEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(activityDTOList.get(position).getEndAt()));
        } else {
            holder.lnlImgPlaceEnd.setVisibility(View.GONE);
            holder.lnlNamePlaceEnd.setVisibility(View.GONE);
        }


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int postion) {
                Intent intent = new Intent(mContext, ActivityDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DTO, activityDTOList.get(position));
                bundle.putSerializable(GTABundle.PLANOJECT, planDTO);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        Integer groupStatus = SharePreferenceUtils.getIntSharedPreference(mContext, GTABundle.GROUPSTATUS);
        Integer idAdmin = SharePreferenceUtils.getIntSharedPreference(mContext, GTABundle.ISADMIN);
        String idPerson = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String memberCreate = planDTO.getOwner().getPerson().getFirebaseUid();


        holder.lnlPrintTodolist.setVisibility(View.GONE);
        if (!groupStatus.equals(GroupStatus.PLANNING)) {
            holder.lnlEditActivity.setVisibility(View.GONE);
            if(groupStatus.equals(GroupStatus.EXECUTING)){
                holder.lnlPrintTodolist.setVisibility(View.VISIBLE);
                if (idAdmin.equals(MemberRole.ADMIN)) {
                    holder.imgEditDocumentActivity.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (planDTO.getIdStatus().equals(PlanStatus.ELECTED)) {
                if (idAdmin.equals(MemberRole.ADMIN)) {
                    holder.lnlEditActivity.setVisibility(View.GONE);
                }
            } else {
                if (memberCreate.equals(idPerson)) {
                    holder.lnlEditActivity.setVisibility(View.VISIBLE);
                }

            }
        }


        holder.imgDeleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure to delete this Activity?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDeleteActivityPresenter = new DeleteActivityPresenter(mContext, ActivityAdapter.this);
                        mDeleteActivityPresenter.deleteSuggestedActivity(activityDTOList.get(position).getId());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        holder.imgEditDocumentActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateActivityDocumentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DTO, activityDTOList.get(position));
                bundle.putSerializable(GTABundle.ACTIVITYLIST, (Serializable) activityDTOList);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        holder.imgEditActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DTO, activityDTOList.get(position));
                bundle.putSerializable(GTABundle.ACTIVITYLIST, (Serializable) activityDTOList);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });


        holder.imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.recycleViewTaskInActivity.setVisibility(View.GONE);
                holder.imgDown.setVisibility(View.VISIBLE);
                holder.imgUp.setVisibility(View.GONE);
            }
        });

        holder.imgDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.recycleViewTaskInActivity.setVisibility(View.VISIBLE);
                holder.imgDown.setVisibility(View.GONE);
                holder.imgUp.setVisibility(View.VISIBLE);
            }
        });

        tasksDTOListOren = activityDTOList.get(position).getTaskList();
        Collections.sort(tasksDTOListOren);
        holder.imgUp.setVisibility(View.VISIBLE);
        holder.recycleViewTaskInActivity.setVisibility(View.VISIBLE);
        if (tasksDTOListOren.size() == 0) {
            holder.recycleViewTaskInActivity.setVisibility(View.GONE);
            holder.imgDown.setVisibility(View.GONE);
            holder.imgUp.setVisibility(View.GONE);
            holder.txtTodolist.setVisibility(View.GONE);
        } else {
            holder.imgDown.setVisibility(View.GONE);
            holder.imgUp.setVisibility(View.VISIBLE);
            holder.txtTodolist.setVisibility(View.VISIBLE);
        }
        TaskListApdater taskListApdater = new TaskListApdater(mContext, tasksDTOListOren);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount((tasksDTOListOren != null) ? tasksDTOListOren.size() : 0);
        holder.recycleViewTaskInActivity.setLayoutManager(layoutManager);
        holder.recycleViewTaskInActivity.setAdapter(taskListApdater);
        taskListApdater.setOnCheckToDoClickListener(new TaskListApdater.OnCheckToDoClickListener() {
            @Override
            public void onCheckToDoClickListener(TaskDTO taskDTO, int position) {
                taskDTO.setIdStatus(taskDTO.getIdStatus().equals(1) ? 0 : 1);
                mUpdateTaskPresenter = new UpdateTaskPresenter(mContext, ActivityAdapter.this);
                mUpdateTaskPresenter.updateTask(taskDTO);
            }
        });
        holder.recycleViewTaskInActivity.setRecycledViewPool(viewPool);


        int idActivity = activityDTOList.get(position).getId();
        Date date1 = activityDTOList.get(position).getStartUtcAt();
        Date date2 = activityDTOList.get(position).getEndUtcAt();


        Date dateStartTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTripUTC);
        Date dateEndTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTripUTC);


        if (date1.getTime() < dateStartTripUtc.getTime() || date2.getTime() > dateEndTripUtc.getTime()) {
            holder.txtTimeActivityStart.setBackgroundResource(R.color.colorRed);
            holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorRed);
            holder.txtTimeActivityStart.setText("Out Time");
            holder.txtTimeActivityEnd.setText("Out Time");
        } else {
            for (ActivityDTO activityDTO : activityDTOList) {
                if (activityDTO.getId().equals(idActivity)) {
                    continue;
                }
                Date stratActivity = activityDTO.getStartUtcAt();
                Date endActivity = activityDTO.getEndUtcAt();
                if (date1.getTime() < stratActivity.getTime()
                        && date2.getTime() < stratActivity.getTime()) {
                    holder.txtTimeActivityStart.setBackgroundResource(R.color.colorBackGround);
                    holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorBackGround);
                } else if (date1.getTime() > endActivity.getTime()
                        && date2.getTime() > endActivity.getTime()) {
                    holder.txtTimeActivityStart.setBackgroundResource(R.color.colorBackGround);
                    holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorBackGround);
                } else {
                    holder.txtTimeActivityStart.setBackgroundResource(R.color.colorRed);
                    holder.txtTimeActivityEnd.setBackgroundResource(R.color.colorRed);
                    break;
                }
            }
        }

    }

    public void notifyDataSetChangeActivity(List<ActivityDTO> dtos) {
        activityDTOList = new ArrayList<>();
        for (ActivityDTO activityDTO : activityDTOList) {
            if (!activityDTO.getIdType().equals(ActivityType.TRANSPORTATION)) {
                activityDTOList.add(activityDTO);
            }
        }
        activityDTOList = dtos;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        int count = (activityDTOList != null) ? activityDTOList.size() : 0;
        return count;
    }


    @Override
    public void updateTaskSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerTask.setValue(change);
            listenerActivity.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTaskFail(String masage) {

    }

    @Override
    public void deleteActivitySuccess(String message) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerActivity.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteActivityFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(mContext, messageFail);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtActivityStatName, txtActivityEndName, txtTimeActivityEnd, txtTimeActivityStart, txtTodolist, txtIsTooFar;
        LinearLayout lnlRowActivityOfDay;
        ItemClickListener itemClickListener;
        RecyclerView recycleViewTaskInActivity;
        ImageView imgUp, imgDown;
        ImageView imgPlaceStartActivity;
        ImageView imgPlaceEndActivity;
        LinearLayout lnlImgPlaceEnd, lnlNamePlaceEnd, lnlEditActivity;
        ImageView imgEditActivity;
        ImageView imgDeleteActivity;
        LinearLayout lnlPrintTodolist;
        ImageView imgEditDocumentActivity;


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnlRowActivityOfDay = (LinearLayout) itemView.findViewById(R.id.lnlRowActivityOfDay);
            txtActivityStatName = (TextView) itemView.findViewById(R.id.txtAcitivityStartPlaceName);
            txtActivityEndName = (TextView) itemView.findViewById(R.id.txtAcitivityEndPlaceName);
            txtTodolist = (TextView) itemView.findViewById(R.id.txtTodolist);
            txtTimeActivityStart = (TextView) itemView.findViewById(R.id.txtActivityTimeStart);
            txtTimeActivityEnd = (TextView) itemView.findViewById(R.id.txtTimeActivityEnd);
            lnlRowActivityOfDay.setOnClickListener(this);
            recycleViewTaskInActivity = (RecyclerView) itemView.findViewById(R.id.rcvTaskInActivity);
            imgUp = (ImageView) itemView.findViewById(R.id.imgUp);
            imgDown = (ImageView) itemView.findViewById(R.id.imgDown);
            imgPlaceStartActivity = (ImageView) itemView.findViewById(R.id.imgPlaceStartActivity);
            imgPlaceEndActivity = (ImageView) itemView.findViewById(R.id.imgPlaceEndActivity);
            lnlImgPlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlImgPlaceEnd);
            lnlNamePlaceEnd = (LinearLayout) itemView.findViewById(R.id.lnlNamePlaceEnd);
            imgEditActivity = (ImageView) itemView.findViewById(R.id.imgEditActivity);
            imgDeleteActivity = (ImageView) itemView.findViewById(R.id.imgDeleteActivity);
            lnlEditActivity = (LinearLayout) itemView.findViewById(R.id.lnlEditActivity);
            lnlPrintTodolist = (LinearLayout) itemView.findViewById(R.id.lnlPrintTodolist);
            imgEditDocumentActivity = (ImageView) itemView.findViewById(R.id.imgEditDocumentActivity);
            txtIsTooFar = (TextView) itemView.findViewById(R.id.txtIsTooFar);
            imgEditDocumentActivity.setVisibility(View.GONE);
            lnlImgPlaceEnd.setVisibility(View.GONE);
            lnlNamePlaceEnd.setVisibility(View.GONE);
            txtIsTooFar.setVisibility(View.GONE);
            lnlEditActivity.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClickListener(view, getAdapterPosition());
        }


    }


}
