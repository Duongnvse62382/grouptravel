package com.fpt.gta.feature.managesuggestedactivity.editsuggestedactivity;

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
import com.fpt.gta.presenter.UpdateSuggestedActivityPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.UpdateSuggestedActivityView;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.TimeZone;

public class EditSuggestedActivity extends AppCompatActivity implements View.OnClickListener, UpdateSuggestedActivityView {

    private UpdateSuggestedActivityPresenter mUpdateSuggestedActivityPresenter;
    private ImageView imgBack;
    private EditText editSearchStartPlace, editSearchEndPlace, editNameSuggested, edtTypeSuggested;
    private Button btnSubmit;
    private String nameSuggested, namePlaceStart, namePlaceEnd;
    private String placeIDStart, placeIdEnd;
    private SuggestedActivityResponseDTO mSuggestedDTO;
    private Integer idSuggested;
    private Integer idTrip;
    private PlaceDTO place;
    private Boolean isTooFarStart;
    private ImageView imgAddPlace, imgSubPlace;
    private LinearLayout lnlAddPlaceSuggetsed, lnlSubPlaceSuggetsed;
    private LinearLayout lnlEndPlace;
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
        setContentView(R.layout.activity_edit_suggested);
        initview();
        initData();
        initPlace();
    }

    public void initview() {
        imgBack = findViewById(R.id.imgBack);
        imgAddPlace = findViewById(R.id.imgAddPlace);
        imgSubPlace = findViewById(R.id.imgSubPlace);
        lnlEndPlace = findViewById(R.id.lnlEndPlace);
        lnlAddPlaceSuggetsed = findViewById(R.id.lnlAddPlaceSuggetsed);
        lnlSubPlaceSuggetsed = findViewById(R.id.lnlSubPlaceSuggetsed);
        editSearchStartPlace = findViewById(R.id.editSearchStartPlaceEdit);
        editSearchEndPlace = findViewById(R.id.editSearchEndPlaceEdit);
        editNameSuggested = findViewById(R.id.editNameSuggestedEdit);
        btnSubmit = findViewById(R.id.btnSubmitUpdateSuggested);
        edtTypeSuggested = findViewById(R.id.edtTypeSuggestedEdit);
        Bundle bundle = getIntent().getExtras();
        mSuggestedDTO = (SuggestedActivityResponseDTO) bundle.getSerializable(GTABundle.KEYSUGGESTEDACTIVITY);

        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseSuggested = FirebaseDatabase.getInstance();
        listenerSuggested = databaseSuggested.getReference(String.valueOf(idGroup)).child("listener").child("reloadSuggestedPlace");
        timezoneTrip = SharePreferenceUtils.getStringSharedPreference(this, GTABundle.TIMEZONETRIP);
    }

    public void initData() {
        imgBack.setOnClickListener(this::onClick);
        btnSubmit.setOnClickListener(this::onClick);
        imgAddPlace.setOnClickListener(this::onClick);
        imgSubPlace.setOnClickListener(this::onClick);
        edtTypeSuggested.setFocusable(false);
        edtTypeSuggested.setOnClickListener(this::onClick);

        idTrip = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDTRIP);
        nameSuggested = mSuggestedDTO.getName();
        namePlaceStart = mSuggestedDTO.getStartPlace().getName();
        idSuggested = mSuggestedDTO.getId();

        idType = mSuggestedDTO.getIdType();
        edtTypeSuggested.setText("Activity");

        isTooFarStart = mSuggestedDTO.getIsTooFar();


        timezonePlaceStart = mSuggestedDTO.getStartPlace().getTimeZone();
        timezonePlaceEnd = mSuggestedDTO.getEndPlace().getTimeZone();

        editNameSuggested.setText(nameSuggested);
        editSearchStartPlace.setText(namePlaceStart);
        placeIDStart = mSuggestedDTO.getStartPlace().getGooglePlaceId();
        placeIdEnd = mSuggestedDTO.getEndPlace().getGooglePlaceId();
        namePlaceEnd = mSuggestedDTO.getEndPlace().getName();

        lnlAddPlaceSuggetsed.setVisibility(View.GONE);
        editSearchEndPlace.setText(namePlaceEnd);
        if (placeIDStart.equals(placeIdEnd)) {
            lnlEndPlace.setVisibility(View.GONE);
            lnlSubPlaceSuggetsed.setVisibility(View.GONE);
            placeIdEnd = null;
        } else {
            lnlAddPlaceSuggetsed.setVisibility(View.GONE);
        }
        editSearchEndPlace.setText(namePlaceEnd);

    }

    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }
        editSearchStartPlace.setFocusable(false);
        editSearchEndPlace.setFocusable(false);
        editSearchStartPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSuggestedActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 2);
                intent.putExtra(GTABundle.IDTRIP , idTrip);
                startActivityForResult(intent, 200);
            }
        });
        editSearchEndPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSuggestedActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 2);
                intent.putExtra(GTABundle.IDTRIP , idTrip);
                startActivityForResult(intent, 100);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK) {
            place = (PlaceDTO) data.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
            namePlaceStart = place.getName();
            editSearchStartPlace.setText(namePlaceStart);
            placeIDStart = place.getGooglePlaceId();
            timezonePlaceStart = place.getTimeZone();

        } else if (requestCode == 100 && resultCode == RESULT_OK) {
            place = (PlaceDTO) data.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
            namePlaceEnd = place.getName();
            editSearchEndPlace.setText(namePlaceEnd);
            placeIdEnd = place.getGooglePlaceId();
            timezonePlaceEnd = place.getTimeZone();
        } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
        }
    }

    public void clickEditSuggested() {
        if (!checkNull()) {
            return;
        } else {
            mUpdateSuggestedActivityPresenter = new UpdateSuggestedActivityPresenter(this, this);
            mUpdateSuggestedActivityPresenter.updateSuggestedActivity(idTrip, getSuggestedFromFrom());
        }


    }




    private boolean checkNull() {
        String nameSuggested = editNameSuggested.getText().toString().trim();
        if (nameSuggested.length() == 0 && nameSuggested.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please input Name Suggested");
            return false;
        } else if (namePlaceEnd.length() == 0 && namePlaceEnd == "") {
            DialogShowErrorMessage.showValidationDialog(this, "Please choose place end");
            return false;
        } else if (!timezonePlaceStart.equals(timezoneTrip)) {
            DialogShowErrorMessage.showValidationDialog(this, "This place is in different time zone");
            return false;
        } else {
            return true;
        }
    }

    public SuggestedActivityResponseDTO getSuggestedFromFrom() {
        nameSuggested = editNameSuggested.getText().toString();
        SuggestedActivityResponseDTO suggestedActivityResponseDTO = new SuggestedActivityResponseDTO();

        if(place != null){
            isTooFarStart = place.getIsTooFar();
        }
        suggestedActivityResponseDTO.setIsTooFar(isTooFarStart);

        suggestedActivityResponseDTO.setId(idSuggested);
        suggestedActivityResponseDTO.setName(nameSuggested);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgBack:
                finish();
                break;

            case R.id.btnSubmitUpdateSuggested:
                clickEditSuggested();
                break;
            case R.id.imgAddPlace:
                lnlEndPlace.setVisibility(View.VISIBLE);
                lnlAddPlaceSuggetsed.setVisibility(View.GONE);
                lnlSubPlaceSuggetsed.setVisibility(View.VISIBLE);
                placeIdEnd = mSuggestedDTO.getEndPlace().getGooglePlaceId();
                namePlaceEnd = mSuggestedDTO.getEndPlace().getName();
                namePlaceEnd = "";
                editSearchEndPlace.setText("");
                break;
            case R.id.imgSubPlace:
                lnlEndPlace.setVisibility(View.GONE);
                lnlAddPlaceSuggetsed.setVisibility(View.VISIBLE);
                lnlSubPlaceSuggetsed.setVisibility(View.GONE);
                namePlaceEnd = "PlaceEnd";
                placeIdEnd = null;
                timezonePlaceEnd = null;
                editSearchEndPlace.setText(namePlaceEnd);
                break;

        }
    }

    @Override
    public void updateSuggestedSuccess(String messageSuccess) {
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
    public void updateSuggestedFail(String mesage) {
        DialogShowErrorMessage.showValidationDialog(this, mesage);
    }
}