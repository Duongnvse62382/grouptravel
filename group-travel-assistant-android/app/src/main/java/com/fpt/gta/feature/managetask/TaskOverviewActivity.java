package com.fpt.gta.feature.managetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.feature.managetodolist.addtodolist.AddTodoListActivity;
import com.fpt.gta.feature.managetodolist.updatetask.UpdateTaskActivity;
import com.fpt.gta.presenter.PrintAllTaskInGroupPresenter;
import com.fpt.gta.presenter.UpdateTaskPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintAllTaskInGroupView;
import com.fpt.gta.view.UpdateTaskview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class TaskOverviewActivity extends AppCompatActivity implements PrintAllTaskInGroupView, TaskSection.ClickListener, UpdateTaskview {
    private RecyclerView rcvTaskOVerView;
    private List<TripReponseDTO> tripReponseDTOList = new ArrayList<>();
    private List<TaskDTO> tasksDTOList;
    private TaskListApdater toDoListApdater;
    private PrintAllTaskInGroupPresenter mPrintAllTaskInGroupPresenter;
    private UpdateTaskPresenter mUpdateTaskPresenter;
    private Integer idGroup;
    private ImageView imgBackTask;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private List<TaskDTO> taskCity;
    private List<TaskDTO> taskJourney;

    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerTask;
    private ValueEventListener taskValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_overview);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskValueEventListener = listenerTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    reloadTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (taskValueEventListener != null) {
                listenerTask.removeEventListener(taskValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        rcvTaskOVerView = findViewById(R.id.rcvTaskOVerView);
        imgBackTask = findViewById(R.id.imgBackTask);
        rcvTaskOVerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerTask = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadTask");
    }

    public void initData() {
        Bundle bundle = getIntent().getExtras();
        tripReponseDTOList = (List<TripReponseDTO>) bundle.getSerializable(GTABundle.KEYTRIPLIST);
        imgBackTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void reloadTask() {
        mPrintAllTaskInGroupPresenter = new PrintAllTaskInGroupPresenter(this, this);
        mPrintAllTaskInGroupPresenter.printAllTaskInGroup(idGroup);
    }

    public void updateUI() {
        rcvTaskOVerView.setAdapter(sectionedAdapter);
        sectionedAdapter.notifyDataSetChanged();
    }

    @Override
    public void printAllTaskInGroupSuccess(List<TaskDTO> taskDTOList) {
        if (taskDTOList != null) {
            tasksDTOList = new ArrayList<>();
            tasksDTOList = taskDTOList;
            sectionedAdapter = new SectionedRecyclerViewAdapter();
            Collections.sort(taskDTOList, new Comparator<TaskDTO>() {
                @Override
                public int compare(TaskDTO o1, TaskDTO o2) {
                    return o1.getIdStatus().compareTo(o2.getIdStatus());
                }
            });

            taskCity = new ArrayList<>();
            taskJourney = new ArrayList<>();
            for (TaskDTO taskDTO : taskDTOList) {
                try {
                    if (taskDTO.getTrip() == null) {
                        taskJourney.add(taskDTO);
                    } else {
                        taskCity.add(taskDTO);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            sectionedAdapter.addSection(new TaskSection(taskJourney, this, "On Journey", null, this));

            for (TripReponseDTO tripReponseDTO : tripReponseDTOList) {
                List<TaskDTO> filterCity = new ArrayList<>();
                for (TaskDTO taskDTO : taskCity) {
                    if (taskDTO.getTrip().getId().equals(tripReponseDTO.getId())) {
                        filterCity.add(taskDTO);
                    }
                }
                sectionedAdapter.addSection(new TaskSection(filterCity, this, tripReponseDTO.getStartPlace().getName(), tripReponseDTO, this));
            }


            updateUI();
        }
    }

    @Override
    public void printAllTaskInGroupFail(String messageFail) {

    }

    @Override
    public void onItemRootViewClicked(@NonNull TaskSection section, int itemAdapterPosition, TaskDTO taskDTO) {
        Intent intent = new Intent(this, UpdateTaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYTASK, taskDTO);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemCheckClicked(@NonNull TaskSection section, int itemAdapterPosition, TaskDTO taskDTO) {
        taskDTO.setIdStatus(taskDTO.getIdStatus().equals(1) ? 0 : 1);
        mUpdateTaskPresenter = new UpdateTaskPresenter(this, TaskOverviewActivity.this);
        mUpdateTaskPresenter.updateTask(taskDTO);
    }

    @Override
    public void onClickAddTask(TaskSection section, TripReponseDTO tripReponseDTO) {
        Intent intent = new Intent(this, AddTodoListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.TRIPTASK, tripReponseDTO);
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDACTIVITY, -1);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void updateTaskSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerTask.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTaskFail(String masage) {
        DialogShowErrorMessage.showValidationDialog(this, masage);
    }
}