package com.fpt.gta.feature.manageplan.plansuggested;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.ZonedDateTimeUtil;

public class CalculatedPlaceDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView txtNameActivity, txtPlaceStartActivity, txtAddressActivityStart, txtEndPlaceActivity, txtAddressActivityEnd, txtTimeStartActivity, txtTimeEndActivity, txtTimeStartActivityNotSame, txtTimeEndActivityNotSame;
    private String nameActivity, placeStartActivity, addressActivityStart, endPlaceActivity, addressActivityEnd;
    private String timeStartActivity, timeEndActivity;
    private ImageView imgActivityDetailBack, imgPlaceStart, imgPlaceEnd, imgNextStart, imgNextEnd;
    private LinearLayout lnlEndPlace;
    private ActivityDTO activityDTO;
    private String imgStartActivity, imgEndActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculated_place_detail);
        initView();
        initData();
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
        txtNameActivity.setSelected(true);

    }

    public void initData() {
        Bundle bundle = getIntent().getExtras();
        activityDTO = (ActivityDTO) bundle.getSerializable(GTABundle.UPDATE_ACTIVITY_DTO);
        imgActivityDetailBack.setOnClickListener(this::onClick);
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
        }
    }



}