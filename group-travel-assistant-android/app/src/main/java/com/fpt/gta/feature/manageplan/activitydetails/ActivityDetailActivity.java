package com.fpt.gta.feature.manageplan.activitydetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.constant.PlanStatus;
import com.fpt.gta.feature.managegroup.documentgroup.ImageOpenActivity;
import com.fpt.gta.feature.managegroup.documentgroup.PdfViewActivity;
import com.fpt.gta.feature.manageplan.documentactivity.ActivityDocumentAdapter;
import com.fpt.gta.feature.managetodolist.addtodolist.AddTodoListActivity;
import com.fpt.gta.feature.managetask.TaskListApdater;
import com.fpt.gta.feature.managetodolist.updatetask.UpdateTaskActivity;
import com.fpt.gta.presenter.ChangePositionTaskPresenter;
import com.fpt.gta.presenter.PrintAllTaskPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.presenter.UpdateTaskPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.ChangePositionTaskView;

import com.fpt.gta.view.PrintAllTaskView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.fpt.gta.view.UpdateTaskview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ActivityDetailActivity extends AppCompatActivity implements View.OnClickListener, PrintAllTaskView, UpdateTaskview, ChangePositionTaskView, PrintMemberInGroupView {

    private TextView txtAcivityType, txtNameActivity, txtPlaceStartActivity, txtAddressActivityStart, txtEndPlaceActivity, txtAddressActivityEnd, txtTimeStartActivity, txtTimeEndActivity, txtTimeStartActivityNotSame, txtTimeEndActivityNotSame;
    private String nameActivity, placeStartActivity, addressActivityStart, endPlaceActivity, addressActivityEnd;
    private Integer activityType, idActivity;
    private String timeStartActivity, timeEndActivity;
    private ImageView imgActivityDetailBack, imgDeleteActivity, imgAddTodolist, imgPlaceStart, imgPlaceEnd, imgNextStart, imgNextEnd;
    private LinearLayout lnlEndPlace, lnlEndPlaceAddress;
    private ActivityDTO activityDTO;
    private PrintAllTaskPresenter mPrintAllTaskPresenter;
    private UpdateTaskPresenter mUpdateTaskPresenter;
    private RecyclerView recyclerViewTodoList, rcvOverviewActivityDocument;
    private ChangePositionTaskPresenter mChangePositionTaskPresenter;

    private TaskListApdater toDoListApdater;
    private ActivityDocumentAdapter activityDocumentAdapter;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private List<TaskDTO> tasksDTOList = new ArrayList<>();
    private PlanDTO planDTO;
    private Integer taskStatus;
    private String imgStartActivity, imgEndActivity;
    private DocumentDTO documentDTO;
    private List<DocumentDTO> mDocumentDTOList = new ArrayList<>();
    private boolean valid = false;
    private int groupId;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private List<MemberDTO> memberList;
    private LinearLayout lnlTodolist;

    Integer groupStatus;

    private Integer idGroup;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerTask;
    private ValueEventListener taskValueEventListener;
    private DatabaseReference listenerActivity;
    private ValueEventListener activityValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(taskValueEventListener != null){
                listenerTask.removeEventListener(taskValueEventListener);
            }

            if(activityValueEventListener != null){
                listenerActivity.removeEventListener(activityValueEventListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initView() {
        txtNameActivity = findViewById(R.id.txtNameActivity);
        txtPlaceStartActivity = findViewById(R.id.txtPlaceStartActivity);
        txtEndPlaceActivity = findViewById(R.id.txtEndPlaceActivity);
        txtTimeStartActivity = findViewById(R.id.txtTimeStartActivity);
        txtTimeEndActivity = findViewById(R.id.txtTimeEndActivity);
        txtTimeStartActivityNotSame = findViewById(R.id.txtTimeStartActivityNotSame);
        txtTimeEndActivityNotSame = findViewById(R.id.txtTimeEndActivityNotSame);
        txtAddressActivityStart = findViewById(R.id.txtAddressActivityStart);
        txtAddressActivityEnd = findViewById(R.id.txtAddressActivityEnd);
        imgActivityDetailBack = findViewById(R.id.imgActivityDetailBack);
        imgAddTodolist = findViewById(R.id.imgAddTodolist);
        imgPlaceStart = findViewById(R.id.imgPlaceStart);
        imgPlaceEnd = findViewById(R.id.imgPlaceEnd);
        imgNextStart = findViewById(R.id.imgNextStart);
        imgNextEnd = findViewById(R.id.imgNextEnd);
        lnlEndPlace = findViewById(R.id.lnlEndPlaceA);
        lnlTodolist = findViewById(R.id.lnlTodolist);
        recyclerViewTodoList = (RecyclerView) findViewById(R.id.rcvToDoList);
        rcvOverviewActivityDocument = (RecyclerView) findViewById(R.id.rcvOverviewActivityDocument);
        recyclerViewTodoList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvOverviewActivityDocument.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTodoList.setHasFixedSize(true);
        recyclerViewTodoList.setNestedScrollingEnabled(false);
        recyclerViewTodoList.setVisibility(View.GONE);
        txtNameActivity.setSelected(true);
        lnlTodolist.setVisibility(View.GONE);

        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");
        listenerTask = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadTask");
        Bundle bundle = getIntent().getExtras();
        activityDTO = (ActivityDTO) bundle.getSerializable(GTABundle.UPDATE_ACTIVITY_DTO);
        idActivity = activityDTO.getId();
        mDocumentDTOList = activityDTO.getDocumentList();
        planDTO = (PlanDTO) bundle.getSerializable(GTABundle.PLANOJECT);
        groupId = SharePreferenceUtils.getIntSharedPreference(ActivityDetailActivity.this, GTABundle.IDGROUP);
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDACTIVITY, idActivity);
        groupStatus = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.GROUPSTATUS);
        if(groupStatus.equals(GroupStatus.EXECUTING)){
            lnlTodolist.setVisibility(View.VISIBLE);
        }
        loadData();
        mPrintAllTaskPresenter = new PrintAllTaskPresenter(this, this);
        mUpdateTaskPresenter = new UpdateTaskPresenter(this, this);
        mChangePositionTaskPresenter = new ChangePositionTaskPresenter(this, this);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(ActivityDetailActivity.this, ActivityDetailActivity.this);
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
    }

    public void initData() {
        imgActivityDetailBack.setOnClickListener(this::onClick);
        imgAddTodolist.setOnClickListener(this::onClick);

        activityType = activityDTO.getIdType();
        nameActivity = activityDTO.getName();
        placeStartActivity = activityDTO.getStartPlace().getName();
        addressActivityStart = activityDTO.getStartPlace().getAddress();
        endPlaceActivity = activityDTO.getEndPlace().getName();
        addressActivityEnd = activityDTO.getEndPlace().getAddress();
        timeStartActivity = ZonedDateTimeUtil.convertDateTimeToString(activityDTO.getStartAt());
        timeEndActivity = ZonedDateTimeUtil.convertDateTimeToString(activityDTO.getEndAt());

        if (activityDTO.getStartPlace().getPlaceImageList().size() != 0) {
            imgStartActivity = activityDTO.getStartPlace().getPlaceImageList().get(0).getUri();
            ImageLoaderUtil.loadImage(this, imgStartActivity, imgPlaceStart);
        } else {
            Glide.with(this).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(imgPlaceStart);
        }

        if (activityDTO.getEndPlace().getPlaceImageList().size() != 0) {
            imgEndActivity = activityDTO.getEndPlace().getPlaceImageList().get(0).getUri();
            ImageLoaderUtil.loadImage(this, imgEndActivity, imgPlaceEnd);
        } else {
            Glide.with(this).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(imgPlaceEnd);
        }

        txtNameActivity.setText(nameActivity);
        txtPlaceStartActivity.setText(placeStartActivity);
        txtEndPlaceActivity.setText(endPlaceActivity);
        txtAddressActivityStart.setText(addressActivityStart);
        txtAddressActivityEnd.setText(addressActivityEnd);
        txtTimeStartActivity.setText(timeStartActivity);
        txtTimeEndActivity.setText(timeEndActivity);


        txtTimeStartActivityNotSame.setText(timeStartActivity);
        txtTimeEndActivityNotSame.setText(timeEndActivity);

        taskValueEventListener = listenerTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintAllTaskPresenter.printAllTask(idActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void loadData() {
        updateActivityDoucument();
        goneWithSamePlace();
    }

    private void updateActivityDoucument() {
        if (activityDocumentAdapter == null) {
            activityDocumentAdapter = new ActivityDocumentAdapter(ActivityDetailActivity.this, mDocumentDTOList);
            rcvOverviewActivityDocument.setAdapter(activityDocumentAdapter);
            activityDocumentAdapter.setOnItemImageClickListenerl(new ActivityDocumentAdapter.OnItemImageClickListener() {
                @Override
                public void onItemImageClickListener(DocumentDTO documentDTO, int position) {
                    if (documentDTO.getContentType().equals("image/jpeg") || documentDTO.getContentType().equals("image/png")) {
                        onClickImage(documentDTO);
                    } else if (documentDTO.getContentType().equals("application/pdf")) {
                        onClickPDF(documentDTO);
                    }
                }
            });

        } else {
            activityDocumentAdapter.notifyChangeData(mDocumentDTOList);
        }
    }


    public void onClickImage(DocumentDTO documentDTO) {
        Intent intent = new Intent(ActivityDetailActivity.this, ImageOpenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "ActivityPlanDetailActivity");
        bundle.putBoolean("ActivityValid", valid);
        bundle.putSerializable(GTABundle.ACTIVITY_DETAIL_DOCUMENT_IMAGE, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.ACTIVITY_DETAIL_DOCUMENT_IMAGE_CODE);
    }

    public void onClickPDF(DocumentDTO documentDTO) {
        Intent intent = new Intent(ActivityDetailActivity.this, PdfViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CALLING_ACTIVITY", "ActivityPlanDetailActivity");
        bundle.putBoolean("ActivityValid", valid);
        bundle.putSerializable(GTABundle.ACTIVITY_DETAIL_DOCUMENT_PDF, documentDTO);
        intent.putExtras(bundle);
        startActivityForResult(intent, GTABundle.ACTIVITY_DETAIL_DOCUMENT_PDF_CODE);
    }


    public void goneWithSamePlace() {
        String idGoogleStart = activityDTO.getStartPlace().getGooglePlaceId();
        String idGoogleEnd = activityDTO.getEndPlace().getGooglePlaceId();
        if (idGoogleStart.equals(idGoogleEnd)) {
            lnlEndPlace.setVisibility(View.GONE);
        }else {
            imgNextStart.setVisibility(View.GONE);
            txtTimeEndActivity.setVisibility(View.GONE);
            txtTimeStartActivityNotSame.setVisibility(View.GONE);
            imgNextEnd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgActivityDetailBack:
                finish();
                break;
            case R.id.imgAddTodolist:
                clickToAddTodoList();
                break;
        }
    }


    public void clickToAddTodoList() {
        Intent intent = new Intent(ActivityDetailActivity.this, AddTodoListActivity.class);
        startActivity(intent);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GTABundle.UPDATE_ACTIVITY_DTO_CODE) {
            if (resultCode == RESULT_OK) {
                activityDTO = (ActivityDTO) data.getSerializableExtra(GTABundle.UPDATE_ACTIVITY_DTO);
                mDocumentDTOList = activityDTO.getDocumentList();
            }
        } else if (requestCode == GTABundle.ACTIVITY_DETAIL_DOCUMENT_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                documentDTO = (DocumentDTO) data.getSerializableExtra(GTABundle.ACTIVITY_DETAIL_DOCUMENT_IMAGE);
                mDocumentDTOList.removeIf(removingDocumentDTO -> removingDocumentDTO.getId().compareTo(documentDTO.getId()) == 0);
                updateActivityDoucument();
            }
        } else if (requestCode == GTABundle.ACTIVITY_DETAIL_DOCUMENT_PDF_CODE) {
            if (resultCode == RESULT_OK) {
                documentDTO = (DocumentDTO) data.getSerializableExtra(GTABundle.ACTIVITY_DETAIL_DOCUMENT_PDF);
                mDocumentDTOList.removeIf(removingDocumentDTO -> removingDocumentDTO.getId().compareTo(documentDTO.getId()) == 0);
            }
        }
    }

    public void updateUI() {
        if (toDoListApdater == null) {
            toDoListApdater = new TaskListApdater(ActivityDetailActivity.this, tasksDTOList);
            recyclerViewTodoList.setAdapter(toDoListApdater);

            ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    int dragFlags = ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END;
                    int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }


                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    toDoListApdater.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    try {
                        Integer order = target.getLayoutPosition();
                        if (viewHolder.getLayoutPosition() < target.getLayoutPosition()) {
                            order = order + 1;
                        }
                        Integer idTask = tasksDTOList.get(viewHolder.getAdapterPosition()).getId();
                        mChangePositionTaskPresenter.changePositionTask(idTask, order);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    toDoListApdater.onItemDismiss(viewHolder.getAdapterPosition());
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerViewTodoList);
            toDoListApdater.setOnItemEditTaskClickListener(new TaskListApdater.OnItemEditTaskClickListener() {
                @Override
                public void onItemEditTaskClickListener(TaskDTO taskDTO, int position) {
                    Intent intent = new Intent(ActivityDetailActivity.this, UpdateTaskActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(GTABundle.KEYTASK, taskDTO);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            toDoListApdater.setOnCheckToDoClickListener(new TaskListApdater.OnCheckToDoClickListener() {
                @Override
                public void onCheckToDoClickListener(TaskDTO taskDTO, int position) {
                    TaskDTO task = tasksDTOList.get(position);
                    task.setIdStatus(task.getIdStatus().equals(1) ? 0 : 1);
                    mUpdateTaskPresenter.updateTask(taskDTO);
                }
            });

        } else {
            toDoListApdater.notifyDataChange(tasksDTOList);
        }
    }



    @Override
    public void printAllTaskSuccess(List<TaskDTO> taskDTOList) {
        if (taskDTOList.size() != 0) {
            tasksDTOList = taskDTOList;
            tasksDTOList.sort((o1, o2) -> o1.getOrder().compareTo(o2.getOrder()));
            recyclerViewTodoList.setVisibility(View.VISIBLE);
        } else {
            recyclerViewTodoList.setVisibility(View.GONE);
        }
        updateUI();
    }

    @Override
    public void printAllTaskFail(String messageFail) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTaskFail(String masage) {
        DialogShowErrorMessage.showValidationDialog(this, masage);
    }

    @Override
    public void changePositionTaskSS(String messsageSS) {
        updateUI();
    }

    @Override
    public void changePositionFail(String messageFail) {

    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        memberList = new ArrayList<>();
        memberList = memberDTOList;
        String idFribase;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        idFribase = user.getUid();
        for (MemberDTO memberDTO : memberList) {
            int idRole = memberDTO.getIdRole();
            if (idFribase.equals(memberDTO.getPerson().getFirebaseUid()) && idRole == MemberRole.ADMIN) {
                valid = true;
                break;
            }
        }
    }

    @Override
    public void PrintMemberFail(String message) {

    }
}