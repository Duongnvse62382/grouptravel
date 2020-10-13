package com.fpt.gta.feature.managetrip.overviewtrip;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.FriendlyMessage;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.feature.chatgroup.ChatGroupActivity;
import com.fpt.gta.feature.managebalance.BalanceFragment;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.managebudget.BudgetFragment;
import com.fpt.gta.feature.managebudget.ConfirmBudgetGroupActivity;
import com.fpt.gta.feature.managemember.MemberOverviewActivity;
import com.fpt.gta.feature.managemember.MemberViewAdapter;
import com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity.OverViewSuggestAndVoteCustomAdapter;
import com.fpt.gta.feature.managetransaction.transactiontypeoverview.TransactionOverViewFragment;
import com.fpt.gta.feature.managetrip.manageconfirm.ConfirmElectedFragment;
import com.fpt.gta.presenter.CreateInvitationCodePresenter;
import com.fpt.gta.presenter.PrintGroupByIdPresenter;
import com.fpt.gta.presenter.PrintInvitationPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateInvitationCodeView;
import com.fpt.gta.view.PrintGroupByIdView;
import com.fpt.gta.view.PrintInvitationView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ru.nikartm.support.ImageBadgeView;


public class TripOverviewActivity extends AppCompatActivity implements View.OnClickListener, PrintMemberInGroupView,
        PrintGroupByIdView, PrintInvitationView, CreateInvitationCodeView {
    public static final String MESSAGES_CHILD = "messages";
    public static final String MEMBERS_CHILD = "members";
    private ViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;
    private FragmentStatePagerItemAdapter mAdapter;
    private ImageView imgBack, imgInviteMember, imgMakePending;
    private TextView txtNameGroup, txtMemberNumber;
    private GroupResponseDTO groupResponseDTO;
    private GroupResponseDTO groupDTO;
    private RecyclerView recyclerViewMember;
    private MemberViewAdapter memberViewAdapter;
    private List<MemberDTO> membersDTOList;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private PrintInvitationPresenter mPrintInvitationPresenter;
    private CreateInvitationCodePresenter mCreateInvitationCodePresenter;
    private PrintGroupByIdPresenter mPrintGroupByIdPresenter;
    private Integer groupStatus;
    private int groupId, roleMember, i;
    private String invitationCode;
    private ImageBadgeView imgChatGroup;
    //    private FirebaseFirestore database;
    private AlertDialog alert;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Date currentTime;
    private StorageReference mStorage;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference listenerMemberRef;
    private DatabaseReference listenerTransactionRef;
    private DatabaseReference listenerGroupByidRef;
    private ValueEventListener makeReadyValueEventListener;
    private ValueEventListener memberValueEventListener;
    private ValueEventListener transactionValueEventListener;
    private ValueEventListener lastestMessageTimeValueEventListener;
    private ValueEventListener reloadMessageTimeValueEventListener;
    private FirebaseDatabase databaseTrip;
    private DatabaseReference listenerTrip;
    private List<MemberDTO> memberActiveList;
    private List<FriendlyMessage> friendlyMessageList;
    private String userId;
    private DatabaseReference membersRef;
    private long lastMessageTimeRead;
    private DatabaseReference listenerMemberBadges;
    private DatabaseReference reloadGroupStatus;
    private int memberSize;
    private Long dateMember = -1l;
    private Long date = -1l;

    private FragmentPagerItems fragmentPagerItems = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_manager);
        firebaseAuth = FirebaseAuth.getInstance();
        initialView();
        initialData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadGroup();
        loadMember();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initialView() {
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD);
        currentTime = Calendar.getInstance().getTime();
        imgChatGroup = findViewById(R.id.imgChatGroup);
        mViewPager = findViewById(R.id.viewpager);
        mSmartTabLayout = findViewById(R.id.view_pager_tab);
        imgBack = findViewById(R.id.imgBack);
        imgMakePending = findViewById(R.id.imgMakePending);
        imgInviteMember = findViewById(R.id.imgAddMember);
        txtNameGroup = findViewById(R.id.txtNameGroup);
        txtMemberNumber = findViewById(R.id.txtMemberNumber);
        recyclerViewMember = findViewById(R.id.recycleViewMember);
        imgInviteMember.setVisibility(View.GONE);
        imgMakePending.setVisibility(View.GONE);
        imgChatGroup.setVisibility(View.GONE);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMember.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupResponseDTO = (GroupResponseDTO) bundle.getSerializable(GTABundle.KEYGROUP);
        groupId = groupResponseDTO.getId();
        txtNameGroup.setSelected(true);
        membersRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MEMBERS_CHILD).child(userId).child("lastestReadMessage");
        listenerMemberRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadMemberUtcAt");
        listenerTransactionRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadTransactionUtc");
        listenerMemberBadges = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadMemberBadges");
        listenerGroupByidRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadGroupById");
        reloadGroupStatus = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadGroupStatus");
        invitationCode = groupResponseDTO.getInvitationCode();
        mCreateInvitationCodePresenter = new CreateInvitationCodePresenter(this, this);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(this, this);
        mPrintInvitationPresenter = new PrintInvitationPresenter(TripOverviewActivity.this, TripOverviewActivity.this);
        mPrintGroupByIdPresenter = new PrintGroupByIdPresenter(this, this);
    }

    public void loadMessagesBadge() {

        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    lastMessageTimeRead = snapshot.getValue(Long.class);
                } else {
                    TimeZone tz = TimeZone.getDefault();
                    String timez = tz.getID();
                    Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                    lastMessageTimeRead = dateNoti.getTime();
                }
                lastestMessageTimeValueEventListener = listenerMemberBadges.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long date = snapshot.getValue(Long.class);
                        loadNewMessage();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                reloadMessageTimeValueEventListener = membersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            lastMessageTimeRead = snapshot.getValue(Long.class);
                        } else {
                            TimeZone tz = TimeZone.getDefault();
                            String timez = tz.getID();
                            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                            lastMessageTimeRead = dateNoti.getTime();
                        }
                        loadNewMessage();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//
//    public void loadTransactionChange(){
//        transactionValueEventListener = listenerTransactionRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    Long date = snapshot.getValue(Long.class);
//                    loadMessagesBadge();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public void loadNewMessage() {
        try {
            Query query;
            query = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD).orderByChild("messageTime")
                    .startAt(lastMessageTimeRead, "messageTime");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    friendlyMessageList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                        friendlyMessageList.add(friendlyMessage);
                    }
                    imgChatGroup.setBadgeValue(friendlyMessageList.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goneWithNotAdmin() {
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            for (i = 0; i <= groupResponseDTO.getMemberList().size(); i++) {
                roleMember = groupResponseDTO.getMemberList().get(i).getIdRole();
                String member = groupResponseDTO.getMemberList().get(i).getPerson().getFirebaseUid();
                if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                    imgMakePending.setVisibility(View.VISIBLE);
                    if (!groupResponseDTO.getIdStatus().equals(GroupStatus.PLANNING)) {
                        imgMakePending.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("CityOverviewActivity", "Role: " + e.getMessage());
        }
    }

    public boolean isAdminGroup() {
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            for (i = 0; i <= groupDTO.getMemberList().size(); i++) {
                roleMember = groupDTO.getMemberList().get(i).getIdRole();
                String member = groupDTO.getMemberList().get(i).getPerson().getFirebaseUid();
                if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                    if (!groupDTO.getIdStatus().equals(GroupStatus.EXECUTING)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("CityOverviewActivity", "Role: " + e.getMessage());
        }
        return false;
    }


    public void loadGroup() {
        makeReadyValueEventListener = reloadGroupStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long latestValue = snapshot.getValue(Long.class);
                    if (latestValue == null || date == null) {
                        if (date != latestValue) {
                            mPrintGroupByIdPresenter.getGroupById(groupId);
                        }
                    } else {
                        if (Long.compare(date, latestValue) != 0) {
                            mPrintGroupByIdPresenter.getGroupById(groupId);
                        }
                    }
                    date = snapshot.getValue(Long.class);


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
            if (memberValueEventListener != null) {
                listenerMemberRef.removeEventListener(memberValueEventListener);
            }
            if (lastestMessageTimeValueEventListener != null) {
                listenerMemberBadges.removeEventListener(lastestMessageTimeValueEventListener);
            }
            if (reloadMessageTimeValueEventListener != null) {
                membersRef.removeEventListener(reloadMessageTimeValueEventListener);
            }
            if (transactionValueEventListener != null) {
                listenerTransactionRef.removeEventListener(transactionValueEventListener);
            }

            if (makeReadyValueEventListener != null) {
                reloadGroupStatus.removeEventListener(makeReadyValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void refreshFragmentPage() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Fragment fragment = mAdapter.getPage(i);
            if (fragment != null && fragment instanceof TripManageFragment) {
                ((TripManageFragment) fragment).reloadFagment();
            }
            if (fragment != null && fragment instanceof TransactionOverViewFragment) {
                ((TransactionOverViewFragment) fragment).reloadFragment();
            }

            if (fragment != null && fragment instanceof BalanceFragment) {
                ((BalanceFragment) fragment).reloadFragment();
            }
            if (fragment != null && fragment instanceof ConfirmElectedFragment) {
                ((ConfirmElectedFragment) fragment).loadConfirmMember();
            }
        }
    }


    private void initialData() {
        loadMessagesBadge();
        imgBack.setOnClickListener(this::onClick);
        imgMakePending.setOnClickListener(this::onClick);
        imgInviteMember.setOnClickListener(this::onClick);
        imgChatGroup.setOnClickListener(this::onClick);
        txtNameGroup.setText(groupResponseDTO.getName());
    }


        public void loadMember() {
        memberValueEventListener = listenerMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (dateMember != snapshot.getValue(Long.class)) {
                        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
                    }
                    dateMember = snapshot.getValue(Long.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        transactionValueEventListener = listenerTransactionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    loadMessagesBadge();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void viewTabAdapter() {
        FragmentPagerItems.Creator pageCreator = FragmentPagerItems.with(this);
        if (groupDTO.getIdStatus().equals(GroupStatus.PLANNING)) {
            pageCreator.add(FragmentPagerItem.of("City", TripManageFragment.class));
        } else if (groupDTO.getIdStatus().equals(GroupStatus.PENDING)) {
            pageCreator.add(FragmentPagerItem.of("City", TripManageFragment.class));
            pageCreator.add(FragmentPagerItem.of("Confirm", ConfirmElectedFragment.class));
        } else if (groupDTO.getIdStatus().equals(GroupStatus.EXECUTING)) {
            pageCreator.add(FragmentPagerItem.of("City", TripManageFragment.class));
            pageCreator.add(FragmentPagerItem.of("Expense", TransactionOverViewFragment.class));
            pageCreator.add(FragmentPagerItem.of("Balance", BalanceFragment.class));
            pageCreator.add(FragmentPagerItem.of("Budget", BudgetFragment.class));
        }
        fragmentPagerItems = pageCreator.create();
        mAdapter = new FragmentStatePagerItemAdapter(
                getSupportFragmentManager(), fragmentPagerItems);
        OverViewSuggestAndVoteCustomAdapter tabAdapter = new OverViewSuggestAndVoteCustomAdapter(this, fragmentPagerItems);
        mSmartTabLayout.setCustomTabView(tabAdapter);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mSmartTabLayout.setViewPager(mViewPager);
        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            TextView textView = mSmartTabLayout.getTabAt(i).findViewById(R.id.name_tab);
            if (i == 0) {
                textView.setTextColor(Color.parseColor("#56A8A2"));
            } else {
                textView.setTextColor(Color.parseColor("#000000"));
            }
        }

        mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                    TextView textView = mSmartTabLayout.getTabAt(i).findViewById(R.id.name_tab);
                    if (i == position) {
                        textView.setTextColor(Color.parseColor("#56A8A2"));
                    } else {
                        textView.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgBack:
                finish();
                break;

            case R.id.imgMakePending:
                Intent intent = new Intent(this, ConfirmBudgetGroupActivity.class);
                startActivity(intent);
                break;

            case R.id.imgAddMember:
                addMemberDialog();
                break;
            case R.id.imgChatGroup:
//                TimeZone tz = TimeZone.getDefault();
//                String timez = tz.getID();
//                Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
//                Long messageTime = dateNoti.getTime();
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                String userName = null, userImg = null;

//                for (int j = 0; j < memberActiveList.size(); j++) {
//                    userName = memberActiveList.get(j).getPerson().getName();
//                    userImg = memberActiveList.get(j).getPerson().getPhotoUri();
//                }

                Intent chatIntent = new Intent(TripOverviewActivity.this, ChatGroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("MEMBERLIST", (Serializable) membersDTOList);
                chatIntent.putExtras(bundle);
                startActivity(chatIntent);
                break;
        }
    }


    @Override
    public void viewInvitationDialog(String inviteLink) {
        alert = new AlertDialog.Builder(this).create();
        LayoutInflater layoutInflater = this.getLayoutInflater();
        ViewGroup viewGroup = findViewById(R.id.content);
        View viewDialog = layoutInflater.inflate(R.layout.add_member_dialog, viewGroup, false);
        TextView txtLink = viewDialog.findViewById(R.id.txtLink);
        Button btnCopy = viewDialog.findViewById(R.id.btnCopy);
        Button btnLoadCode = viewDialog.findViewById(R.id.btnLoadCode);
        if (invitationCode == null) {
            txtLink.setText("Load Please!");
        }
        {
            txtLink.setText(inviteLink);
        }
        btnLoadCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreateInvitationCodePresenter.createInvitationCode(groupId);
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String txtCopy = txtLink.getText().toString();
                ClipData clip = ClipData.newPlainText("text", txtCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TripOverviewActivity.this, "Copy", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setView(viewDialog);
        alert.show();
    }

    public void addMemberDialog() {
        mPrintInvitationPresenter.printInvitationCode(groupId);
    }

    public void updateUI() {
        if (memberViewAdapter == null) {
            memberViewAdapter = new MemberViewAdapter(memberActiveList, this);
            recyclerViewMember.setAdapter(memberViewAdapter);
            memberViewAdapter.setOnClickmember(new MemberViewAdapter.OnClickIteamMember() {
                @Override
                public void onClickIteam(List<MemberDTO> memberDTO, int position) {
                    Intent intent = new Intent(TripOverviewActivity.this, MemberOverviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(GTABundle.KEYOWNER, memberActiveList.get(position));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } else {
            memberViewAdapter.notifyChangeData(memberActiveList);
        }
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        if (memberDTOList != null) {
            this.membersDTOList = memberDTOList;
            memberActiveList = new ArrayList<>();
            imgChatGroup.setVisibility(View.VISIBLE);
            for (MemberDTO memberDTO : membersDTOList) {
                if (memberDTO.getIdStatus().compareTo(1) == 0) {
                    memberActiveList.add(memberDTO);
                }
            }
            memberSize = memberActiveList.size();
            SharePreferenceUtils.saveIntSharedPreference(TripOverviewActivity.this, GTABundle.MEMBERNUMER, memberSize);

            updateUI();
            txtMemberNumber.setText("Members: " + memberActiveList.size());
        }
    }


    @Override
    public void PrintMemberFail(String message) {
        DialogShowErrorMessage.showValidationDialog(this, message);
    }

    @Override
    public void printCodeInviteSuccess(String inviteCode) {
        if (inviteCode != null & alert != null) {
            alert.dismiss();
        }
    }

    @Override
    public void printCodeInviteFail(String message) {
        DialogShowErrorMessage.showValidationDialog(this, message);
    }

    @Override
    public void createInvitationSuccess(String message) {
        if (alert != null) {
            alert.dismiss();
        }
        Toast.makeText(TripOverviewActivity.this, "Renew Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void createInvitationFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }

    @Override
    public void printGroupByIdSS(GroupResponseDTO groupResponseDTO) {
        groupDTO = groupResponseDTO;
        groupStatus = groupDTO.getIdStatus();
        if(groupStatus.equals(GroupStatus.PLANNING)){
            txtNameGroup.setText(groupResponseDTO.getName() + " (Planning)");
        }else if(groupStatus.equals(GroupStatus.PENDING)){
            txtNameGroup.setText(groupResponseDTO.getName() + " (Make Pending)");
        }else {
            txtNameGroup.setText(groupResponseDTO.getName()  + " (Executing)");
        }

        SharePreferenceUtils.saveIntSharedPreference(TripOverviewActivity.this, GTABundle.GROUPSTATUS, groupStatus);
        viewTabAdapter();
        boolean isCheck = isAdminGroup();
        if (isCheck == true) {
            if (groupDTO.getIdStatus().equals(GroupStatus.PENDING)) {
                imgInviteMember.setVisibility(View.GONE);
                imgMakePending.setVisibility(View.GONE);
            } else if (groupDTO.getIdStatus().equals(GroupStatus.EXECUTING)) {
                imgInviteMember.setVisibility(View.GONE);
                imgMakePending.setVisibility(View.GONE);
            } else if (groupDTO.getIdStatus().equals(GroupStatus.PLANNING)) {
                imgInviteMember.setVisibility(View.VISIBLE);
                imgMakePending.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void printGroupByIdFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}