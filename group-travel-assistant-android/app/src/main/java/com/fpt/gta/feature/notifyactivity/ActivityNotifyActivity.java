package com.fpt.gta.feature.notifyactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.LatLongDTO;
import com.fpt.gta.feature.managetrip.overviewtrip.TripOverviewActivity;
import com.fpt.gta.presenter.PrintActivityByIdPresenter;
import com.fpt.gta.presenter.PrintGroupByIdPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintActivityByIdView;
import com.fpt.gta.view.PrintGroupByIdView;
import com.google.gson.Gson;

public class ActivityNotifyActivity extends AppCompatActivity implements View.OnClickListener, PrintGroupByIdView {


    private TextView txtAcivityType, txtNameActivity, txtPlaceStartActivity, txtAddressActivityStart, txtEndPlaceActivity, txtAddressActivityEnd, txtTimeStartActivity, txtTimeEndActivity, txtTimeStartActivityNotSame, txtTimeEndActivityNotSame;
    private String nameActivity, placeStartActivity, addressActivityStart, endPlaceActivity, addressActivityEnd;
    private Integer activityType, idActivity, idGroup;
    private String timeStartActivity, timeEndActivity;
    private ImageView imgActivityDetailBack, imgPlaceStart, imgPlaceEnd, imgNextStart, imgNextEnd;
    private LinearLayout lnlEndPlace, lnlEndPlaceAddress;
    private ActivityDTO activityDTO;
    private String imgStartActivity, imgEndActivity;
    private PrintGroupByIdPresenter mPrintGroupByIdPresenter;
    private LatLongDTO latLongDTO;
    private CurrencyDTO currencyDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onBackPressed() {
        mPrintGroupByIdPresenter.getGroupById(idGroup);
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
        imgPlaceStart = findViewById(R.id.imgPlaceStart);
        imgPlaceEnd = findViewById(R.id.imgPlaceEnd);
        imgNextStart = findViewById(R.id.imgNextStart);
        imgNextEnd = findViewById(R.id.imgNextEnd);
        lnlEndPlace = findViewById(R.id.lnlEndPlaceA);
        lnlEndPlace.setVisibility(View.GONE);
        txtNameActivity.setSelected(true);
        Bundle bundle = getIntent().getExtras();
        activityDTO = (ActivityDTO) bundle.getSerializable(GTABundle.ACTIVITYNOTIFY);
        idGroup = (Integer) getIntent().getExtras().getSerializable(GTABundle.IDGROUPNOTIFY);
    }

    public void initData() {
        mPrintGroupByIdPresenter = new PrintGroupByIdPresenter(this, this);
        imgActivityDetailBack.setOnClickListener(this::onClick);
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
        goneWithSamePlace();
    }

    public void goneWithSamePlace() {
        String idGoogleStart = activityDTO.getStartPlace().getGooglePlaceId();
        String idGoogleEnd = activityDTO.getEndPlace().getGooglePlaceId();
        if (idGoogleStart.equals(idGoogleEnd)) {
            lnlEndPlace.setVisibility(View.GONE);
        }else {
            lnlEndPlace.setVisibility(View.VISIBLE);
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
                mPrintGroupByIdPresenter.getGroupById(idGroup);
                break;
        }
    }



    public void onGroupClick(GroupResponseDTO groupResponseDTO) {
        Intent intent1 = new Intent(this, TripOverviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYGROUP, groupResponseDTO);
        intent1.putExtras(bundle);
        startActivity(intent1);
    }

    public void SendDateGo(String dateGo) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.DATEGO, dateGo);
    }
    public void SendDateEnd(String dateEnd) {
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.DATEEND, dateEnd);
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

    public void SendLatLong(String latlong) {
        SharePreferenceUtils.saveStringSharedPreference(this, "LATLONGDTO", latlong);
    }

    public void SendGroupCurrency(CurrencyDTO currencyDTO) {
        Gson gson = new Gson();
        String groupCurrency = gson.toJson(currencyDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.GROUP_CURRENCY_SHARE, groupCurrency);
    }

    public void SendData(int id) {
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDGROUP, id);
    }


    @Override
    public void printGroupByIdSS(GroupResponseDTO groupResponseDTO) {
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
        finish();
    }

    @Override
    public void printGroupByIdFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}