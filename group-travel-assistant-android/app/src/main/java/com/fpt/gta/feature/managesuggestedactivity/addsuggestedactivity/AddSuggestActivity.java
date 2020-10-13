package com.fpt.gta.feature.managesuggestedactivity.addsuggestedactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.feature.searchautocomplete.PlaceAutoCompleteActivity;
import com.fpt.gta.presenter.CreateSuggestedActivityPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateSuggestedActivityView;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.TimeZone;

public class AddSuggestActivity extends AppCompatActivity implements View.OnClickListener, CreateSuggestedActivityView {

    private ImageView imgBack;
    private EditText editSearchStartPlace, editSearchEndPlace, editNameSuggested, edtTypeSuggested;
    private LinearLayout lnlPlaceEnd;
    private Button btnSubmit;
    private ImageView imgAddPlace, imgSubPlace;
    private LinearLayout lnlAddPlaceSuggetsed, lnlSubPlaceSuggetsed;
    private String placeIDStart, placeIdEnd;
    private CreateSuggestedActivityPresenter mCreateSuggestedActivityPresenter;
    private Integer idTrip;
    private PlaceDTO place;
    private String namePlaceStart = "", nameEndPlace = "PlaceEnd";
    private String activityType = "";
    private Integer idType;
    private Integer idGroup;
    private FirebaseDatabase databaseSuggested;
    private DatabaseReference listenerSuggested;

    private String timezonePlaceStart, timezonePlaceEnd;
    private String timezoneTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_suggest);
        initview();
        initPlace();
        initData();
    }


    public void initview() {
        imgBack = findViewById(R.id.imgBack);
        editSearchStartPlace = findViewById(R.id.editSearchStartPlace);
        editSearchEndPlace = findViewById(R.id.editSearchEndPlace);
        editNameSuggested = findViewById(R.id.editNameSuggested);
        btnSubmit = findViewById(R.id.btnSubmitCreateSuggested);
        imgAddPlace = findViewById(R.id.imgAddPlace);
        imgSubPlace = findViewById(R.id.imgSubPlace);
        lnlPlaceEnd = findViewById(R.id.lnlPlaceEnd);
        edtTypeSuggested = findViewById(R.id.edtTypeSuggested);
        lnlAddPlaceSuggetsed = findViewById(R.id.lnlAddPlaceSuggetsed);
        lnlSubPlaceSuggetsed = findViewById(R.id.lnlSubPlaceSuggetsed);
        lnlAddPlaceSuggetsed.setVisibility(View.GONE);
        lnlSubPlaceSuggetsed.setVisibility(View.GONE);
        lnlPlaceEnd.setVisibility(View.GONE);
        idTrip = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDTRIP);
        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseSuggested = FirebaseDatabase.getInstance();
        listenerSuggested = databaseSuggested.getReference(String.valueOf(idGroup)).child("listener").child("reloadSuggestedPlace");
        edtTypeSuggested.setText("Activity");
        idType = 1;
        timezoneTrip = SharePreferenceUtils.getStringSharedPreference(this, GTABundle.TIMEZONETRIP);

    }

    public void initData() {
        imgBack.setOnClickListener(this::onClick);
        btnSubmit.setOnClickListener(this::onClick);
        imgAddPlace.setOnClickListener(this::onClick);
        imgSubPlace.setOnClickListener(this::onClick);
    }

    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }
        editSearchStartPlace.setFocusable(false);
        editSearchStartPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddSuggestActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 2);
                intent.putExtra(GTABundle.IDTRIP , idTrip);
                startActivityForResult(intent, 200);
            }

        });


        editSearchEndPlace.setFocusable(false);
        editSearchEndPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEnd = new Intent(AddSuggestActivity.this, PlaceAutoCompleteActivity.class);
                intentEnd.putExtra(GTABundle.SEARCHTYPE, 2);
                intentEnd.putExtra(GTABundle.IDTRIP , idTrip);
                startActivityForResult(intentEnd, 100);
            }
        });

        edtTypeSuggested.setFocusable(false);
    }

    public void onStartPlaceData(Intent intent) {
        place = (PlaceDTO) intent.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
        namePlaceStart = place.getName();
        editSearchStartPlace.setText(namePlaceStart);
        placeIDStart = place.getGooglePlaceId();
        timezonePlaceStart = place.getTimeZone();
    }


    public void onEndPlaceData(Intent intent) {
        place = (PlaceDTO) intent.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
        nameEndPlace = place.getName();
        editSearchEndPlace.setText(nameEndPlace);
        placeIdEnd = place.getGooglePlaceId();
        timezonePlaceEnd = place.getTimeZone();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            onStartPlaceData(data);
        } else if (requestCode == 100 && resultCode == RESULT_OK) {
            onEndPlaceData(data);
        } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
        }

    }

    public void clickCreateSuggestedActivity() {
        if (!checkNull()) {
            return;
        } else {
            mCreateSuggestedActivityPresenter = new CreateSuggestedActivityPresenter(this, AddSuggestActivity.this);
            mCreateSuggestedActivityPresenter.createSuggestActivity(idTrip, getSuggestedFromFrom());
        }

    }



    public SuggestedActivityResponseDTO getSuggestedFromFrom() {
        String name = editNameSuggested.getText().toString();
        SuggestedActivityResponseDTO suggestedActivityResponseDTO = new SuggestedActivityResponseDTO();
        suggestedActivityResponseDTO.setName(name);

        Boolean isTooFarStart = place.getIsTooFar();
        suggestedActivityResponseDTO.setIsTooFar(isTooFarStart);

        suggestedActivityResponseDTO.setIdType(idType);
        PlaceDTO placeDTOStart = new PlaceDTO();
        placeDTOStart.setGooglePlaceId(placeIDStart);
        suggestedActivityResponseDTO.setStartPlace(placeDTOStart);
        PlaceDTO placeDTOEnd = new PlaceDTO();
        if (placeIdEnd == null) {
            placeDTOEnd.setGooglePlaceId(placeIDStart);
        } else {
            placeDTOEnd.setGooglePlaceId(placeIdEnd);
        }

        suggestedActivityResponseDTO.setEndPlace(placeDTOEnd);

        return suggestedActivityResponseDTO;
    }


    private boolean checkNull() {
        String nameSuggested = editNameSuggested.getText().toString().trim();
        String namePlaceStart = editSearchStartPlace.getText().toString().trim();
        if(timezonePlaceEnd == null){
            timezonePlaceEnd = timezonePlaceStart;
        }
        if (nameSuggested.length() == 0 && nameSuggested.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please input Name Suggested");
            return false;
        } else if (namePlaceStart.length() == 0 && namePlaceStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please choose place");
            return false;
        } else if (nameEndPlace.length() == 0 && nameEndPlace == "") {
            DialogShowErrorMessage.showValidationDialog(this, "Please choose place end");
            return false;
        }
        else if(!timezonePlaceStart.equals(timezoneTrip)){
            DialogShowErrorMessage.showValidationDialog(this, "This place is in different time zone");
            return false;
        }

        else {
            return true;
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSubmitCreateSuggested:
                clickCreateSuggestedActivity();
                break;
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgSubPlace:
                lnlPlaceEnd.setVisibility(View.GONE);
                lnlAddPlaceSuggetsed.setVisibility(View.VISIBLE);
                lnlSubPlaceSuggetsed.setVisibility(View.GONE);
                placeIdEnd = null;
                nameEndPlace = "PlaceEnd";
                timezonePlaceEnd = null;
                break;

            case R.id.imgAddPlace:
                lnlPlaceEnd.setVisibility(View.VISIBLE);
                nameEndPlace = "";
                editSearchEndPlace.setText("");
                lnlAddPlaceSuggetsed.setVisibility(View.GONE);
                lnlSubPlaceSuggetsed.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void createSuggestSuccess(String messageSuccess) {

        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerSuggested.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void createSuggestFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}