package com.fpt.gta.feature.managetodolist.updatetask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.constant.MemberStatus;
import com.fpt.gta.feature.managetodolist.addtodolist.CustomeTodolistAdapter;
import com.fpt.gta.presenter.DeleteTaskPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.presenter.UpdateTaskPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.DeleteTaskView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.fpt.gta.view.UpdateTaskview;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UpdateTaskActivity extends AppCompatActivity implements PrintMemberInGroupView, UpdateTaskview, DeleteTaskView {
    private List<MemberDTO> membersDTOSList;
    private TaskDTO taskDTO;
    private EditText edtUpdateTaskName;
    private CheckBox chkForUpdateAllTodo;
    private ImageView imgUpdateTaskBack, imgDeleteTask;
    private Button btnUpdateTask;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private UpdateTaskPresenter mUpdateTaskPresenter;
    private DeleteTaskPresenter mDeleteTaskPresenter;
    private RecyclerView rcvCheckBoxUpdateMemberforto;
    private CustomeTodolistAdapter customeTodolistAdapter;
    private String nameTask;
    private Integer statusTask, idTask;
    private List<TaskDTO.TaskAssignmentDTO> assignmentDTOList;
    private MemberDTO memberTask = null;

    private Integer idGroup;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;
    private DatabaseReference listenerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        initView();
        initData();
    }

    public void initView() {
        chkForUpdateAllTodo = findViewById(R.id.chkForUpdateAllTodo);
        imgUpdateTaskBack = findViewById(R.id.imgUpdateTaskBack);
        edtUpdateTaskName = findViewById(R.id.edtUpdateTaskName);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        imgDeleteTask = findViewById(R.id.imgDeleteTask);
        rcvCheckBoxUpdateMemberforto = findViewById(R.id.rcvCheckBoxUpdateMemberforto);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvCheckBoxUpdateMemberforto.setLayoutManager(linearLayoutManager);
        Bundle bundle = getIntent().getExtras();
        taskDTO = (TaskDTO) bundle.getSerializable(GTABundle.KEYTASK);
        chkForUpdateAllTodo.setVisibility(View.GONE);
        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");
        listenerTask = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadTask");
    }


    public void initData() {
        int groupId = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(this, this);
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
        mDeleteTaskPresenter = new DeleteTaskPresenter(this, this);
        mUpdateTaskPresenter = new UpdateTaskPresenter(this, this);
        nameTask = taskDTO.getName();
        statusTask = taskDTO.getIdStatus();
        idTask = taskDTO.getId();
        edtUpdateTaskName.setText(nameTask);
        assignmentDTOList = new ArrayList<>();
        assignmentDTOList = taskDTO.getTaskAssignmentList();

        imgUpdateTaskBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUpdateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });

        imgDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });


        chkForUpdateAllTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignmentDTOList = new ArrayList<>();
                try {
                    if (chkForUpdateAllTodo.isChecked()) {
                        for (int i = 0; i < membersDTOSList.size(); i++) {
                            membersDTOSList.get(i).setSelected(chkForUpdateAllTodo.isChecked());
                            memberTask = membersDTOSList.get(i);
                            TaskDTO.TaskAssignmentDTO taskAssignmentDTO = new TaskDTO.TaskAssignmentDTO();
                            taskAssignmentDTO.setMember(memberTask);
                            assignmentDTOList.add(taskAssignmentDTO);
                        }
                    } else {
                        for (int i = 0; i < membersDTOSList.size(); i++) {
                            membersDTOSList.get(i).setSelected(false);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                updateUI();
            }
        });
    }


    public void updateTask() {
        if (!checkNull()) {
            return;
        }
        mUpdateTaskPresenter.updateTask(getTaskDTOFromFrom());
    }

    private boolean checkNull() {
        String nameTaskA = edtUpdateTaskName.getText().toString();
        if (nameTaskA.length() == 0 && nameTaskA.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please input name task");
            return false;
        } else {
            return true;
        }
    }


    public void deleteTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTaskActivity.this);
        builder.setMessage("Are you sure to delete this Task?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteTaskPresenter.deleteTask(taskDTO.getId());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public TaskDTO getTaskDTOFromFrom() {
        String nameWork = edtUpdateTaskName.getText().toString();
        taskDTO.setName(nameWork);
        taskDTO.setTaskAssignmentList(assignmentDTOList);
        return taskDTO;
    }


    public void updateUI() {
        if (customeTodolistAdapter == null) {
            customeTodolistAdapter = new CustomeTodolistAdapter(membersDTOSList, this);
            rcvCheckBoxUpdateMemberforto.setAdapter(customeTodolistAdapter);
            customeTodolistAdapter.setOnItemWorkForClickListener(new CustomeTodolistAdapter.OnItemWorkForClickListener() {
                @Override
                public void onItemWorkForClickListener(MemberDTO memberDTO, int position) {
                    membersDTOSList.get(position).setSelected(!membersDTOSList.get(position).isSelected());
                    boolean isAllSelected = true;
                    assignmentDTOList = new ArrayList<>();
                    for (MemberDTO memberDTO1 : membersDTOSList) {
                        if (!memberDTO1.isSelected()) {
                            isAllSelected = false;
                        } else {
                            memberTask = memberDTO1;
                            TaskDTO.TaskAssignmentDTO taskAssignmentDTO = new TaskDTO.TaskAssignmentDTO();
                            taskAssignmentDTO.setMember(memberTask);
                            assignmentDTOList.add(taskAssignmentDTO);
                        }
                    }
                    if (isAllSelected) {
                        chkForUpdateAllTodo.setChecked(true);
                    } else {
                        chkForUpdateAllTodo.setChecked(false);
                    }
                }
            });
        } else {
            customeTodolistAdapter.notifyChangeMember(membersDTOSList);
        }
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        chkForUpdateAllTodo.setVisibility(View.VISIBLE);
        if (memberDTOList != null) {
            membersDTOSList = new ArrayList<>();
            for (MemberDTO memberDTO : memberDTOList) {
                if(memberDTO.getIdStatus().equals(MemberStatus.ACTIVE)){
                    membersDTOSList.add(memberDTO);
                }
            }

            boolean isAllSelected = true;
            for (TaskDTO.TaskAssignmentDTO taskAssignmentDTO : assignmentDTOList) {
                memberTask = taskAssignmentDTO.getMember();
                    for (MemberDTO memberDTO : membersDTOSList) {
                         if(memberTask.getPerson().getFirebaseUid().equals(memberDTO.getPerson().getFirebaseUid())) {
                            memberDTO.setSelected(true);
                        }
                    }
            }
            for (MemberDTO memberDTO : membersDTOSList) {
                 if(memberDTO.isSelected() == false){
                     isAllSelected = false;
                     break;
                 }
            }
            if (isAllSelected) {
                chkForUpdateAllTodo.setChecked(true);
            } else {
                chkForUpdateAllTodo.setChecked(false);
            }
            updateUI();
        }
    }

    @Override
    public void PrintMemberFail(String message) {
        DialogShowErrorMessage.showDialogNoInternet(this, message);
    }


    @Override
    public void updateTaskSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerActivity.setValue(change);
            listenerTask.setValue(change);
        }catch (Exception e){
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void updateTaskFail(String masage) {
        DialogShowErrorMessage.showValidationDialog(this, masage);
    }

    @Override
    public void deleteTaskSuccess(String message) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerActivity.setValue(change);
            listenerTask.setValue(change);
        }catch (Exception e){
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void deleteTaskFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}

