package com.fpt.gta.feature.manageplan.updateactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.feature.manageplan.documentactivity.ActivityDocumentActivity;
import com.fpt.gta.feature.searchautocomplete.PlaceAutoCompleteActivity;
import com.fpt.gta.presenter.UpdateActivityDayPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.UpdateActivityView;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UpdateAccomodationActivity extends AppCompatActivity implements UpdateActivityView {

    private EditText edtUpdateActivityType, edtUpdateAccomodationStartLocationName, edtUpdateAccomodationStartAddress, edtUpdateAccomodationStartDate, edtUpdateAccomodationEndDate, edtUpdateAccomodationStartTime, edtUpdateAccomodationEndTime, edtAccomodationName;
    private String accomodationName = "";
    private String placeStartID, timeStart = "", dateStart = "", activityStartAddress, activityStartLocationName = "";
    private String timeEnd = "", dateEnd = "";
    private Button btnUpdateAddActivity;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart, calendarEnd, calendarTimeStart, calendarTimeEnd;
    SimpleDateFormat simpleDateFormat, simpleTimeFormat;
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private ImageView imgUpdateActivityBack, imgUpdateActivityDocument;
    private ActivityDTO activityDTO;
    private final int UPDATE_ACTIVITY_STARTPLACE_REQUEST_CODE = 1111;
    private PlaceDTO place;
    private int planId;
    private Integer idType;
    private String day1, day2;
    private int idActivity;
    private UpdateActivityDayPresenter mUpdateActivityDayPresenter;
    private String dateGoTrip, dateEndTrip;
    private List<DocumentDTO> documentDTOList;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();

    private Integer idGroup, groupStatus;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;
    private String timezonePlaceStart;
    private String timezoneTrip;
    private Boolean isTooFarStart;
    private Integer idTrip;

    private String dateGoTripUTC, dateEndTripUTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_accomodation);
        initView();
        initData();
        initPlace();
    }

    public void initView() {
        imgUpdateActivityDocument = findViewById(R.id.imgUpdateActivityDocument);
        dateGoTrip = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPGO);
        dateEndTrip = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPEND);
        dateGoTripUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPGOUTC);
        dateEndTripUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPENDUTC);
        timezoneTrip = SharePreferenceUtils.getStringSharedPreference(this, GTABundle.TIMEZONETRIP);
        Bundle bundle = getIntent().getExtras();
        activityDTO = (ActivityDTO) bundle.getSerializable(GTABundle.UPDATE_ACTIVITY_DTO);
        activityDTOList = (List<ActivityDTO>) bundle.getSerializable(GTABundle.ACTIVITYLIST);

        groupStatus = (Integer) bundle.getSerializable(GTABundle.GROUPOBJECTSTATUS);


        documentDTOList = activityDTO.getDocumentList();
        edtAccomodationName = findViewById(R.id.edtUpdateAccomodationName);
        imgUpdateActivityBack = findViewById(R.id.imgAddUpdateAccomodationBack);

        edtUpdateAccomodationStartLocationName = findViewById(R.id.edtUpdateAccomodationStartLocationName);
        edtUpdateAccomodationStartAddress = findViewById(R.id.edtUpdateStartPlaceAccomodationAddress);
        edtUpdateAccomodationEndDate = findViewById(R.id.edtUpdateAccomodationEndDate);
        edtUpdateAccomodationEndTime = findViewById(R.id.edtUpdateAccomodationEndTime);
        edtUpdateAccomodationStartDate = findViewById(R.id.edtUpdateAccomodationStartDate);
        edtUpdateAccomodationStartTime = findViewById(R.id.edtUpdateAccomodationStartTime);
        edtUpdateActivityType = findViewById(R.id.edtUpdateActivityType);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");
        btnUpdateAddActivity = findViewById(R.id.btnUpdateAccomodation);
        mUpdateActivityDayPresenter = new UpdateActivityDayPresenter(this, this);
        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");

    }

    public void initData() {
        idTrip = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDTRIP);
        imgUpdateActivityDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateAccomodationActivity.this, ActivityDocumentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "UpdateActivityDayActivity");
                bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DOCUMENT, (Serializable) documentDTOList);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.UPDATE_ACTIVITY_DOCUMENT_CODE);
            }
        });

        idActivity = activityDTO.getId();
        idType = activityDTO.getIdType();
        edtUpdateActivityType.setText("Accommodation");

        isTooFarStart = activityDTO.getIsTooFar();


        timezonePlaceStart = activityDTO.getStartPlace().getTimeZone();
        accomodationName = activityDTO.getName();
        edtAccomodationName.setText(accomodationName);
        idActivity = activityDTO.getId();

        placeStartID = activityDTO.getStartPlace().getGooglePlaceId();
        activityStartLocationName = activityDTO.getStartPlace().getName();
        activityStartAddress = activityDTO.getStartPlace().getAddress();
        edtUpdateAccomodationStartLocationName.setText(activityStartLocationName);
        edtUpdateAccomodationStartAddress.setText(activityStartAddress);


        dateStart = ZonedDateTimeUtil.convertDateToStringASIA(activityDTO.getStartAt());
        timeStart = ZonedDateTimeUtil.convertTimeToString(activityDTO.getStartAt());


        dateEnd = ZonedDateTimeUtil.convertDateToStringASIA(activityDTO.getEndAt());
        timeEnd = ZonedDateTimeUtil.convertTimeToString(activityDTO.getEndAt());
        edtUpdateAccomodationEndTime.setText(timeEnd);
        edtUpdateAccomodationEndDate.setText(dateEnd);

        edtUpdateAccomodationStartDate.setText(dateStart);
        edtUpdateAccomodationStartTime.setText(timeStart);


        imgUpdateActivityBack.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                finish();
            }
        });


        edtUpdateAccomodationStartTime.setFocusable(false);
        edtUpdateAccomodationEndTime.setFocusable(false);
        edtUpdateAccomodationStartDate.setFocusable(false);
        edtUpdateAccomodationEndDate.setFocusable(false);
        edtUpdateActivityType.setFocusable(false);
        if (groupStatus.equals(GroupStatus.EXECUTING)) {
            edtAccomodationName.setFocusable(false);
            edtAccomodationName.setOnClickListener(null);
            edtUpdateAccomodationStartDate.setOnClickListener(null);
            edtUpdateAccomodationEndDate.setOnClickListener(null);
            edtUpdateAccomodationStartTime.setOnClickListener(null);
            edtUpdateAccomodationEndTime.setOnClickListener(null);
            edtUpdateAccomodationStartLocationName.setOnClickListener(null);
        } else {

            edtUpdateAccomodationStartLocationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UpdateAccomodationActivity.this, PlaceAutoCompleteActivity.class);
                    intent.putExtra(GTABundle.SEARCHTYPE, 2);
                    intent.putExtra(GTABundle.IDTRIP, idTrip);
                    startActivityForResult(intent, UPDATE_ACTIVITY_STARTPLACE_REQUEST_CODE);
                }
            });

            edtUpdateAccomodationStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTimeStart();
                }
            });


            edtUpdateAccomodationEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTimeEnd();
                }
            });


            edtUpdateAccomodationStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDateGo();
                }
            });


            edtUpdateAccomodationEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDateEnd();
                }
            });
        }


        btnUpdateAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickUpdateActivity();
            }
        });

    }


    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }

        edtUpdateAccomodationStartLocationName.setFocusable(false);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1111:
                onResultStartPlace(resultCode, data, GTABundle.RESUTLPLACEEDITTRIP);
                break;
            case GTABundle.UPDATE_ACTIVITY_DOCUMENT_CODE:
                onResultDocument(resultCode, data, GTABundle.UPDATE_ACTIVITY_DOCUMENT);
        }

    }


    public void onResultDocument(int resultCode, Intent data, String key) {
        if (resultCode == RESULT_OK) {
            documentDTOList = (List<DocumentDTO>) data.getSerializableExtra(GTABundle.UPDATE_ACTIVITY_DOCUMENT);
        }
    }


    public void onResultStartPlace(int resultCode, Intent data, String key) {
        if (resultCode == RESULT_OK) {
            place = (PlaceDTO) data.getSerializableExtra(key);
            edtUpdateAccomodationStartLocationName.setVisibility(View.VISIBLE);
            activityStartLocationName = place.getName();
            activityStartAddress = place.getAddress();
            placeStartID = place.getGooglePlaceId();
            edtUpdateAccomodationStartLocationName.setText(activityStartLocationName);
            edtUpdateAccomodationStartAddress.setText(activityStartAddress);
            timezonePlaceStart = place.getTimeZone();

        }
    }


    public void clickUpdateActivity() {
        try {
            TimeZone tz = TimeZone.getDefault();
            String timez = tz.getDisplayName();
            if (!checkNull() || !checkValidationSave(timeStart, timeEnd, dateStart, dateEnd, timez)) {
                return;
            } else {
                if (!checkActivityTime()) {
                    return;
                }
                planId = SharePreferenceUtils.getIntSharedPreference(UpdateAccomodationActivity.this, GTABundle.PLANID);
                mUpdateActivityDayPresenter.UpdateActivity(planId, getActivityResponseDTOFromForm());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private ActivityDTO getActivityResponseDTOFromForm() {
        accomodationName = edtAccomodationName.getText().toString();
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(idActivity);

        if (place != null) {
            isTooFarStart = place.getIsTooFar();
        }
        activityDTO.setIsTooFar(isTooFarStart);

        activityDTO.setName(accomodationName);
        activityDTO.setIdType(idType);
        activityDTO.setStartAt(ZonedDateTimeUtil.convertStringToDateOrTime(day1));
        activityDTO.setEndAt(ZonedDateTimeUtil.convertStringToDateOrTime(day2));
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setGooglePlaceId(placeStartID);
        activityDTO.setStartPlace(placeDTO);
        activityDTO.setDocumentList(documentDTOList);
        PlaceDTO placeDTO1 = new PlaceDTO();
        placeDTO1.setGooglePlaceId(placeStartID);
        activityDTO.setEndPlace(placeDTO1);
        return activityDTO;
    }

    public void getDateGo() {
        calendarStart = Calendar.getInstance();
        mDay = calendarStart.get(Calendar.DATE);
        mMonth = calendarStart.get(Calendar.MONTH);
        mYear = calendarStart.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int dayOfMonth,
                                          int monthOfYear, int year) {
                        calendarStart.set(dayOfMonth, monthOfYear, year);
                        dateStart = simpleDateFormat.format(calendarStart.getTime());
                        edtUpdateAccomodationStartDate.setText(dateStart);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTrip).getTime());
        datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTrip).getTime());
        datePickerDialog.show();
    }

    public boolean validDateStart() {
        String dateStart = edtUpdateAccomodationStartDate.getText().toString().trim();
        if (dateStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Pick Time start before");
            return false;
        } else {
            return true;
        }
    }


    public void getDateEnd() {
        if (!validDateStart()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please Pick TimeEnd");
            edtUpdateAccomodationEndDate.setText("");
            return;
        } else {
            calendarEnd = Calendar.getInstance();
            mDay = calendarEnd.get(Calendar.DATE);
            mMonth = calendarEnd.get(Calendar.MONTH);
            mYear = calendarEnd.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int dayOfMonth,
                                              int monthOfYear, int year) {
                            calendarEnd.set(dayOfMonth, monthOfYear, year);
                            dateEnd = simpleDateFormat.format(calendarEnd.getTime());
                            edtUpdateAccomodationEndDate.setText(dateEnd);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTrip).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTrip).getTime());
            datePickerDialog.show();
        }
    }

    public void getTimeStart() {
        calendarTimeStart = Calendar.getInstance();
        int hour = calendarTimeStart.get(Calendar.HOUR_OF_DAY);
        int minutes = calendarTimeStart.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendarTimeStart.set(Calendar.HOUR_OF_DAY, i);
                calendarTimeStart.set(Calendar.MINUTE, i1);
                timeStart = simpleTimeFormat.format(calendarTimeStart.getTime());
                edtUpdateAccomodationStartTime.setText(timeStart);
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }


    public void getTimeEnd() {
        calendarTimeEnd = Calendar.getInstance();
        int hour = calendarTimeEnd.get(Calendar.HOUR_OF_DAY);
        int minutes = calendarTimeEnd.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendarTimeEnd.set(Calendar.HOUR_OF_DAY, i);
                calendarTimeEnd.set(Calendar.MINUTE, i1);
                timeEnd = simpleTimeFormat.format(calendarTimeEnd.getTime());
                edtUpdateAccomodationEndTime.setText(timeEnd);
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }


    public boolean checkActivityTime() {
        Date date1 = ZonedDateTimeUtil.convertStringToDateOrTime(day1);
        Date date2 = ZonedDateTimeUtil.convertStringToDateOrTime(day2);
        for (int i = 0; i < activityDTOList.size(); i++) {
            if (activityDTOList.get(i).getId().equals(activityDTO.getId())) {
                activityDTOList.remove(i);
                break;
            }
        }


        Date dateStartTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTripUTC);
        Date convertedDateStartUTC = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateStartTripUtc, timezonePlaceStart);
        Date dateEndTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTripUTC);
        Date convertedDateEndUTC = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndTripUtc, timezonePlaceStart);
        if (date1.getTime() < convertedDateStartUTC.getTime() || date2.getTime() > convertedDateEndUTC.getTime()) {
            DialogShowErrorMessage.showValidationDialog(this, "Date Time! Out of the City");
            return false;
        }


        Date startUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date1, timezonePlaceStart);
        Date endUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date2, timezonePlaceStart);
        for (ActivityDTO activityDTO : activityDTOList) {
            Date stratActivity = activityDTO.getStartUtcAt();
            Date endActivity = activityDTO.getEndUtcAt();
            if (startUTC.getTime() < stratActivity.getTime()
                    && endUTC.getTime() < stratActivity.getTime()) {
            } else if (startUTC.getTime() > endActivity.getTime()
                    && endUTC.getTime() > endActivity.getTime()) {
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(Html.fromHtml("<font color='#FDD017'>This accommodation time  is coinciding with another accommodation time, do you still want to update?</font>"));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planId = SharePreferenceUtils.getIntSharedPreference(UpdateAccomodationActivity.this, GTABundle.PLANID);
                        mUpdateActivityDayPresenter.UpdateActivity(planId, getActivityResponseDTOFromForm());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        }
        return true;
    }


    private boolean checkValidationSave(String timeStart, String timeEnd, String
            dateStart, String dateEnd, String timeZone) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        day1 = dateStart + " " + timeStart;
        day2 = dateEnd + " " + timeEnd;
        Date date1 = simpleDateFormat.parse(day1);
        Date date2 = simpleDateFormat.parse(day2);

        Date startUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date1, timezonePlaceStart);
        Date endUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date2, timezonePlaceStart);
        long distanceTime = endUTC.getTime() - startUTC.getTime();
        long diffM = distanceTime / (60 * 1000);
        if (startUTC.after(endUTC)) {
            DialogShowErrorMessage.showValidationDialog(this, "Time start must be before end time");
            return false;
        } else if (diffM < 2) {
            DialogShowErrorMessage.showValidationDialog(this, "Accommodation is too short!");
            return false;
        }

        return true;

    }

    private boolean checkNull() {
        String activityNameUpdate = edtAccomodationName.getText().toString();
        if (activityNameUpdate.isEmpty() && activityNameUpdate.length() == 0) {
            DialogShowErrorMessage.showValidationDialog(this, "Please input accommodation Name");
            return false;
        } else if (!timezonePlaceStart.equals(timezoneTrip)) {
            DialogShowErrorMessage.showValidationDialog(this, "This place is in different time zone");
            return false;
        } else {
            return true;
        }
    }

    public void onclickUpdateActivity(ActivityDTO activityDTO) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DTO, (Serializable) activityDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void updateActivitySuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerActivity.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onclickUpdateActivity(getActivityResponseDTOFromForm());
        finish();
    }

    @Override
    public void updateActivityFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }


}