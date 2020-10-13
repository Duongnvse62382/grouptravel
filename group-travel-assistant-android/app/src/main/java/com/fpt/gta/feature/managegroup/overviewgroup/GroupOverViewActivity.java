package com.fpt.gta.feature.managegroup.overviewgroup;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.fpt.gta.BaseActivity;
import com.fpt.gta.MainActivity;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.LatLongDTO;
import com.fpt.gta.data.dto.constant.MemberStatus;
import com.fpt.gta.feature.managetrip.overviewtrip.TripOverviewActivity;
import com.fpt.gta.feature.managetrip.userjoin.UserJoinActivity;
import com.fpt.gta.feature.notifyactivity.ActivityNotifyActivity;
import com.fpt.gta.feature.profile.ProfileFragment;
import com.fpt.gta.presenter.PrintActivityByIdPresenter;
import com.fpt.gta.presenter.PrintGroupByIdPresenter;
import com.fpt.gta.presenter.PrintGroupPreviewPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintActivityByIdView;
import com.fpt.gta.view.PrintGroupByIdView;
import com.fpt.gta.view.PrintGroupPreviewView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import lombok.NonNull;


public class GroupOverViewActivity extends BaseActivity implements PrintGroupPreviewView, PrintActivityByIdView, PrintGroupByIdView {
    private ViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;
    private FragmentStatePagerItemAdapter mAdapter;
    private PrintGroupPreviewPresenter mPrintGroupPreviewPresenter;
    private PrintActivityByIdPresenter mPrintActivityByIdPresenter;
    private PrintGroupByIdPresenter mPrintGroupByIdPresenter;
    private String idGroup;
    private String inviteLink;
    private Integer idActivity, idGroupNotify, idGroupPlaning;
    private CurrencyDTO currencyDTO;
    private LatLongDTO latLongDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        initialView();
        getAtivityNotify();
        getJourneyDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            String token = task.getResult().getToken();
            Log.d("FCM", "token: " + token);
        });
        getFirebaseDynamicLinks();
    }


    private void initialView() {
        mPrintGroupPreviewPresenter = new PrintGroupPreviewPresenter(this, this);
        mPrintActivityByIdPresenter = new PrintActivityByIdPresenter(this, this);
        mPrintGroupByIdPresenter = new PrintGroupByIdPresenter(this, this);
        mViewPager = findViewById(R.id.viewpager);
        mSmartTabLayout = findViewById(R.id.view_pager_tab);
        initialPager();
    }

    private void getAtivityNotify() {
        try {
            idActivity = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDACTIVITYNOTIFY);
            idGroupNotify = (Integer) getIntent().getExtras().getSerializable(GTABundle.IDGROUPNOTIFY);

            if (idActivity != 0) {
                mPrintActivityByIdPresenter.getActivityById(idActivity);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getJourneyDetail() {
        try {
            idGroupPlaning = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUPPLANING);
            if (idGroupPlaning != 0) {
                mPrintGroupByIdPresenter.getGroupById(idGroupPlaning);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getFirebaseDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        if (deepLink != null) {
                            inviteLink = deepLink.getQueryParameter("test");
                            idGroup = deepLink.getQueryParameter("idgroup");
                            if (!inviteLink.isEmpty() && !idGroup.isEmpty()) {
                                mPrintGroupPreviewPresenter.printGroupPreview(idGroup);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("GroupOverActivity", "getDynamicLink:onFailure", e);
                    }
                });
    }


    private void initialPager() {
        final OverViewCustomTabAdapter mPagerAdapter = new OverViewCustomTabAdapter(GroupOverViewActivity.this);
        mSmartTabLayout.setCustomTabView(mPagerAdapter);
        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("Danh Sách Nhóm", GroupManageFragment.class));
        pages.add(FragmentPagerItem.of("Thông Tin Cá Nhân", ProfileFragment.class));
        mAdapter = new FragmentStatePagerItemAdapter(
                getSupportFragmentManager(), pages);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);

        mSmartTabLayout.setViewPager(mViewPager);
        ImageView icon = mSmartTabLayout.getTabAt(0).findViewById(R.id.activity_main_tab_icon);
        icon.setColorFilter(Color.parseColor("#426967"));
        mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ImageView icon = mSmartTabLayout.getTabAt(0).findViewById(R.id.activity_main_tab_icon);
                ImageView icon1 = mSmartTabLayout.getTabAt(1).findViewById(R.id.activity_main_tab_icon);
                if (position == 0) {
                    icon.setColorFilter(Color.parseColor("#56a8a2"));
                    icon1.setColorFilter(Color.parseColor("#979797"));
                } else if (position == 1) {
                    icon.setColorFilter(Color.parseColor("#979797"));
                    icon1.setColorFilter(Color.parseColor("#56a8a2"));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    public void SendDateGo(String dateGo) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.DATEGO, dateGo);
    }

    public void SendLatLong(String latlong) {
        SharePreferenceUtils.saveStringSharedPreference(this, "LATLONGDTO", latlong);
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


    @Override
    public void printGroupPreviewSucess(GroupResponseDTO groupResponseDTO) {
        if (!inviteLink.equals(groupResponseDTO.getInvitationCode())) {
            DialogShowErrorMessage.showValidationDialog(this, "Invalid Link");
            return;
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
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDGROUP, groupResponseDTO.getId());

        currencyDTO = groupResponseDTO.getCurrency();
        SendGroupCurrency(currencyDTO);
        String journeyDTOS = gson.toJson(groupResponseDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.JOURNEYOJECT, journeyDTOS);
        Integer groupStatus = groupResponseDTO.getIdStatus();
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.GROUPOBJECTSTATUS, groupStatus);

        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int i;
        Boolean isJourney = false;
        try {
            for (i = 0; i < groupResponseDTO.getMemberList().size(); i++) {
                {
                    String member = groupResponseDTO.getMemberList().get(i).getPerson().getFirebaseUid();
                    if (idFribase.equals(member) && groupResponseDTO.getMemberList().get(i).getIdStatus().equals(MemberStatus.ACTIVE)) {
                        isJourney = true;
                        break;
                    } else {
                        isJourney = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (isJourney.equals(true)) {
            Intent intent1 = new Intent(this, TripOverviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(GTABundle.KEYGROUP, groupResponseDTO);
            intent1.putExtras(bundle);
            startActivity(intent1);
        } else {
            Intent intent = new Intent(this, UserJoinActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(GTABundle.KEYGROUP, groupResponseDTO);
            Gson gson1 = new Gson();
            String jouneryInfo = gson1.toJson(groupResponseDTO);
            SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.JOURNEYGSON, jouneryInfo);
            CurrencyDTO currencyDTO = groupResponseDTO.getCurrency();
            String currency = gson.toJson(currencyDTO);
            SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.CURRENCYGSON, currency);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void printGroupPreviewFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }

    @Override
    public void printActivityById(ActivityDTO activityDTO) {
        SharePreferenceUtils.removeIntSharedPreference(this, GTABundle.IDACTIVITYNOTIFY);
        Intent intent = new Intent(this, ActivityNotifyActivity.class);
        intent.putExtra(GTABundle.IDGROUPNOTIFY, idGroupNotify);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ACTIVITYNOTIFY, activityDTO);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void printActivityByIdFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }


    public void SendGroupCurrency(CurrencyDTO currencyDTO) {
        Gson gson = new Gson();
        String groupCurrency = gson.toJson(currencyDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.GROUP_CURRENCY_SHARE, groupCurrency);
    }

    public void onGroupClick(GroupResponseDTO groupResponseDTO) {
        Intent intent1 = new Intent(this, TripOverviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYGROUP, groupResponseDTO);
        intent1.putExtras(bundle);
        startActivity(intent1);
    }

    @Override
    public void printGroupByIdSS(GroupResponseDTO groupResponseDTO) {
        SharePreferenceUtils.removeIntSharedPreference(this, GTABundle.IDGROUPPLANING);
        onGroupClick(groupResponseDTO);
        String timeZoneStart = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getStartAt());
        String timeZoneEnd = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getEndAt());
        String timeZoneStartUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getStartUtcAt());
        String timeZoneEndUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getEndUtcAt());
        String timezone = groupResponseDTO.getStartPlace().getTimeZone();
        latLongDTO = new LatLongDTO();
        latLongDTO.setPlaceName(groupResponseDTO.getStartPlace().getName());
        latLongDTO.setLattitude(groupResponseDTO.getStartPlace().getLatitude().doubleValue());
        latLongDTO.setLongtitue(groupResponseDTO.getStartPlace().getLongitude().doubleValue());
        Gson gson = new Gson();
        String json = gson.toJson(latLongDTO);
        SendLatLong(json);
        SendDateGo(timeZoneStart);
        SendDateEnd(timeZoneEnd);
        SendDateGoUTC(timeZoneStartUTC);
        SendDateEndUTC(timeZoneEndUTC);
        SendTimeZone(timezone);
        SendData(groupResponseDTO.getId());
        currencyDTO = groupResponseDTO.getCurrency();
        SendGroupCurrency(currencyDTO);
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDGROUP, groupResponseDTO.getId());
        String journeyDTOS = gson.toJson(groupResponseDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.JOURNEYOJECT, journeyDTOS);
        Integer groupStatus = groupResponseDTO.getIdStatus();
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.GROUPOBJECTSTATUS, groupStatus);
    }

    @Override
    public void printGroupByIdFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}
