package com.fpt.gta.feature.managetodolist.addtodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.data.dto.constant.MemberStatus;
import com.fpt.gta.presenter.CreateTaskPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateTaskView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AddTodoListActivity extends AppCompatActivity implements PrintMemberInGroupView, CreateTaskView {
    private List<MemberDTO> membersDTOSList = new ArrayList<>();
    private EditText edtAddTodoTitle;
    private CheckBox chkForAddAllTodo;
    private ImageView imgAddTodoBack;
    private Button btnAddNewTask;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private CreateTaskPresenter mCreateTaskPresenter;
    private RecyclerView rcvCheckBoxAddMemberTodo;
    private CustomeTodolistAdapter customeTodolistAdapter;
    private boolean check = false;
    private Integer idActivity;
    private List<TaskDTO.TaskAssignmentDTO> assignmentDTOList;
    private TaskDTO taskDTO;
    private MemberDTO memberTask = null;
    private TripReponseDTO tripReponseDTO = null;
    private GroupResponseDTO groupResponseDTO = null;

    private Integer idGroup;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;
    private DatabaseReference listenerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_list);
        initView();
        initData();
    }

    public void initView() {
        chkForAddAllTodo = findViewById(R.id.chkForAddAllTodo);
        imgAddTodoBack = findViewById(R.id.imgAddTodoBack);
        edtAddTodoTitle = findViewById(R.id.edtAddTodoTitle);
        btnAddNewTask = findViewById(R.id.btnAddNewTask);
        rcvCheckBoxAddMemberTodo = findViewById(R.id.rcvCheckBoxAddMemberforto);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvCheckBoxAddMemberTodo.setLayoutManager(linearLayoutManager);
        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        chkForAddAllTodo.setVisibility(View.GONE);
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");
        listenerTask = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadTask");
    }


    public void initData() {
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(this, this);
        mPrintMemberInGroupPresenter.printMemberInGroup(idGroup);
        mCreateTaskPresenter = new CreateTaskPresenter(this, this);
        idActivity = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDACTIVITY);

        try {
            Bundle bundle = getIntent().getExtras();
            tripReponseDTO = (TripReponseDTO) bundle.getSerializable(GTABundle.TRIPTASK);
        }catch (Exception e){
            e.printStackTrace();
        }

        String journey = SharePreferenceUtils.getStringSharedPreference(this, GTABundle.JOURNEYOJECT);
        Gson gson = new Gson();
        groupResponseDTO = gson.fromJson(journey, GroupResponseDTO.class);


        customeTodolistAdapter = new CustomeTodolistAdapter(membersDTOSList, this);
        rcvCheckBoxAddMemberTodo.setAdapter(customeTodolistAdapter);
        customeTodolistAdapter.setOnItemWorkForClickListener(new CustomeTodolistAdapter.OnItemWorkForClickListener() {
            @Override
            public void onItemWorkForClickListener(MemberDTO memberDTO, int position) {
                membersDTOSList.get(position).setSelected(!membersDTOSList.get(position).isSelected());
                boolean isAllSelected = true;
                assignmentDTOList = new ArrayList<>();
                for (MemberDTO memberDTO1 : membersDTOSList) {
                    if (!memberDTO1.isSelected()) {
                        isAllSelected = false;
                    }else {
                        memberTask = memberDTO1;
                        TaskDTO.TaskAssignmentDTO taskAssignmentDTO = new TaskDTO.TaskAssignmentDTO();
                        taskAssignmentDTO.setMember(memberTask);
                        assignmentDTOList.add(taskAssignmentDTO);
                    }
                }
                if (isAllSelected) {
                    chkForAddAllTodo.setChecked(true);
                } else {
                    chkForAddAllTodo.setChecked(false);
                }
                updateUI();
            }
        });


        imgAddTodoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });

        chkForAddAllTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignmentDTOList = new ArrayList<>();
                if (chkForAddAllTodo.isChecked()) {
                    for (int i = 0; i < membersDTOSList.size(); i++) {
                        membersDTOSList.get(i).setSelected(chkForAddAllTodo.isChecked());
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
                updateUI();
            }
        });
    }


    public void createTask() {
        if(!checkNull()){
            return;
        }
        mCreateTaskPresenter.createTask(idActivity, getTaskDTOFromFrom());
    }

    private boolean checkNull() {
        String nameTaskA = edtAddTodoTitle.getText().toString();
        if (nameTaskA.length() == 0 && nameTaskA.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this,"Please input name task");
            return false;
        } else {
            return true;
        }
    }

    public TaskDTO getTaskDTOFromFrom() {
        taskDTO = new TaskDTO();
        String nameWork = edtAddTodoTitle.getText().toString();
        taskDTO.setIdStatus(0);
        taskDTO.setGroup(groupResponseDTO);
        if(tripReponseDTO != null){
            taskDTO.setTrip(tripReponseDTO);
        }
        taskDTO.setName(nameWork);
        taskDTO.setTaskAssignmentList(assignmentDTOList);
        return taskDTO;
    }


    public void updateUI() {
        if (customeTodolistAdapter == null) {

        } else {
            customeTodolistAdapter.notifyChangeMember(membersDTOSList);
        }
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        chkForAddAllTodo.setVisibility(View.VISIBLE);
        membersDTOSList = new ArrayList<>();
        if (memberDTOList != null) {
            for (MemberDTO memberDTO : memberDTOList) {
                if(memberDTO.getIdStatus().equals(MemberStatus.ACTIVE)){
                    membersDTOSList.add(memberDTO);
                }
            }

            updateUI();
        }
    }

    @Override
    public void PrintMemberFail(String message) {
        DialogShowErrorMessage.showDialogNoInternet(this, message);
    }

    @Override
    public void createTaskSuccess(String messageSuccess) {
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
    public void createTaskFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}