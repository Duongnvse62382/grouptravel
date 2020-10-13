package com.fpt.gta.feature.managemember;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.managegroup.overviewgroup.GroupOverViewActivity;
import com.fpt.gta.presenter.DeActivityMemberInGroupPresenter;
import com.fpt.gta.presenter.DeleteGroupPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.DeActivityMemberInGroupView;
import com.fpt.gta.view.DeleteGroupView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

public class MemberOverviewActivity extends AppCompatActivity implements View.OnClickListener, PrintMemberInGroupView, DeleteGroupView, DeActivityMemberInGroupView {

    private RecyclerView recyclerViewListMemberOverView;
    private List<MemberDTO> membersDTOList;
    private List<MemberDTO> memberActiveList;
    private MemberOverViewAdapter memberOverViewAdapter;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private DeleteGroupPresenter mDeleteGroupPresenter;
    private DeActivityMemberInGroupPresenter deActivityMemberInGroupPresenter;
    private int groupId;
    private int isAdmin;
    private ImageView imgLeaveGroup, imgMemberBack;
    private FirebaseDatabase databaseMember;
    private DatabaseReference listenerMemberRef;
    private ValueEventListener memberValueEventListener;
    private AtomicBoolean isListening = new AtomicBoolean(true);
    private Integer groupStatus;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_overview);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadMember();
    }

    public void initView() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupStatus = SharePreferenceUtils.getIntSharedPreference(MemberOverviewActivity.this, GTABundle.GROUPSTATUS);
        deActivityMemberInGroupPresenter = new DeActivityMemberInGroupPresenter(MemberOverviewActivity.this, MemberOverviewActivity.this);
        groupId = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseMember = FirebaseDatabase.getInstance();
        listenerMemberRef = databaseMember.getReference(String.valueOf(groupId)).child("listener").child("reloadMemberUtcAt");
        mDeleteGroupPresenter = new DeleteGroupPresenter(this, this);
        recyclerViewListMemberOverView = (RecyclerView) findViewById(R.id.recycleListViewMember);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewListMemberOverView.setLayoutManager(linearLayoutManager);
        imgLeaveGroup = findViewById(R.id.imgLeaveGroup);
        imgLeaveGroup.setVisibility(View.GONE);
        imgMemberBack = findViewById(R.id.imgMemberBack);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(this, this);
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
    }

    public boolean checkValidAdmin() {
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (MemberDTO memberDTO : memberActiveList) {
            try {
                int roleMember = memberDTO.getIdRole();
                String member = memberDTO.getPerson().getFirebaseUid();
                if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                    isAdmin = roleMember;
                    return true;
                }
            } catch (Exception e) {
                Log.d("TripManageFragment", "PrintMemberSuccess: " + e.getMessage());
            }
        }

        return false;
    }


    public boolean isAllowWhileGroupExcuting() {
        if (groupStatus.equals(GroupStatus.EXECUTING)) {
            return true;
        } else if (
                groupStatus.equals(GroupStatus.PENDING)) {
            return true;
        }
        return false;
    }


    public void reloadMember() {
        memberValueEventListener = listenerMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    if (isListening.get()) {
                        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void initData() {
        imgLeaveGroup.setOnClickListener(this::onClick);
        imgMemberBack.setOnClickListener(this::onClick);


    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (memberValueEventListener != null) {
                listenerMemberRef.removeEventListener(memberValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkLeaveGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to leave this journey?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteGroupPresenter.deleteGroup(groupId);
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

    public void clickDeActivityMember(MemberDTO memberDTO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to kick this Member?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deActivityMemberInGroupPresenter.deActiveMemberInGroup(groupId, memberDTO.getId());
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

    public void updateUI() {
        if (memberOverViewAdapter == null) {
            memberOverViewAdapter = new MemberOverViewAdapter(memberActiveList, this, isAdmin, groupStatus);
            recyclerViewListMemberOverView.setAdapter(memberOverViewAdapter);

            memberOverViewAdapter.setOnClickMemberDelete(new MemberOverViewAdapter.OnClickMemberDelete() {
                @Override
                public void onClickMemberDelete(MemberDTO memberDTO, int position) {
                    clickDeActivityMember(memberDTO);
                }
            });
        } else {
            memberOverViewAdapter.notifyChangeData(memberActiveList);
        }
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        if (memberDTOList != null) {
            membersDTOList = new ArrayList<>();
            membersDTOList = memberDTOList;
            memberActiveList = new ArrayList<>();

            for (MemberDTO memberDTO : membersDTOList) {
                if (memberDTO.getIdStatus().compareTo(1) == 0) {
                    memberActiveList.add(memberDTO);
                }
            }

            boolean isCheck = checkValidAdmin();
            isCheck = isAllowWhileGroupExcuting();
            if (isCheck == true) {
                imgLeaveGroup.setVisibility(View.GONE);
            } else {
                imgLeaveGroup.setVisibility(View.VISIBLE);
            }

            initData();
            updateUI();
        }
    }

    @Override
    public void PrintMemberFail(String message) {
    }

    @Override
    public void deleteGroupSuccess(String message) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            if (memberValueEventListener != null) {
                listenerMemberRef.removeEventListener(memberValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerMemberRef.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), GroupOverViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void deleteFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgLeaveGroup:
                checkLeaveGroup();
                break;
            case R.id.imgMemberBack:
                finish();
                break;
        }
    }

    @Override
    public void deActivityMemberInGroupSuccess(String message) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerMemberRef.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deActivityMemberInGroupFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}