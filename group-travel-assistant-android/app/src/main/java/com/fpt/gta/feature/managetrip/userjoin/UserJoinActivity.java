package com.fpt.gta.feature.managetrip.userjoin;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.constant.MemberStatus;
import com.fpt.gta.feature.managebudget.BudgetFragment;
import com.fpt.gta.feature.managetrip.overviewtrip.OverViewTripCustomAdapter;
import com.fpt.gta.feature.managetrip.overviewtrip.TripOverviewActivity;
import com.fpt.gta.feature.managetrip.overviewtrip.TripViewUserFragment;
import com.fpt.gta.feature.managetrip.userjoin.plandetailfragment.BudgetViewFragment;
import com.fpt.gta.presenter.CreateInvitationCodeCopyPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateInvitationCodeCopyView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;
import java.util.Date;
import java.util.TimeZone;


public class UserJoinActivity extends AppCompatActivity implements View.OnClickListener, CreateInvitationCodeCopyView {
    private ViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;
    private FragmentStatePagerItemAdapter mAdapter;
    private ImageView imgExitJourney, imgConfirmJourney;
    private TextView txtNameGroup, txtMemberNumber;
    private GroupResponseDTO groupResponseDTO;
    private CreateInvitationCodeCopyPresenter mCreateInvitationCodeCopyPresenter;
    private Integer groupId;
    private String codeEnrollInGroup;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference listenerMemberRef;
    private ValueEventListener memberValueEventListener;
    private FirebaseDatabase databaseMember;
    private DatabaseReference listenerMember;
    private TextView txtPointStart, txtGroupDateStart, txtGroupDateEnd, txtGroupCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_join);
        initialView();
        initialData();
    }

    private void initialView() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mViewPager = findViewById(R.id.viewpager);
        mSmartTabLayout = findViewById(R.id.view_pager_tab);
        imgExitJourney = findViewById(R.id.imgExitJourney);
        imgConfirmJourney = findViewById(R.id.imgConfirmJourney);
        txtNameGroup = findViewById(R.id.txtNameGroup);
        txtMemberNumber = findViewById(R.id.txtMemberNumber);
        txtPointStart = findViewById(R.id.txtPointStart);
        txtGroupDateStart = findViewById(R.id.txtGroupDateStart);
        txtGroupDateEnd = findViewById(R.id.txtGroupDateEnd);
        txtGroupCurrency = findViewById(R.id.txtGroupCurrency);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupResponseDTO = (GroupResponseDTO) bundle.getSerializable(GTABundle.KEYGROUP);
        groupId = groupResponseDTO.getId();
        codeEnrollInGroup = groupResponseDTO.getInvitationCode();
        txtNameGroup.setSelected(true);
        listenerMemberRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadMemberUtcAt");
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDGROUP, groupId);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to exit this Journey?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserJoinActivity.super.onBackPressed();
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

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void initialData() {
        mCreateInvitationCodeCopyPresenter = new CreateInvitationCodeCopyPresenter(this, this);
        imgExitJourney.setOnClickListener(this::onClick);
        imgConfirmJourney.setOnClickListener(this::onClick);
        int count = 0;
        for (GroupResponseDTO.MemberDTO memberDTO : groupResponseDTO.getMemberList()) {
            if(memberDTO.getIdStatus().equals(MemberStatus.ACTIVE)){
                count++;
            }
        }

        txtMemberNumber.setText("Members: " + count);
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.MEMBERSIZE, count);

        txtPointStart.setText("Point Start: " + groupResponseDTO.getStartPlace().getName());
        txtGroupDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getStartAt()));
        txtGroupDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getEndAt()));
        txtGroupCurrency.setText("Journey Currency: " + groupResponseDTO.getCurrency().getName());

        txtNameGroup.setText(groupResponseDTO.getName());
        final OverViewTripCustomAdapter mPagerAdapter = new OverViewTripCustomAdapter(this);
        mSmartTabLayout.setCustomTabView(mPagerAdapter);
        FragmentPagerItems.Creator pages = FragmentPagerItems.with(this);
        pages.add(FragmentPagerItem.of("City", TripViewUserFragment.class));
        FragmentPagerItems fragmentPagerItems = pages.create();
        mAdapter = new FragmentStatePagerItemAdapter(
                getSupportFragmentManager(), fragmentPagerItems);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mSmartTabLayout.setViewPager(mViewPager);
        TextView txtTrip = mSmartTabLayout.getTabAt(0).findViewById(R.id.name_tab);
        txtTrip.setText("City");
        txtTrip.setTextColor(Color.parseColor("#56A8A2"));
        mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    txtTrip.setTextColor(Color.parseColor("#56A8A2"));
                } else if (position == 1) {
                    txtTrip.setTextColor(Color.parseColor("#000000"));
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
            case R.id.imgExitJourney:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure to exit this Journey?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.imgConfirmJourney:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("Are you sure to join this Journey?");
                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCreateInvitationCodeCopyPresenter.createInvitationCodeCopy(groupId, codeEnrollInGroup);
                    }
                });
                builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                break;

        }
    }


    public void SendDateGo(String dateGo) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.DATEGO, dateGo);
    }

    public void SendLatLong(String latlong) {
        SharePreferenceUtils.saveStringSharedPreference(this, "GROUPLATLONG", latlong);
    }

    public void SendDateEnd(String dateEnd) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.DATEEND, dateEnd);
    }

    public void SendData(int id) {
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDGROUP, id);
    }

    public void SendDateGoUTC(String dateGoUTC) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.DATEGOUTC, dateGoUTC);
    }

    public void SendDateEndUTC(String dateEndUTC) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.DATEENDUTC, dateEndUTC);
    }

    public void SendTimeZone(String timeZone) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.TIMEZONEGROUP, timeZone);
    }

    public void SendGroupCurrency(CurrencyDTO currencyDTO) {
        Gson gson = new Gson();
        String groupCurrency = gson.toJson(currencyDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.GROUP_CURRENCY_SHARE, groupCurrency);
    }

    @Override
    public void createCopyInvitationSuccess(GroupResponseDTO groupResponseDTO) {
        databaseMember = FirebaseDatabase.getInstance();
        listenerMember = databaseMember.getReference(String.valueOf(groupId)).child("listener").child("reloadMemberUtcAt");
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerMember.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String timeZoneStart = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getStartAt());
        String timeZoneEnd = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getEndAt());
        String timeZoneStartUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getStartUtcAt());
        String timeZoneEndUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getEndUtcAt());
        String timezone = groupResponseDTO.getStartPlace().getTimeZone();
        LatLng latLng = new LatLng(groupResponseDTO.getStartPlace().getLatitude().doubleValue(), groupResponseDTO.getStartPlace().getLongitude().doubleValue());
        Gson gson = new Gson();
        String json = gson.toJson(latLng);
        SendLatLong(json);
        SendDateGo(timeZoneStart);
        SendDateEnd(timeZoneEnd);
        SendDateGoUTC(timeZoneStartUTC);
        SendDateEndUTC(timeZoneEndUTC);
        SendTimeZone(timezone);
        SendData(groupResponseDTO.getId());

        CurrencyDTO currencyDTO = groupResponseDTO.getCurrency();
        SendGroupCurrency(currencyDTO);
        String journeyDTOS = gson.toJson(groupResponseDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.JOURNEYOJECT, journeyDTOS);
        Integer groupStatus = groupResponseDTO.getIdStatus();
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.GROUPOBJECTSTATUS, groupStatus);

        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDGROUP, groupResponseDTO.getId());
        Intent intent1 = new Intent(this, TripOverviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYGROUP, groupResponseDTO);
        intent1.putExtras(bundle);
        startActivity(intent1);
        finish();
    }

    @Override
    public void createCopyInvitationFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}