package com.fpt.gta.feature.manageplan.updateactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class UpdateTransportationActivity extends AppCompatActivity implements UpdateActivityView {
    private EditText edtUpdateActivityEndAddress, edtUpdateActivityEndLocationName, edtUpdateActivityType, edtUpdateActivityStartLocationName, edtUpdateActivityStartAddress, edtUpdateActivityStartDate, edtUpdateActivityEndDate, edtUpdateActivityStartTime, edtUpdateActivityEndTime, edtActivityName;
    private String activityName = "";
    private String placeStartID, timeStart = "", dateStart = "", activityStartAddress, activityStartLocationName = "";
    private String placeEndID, timeEnd = "", dateEnd = "", activityEndLocationName = "", activityEndAddress;
    private Button btnUpdateAddActivity;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart, calendarEnd, calendarTimeStart, calendarTimeEnd;
    SimpleDateFormat simpleDateFormat, simpleTimeFormat;
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private ImageView imgUpdateActivityBack, imgUpdateActivityDocument;
    private ActivityDTO activityDTO;
    private final int UPDATE_ACTIVITY_STARTPLACE_REQUEST_CODE = 1111;
    private final int UPDATE_ACTIVITY_ENDPLACE_REQUEST_CODE = 1112;
    private PlaceDTO place, placeEnd;
    private int planId;
    private Integer idType;
    private String day1, day2;
    private int idActivity;
    private UpdateActivityDayPresenter mUpdateActivityDayPresenter;
    private String dateGoTrip, dateEndTrip;
    private String dateGoJourney, dateEndJourney;
    private List<DocumentDTO> documentDTOList;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();
    private String dateGoJourneyUTC, dateEndJourneyUTC;
    private Integer idGroup, groupStatus;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;
    private String timezonePlaceStart, timezonePlaceEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transportation);
        initView();
        initData();
        initPlace();
    }

    public void initView() {
        imgUpdateActivityDocument = findViewById(R.id.imgUpdateActivityDocument);
        dateGoTrip = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPGO);
        dateEndTrip = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPEND);

        dateGoJourney = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEGO);
        dateEndJourney = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEEND);
        dateGoJourneyUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEGOUTC);
        dateEndJourneyUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEENDUTC);

        Bundle bundle = getIntent().getExtras();
        activityDTO = (ActivityDTO) bundle.getSerializable(GTABundle.UPDATE_ACTIVITY_DTO);
        activityDTOList = (List<ActivityDTO>) bundle.getSerializable(GTABundle.ACTIVITYLIST);
        groupStatus = (Integer) bundle.getSerializable(GTABundle.GROUPOBJECTSTATUS);

        documentDTOList = activityDTO.getDocumentList();
        edtActivityName = findViewById(R.id.edtUpdateActivityName);
        imgUpdateActivityBack = findViewById(R.id.imgAddUpdateActivityBack);
        edtUpdateActivityStartLocationName = findViewById(R.id.edtUpdateActivityStartLocationName);
        edtUpdateActivityEndLocationName = findViewById(R.id.edtUpdateActivityEndLocationName);
        edtUpdateActivityStartAddress = findViewById(R.id.edtUpdateStartPlaceActivityAddress);
        edtUpdateActivityEndAddress = findViewById(R.id.edtUpdateEndActivityAddress);
        edtUpdateActivityEndDate = findViewById(R.id.edtUpdateActivityEndDate);
        edtUpdateActivityEndTime = findViewById(R.id.edtUpdateActivityEndTime);
        edtUpdateActivityStartDate = findViewById(R.id.edtUpdateActivityStartDate);
        edtUpdateActivityStartTime = findViewById(R.id.edtUpdateActivityStartTime);
        edtUpdateActivityType = findViewById(R.id.edtUpdateActivityType);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");
        btnUpdateAddActivity = findViewById(R.id.btnUpdateActivity);
        mUpdateActivityDayPresenter = new UpdateActivityDayPresenter(this, this);

        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");

    }

    public void initData() {
        imgUpdateActivityDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateTransportationActivity.this, ActivityDocumentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "UpdateActivityDayActivity");
                bundle.putSerializable(GTABundle.UPDATE_ACTIVITY_DOCUMENT, (Serializable) documentDTOList);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.UPDATE_ACTIVITY_DOCUMENT_CODE);
            }
        });
        idActivity = activityDTO.getId();
        idType = activityDTO.getIdType();
        if (idType == 1) {
            edtUpdateActivityType.setText("Activity");
        } else if (idType == 2) {
            edtUpdateActivityType.setText("Transportation");
        } else {
            edtUpdateActivityType.setText("Accomodation");
        }

        timezonePlaceStart = activityDTO.getStartPlace().getTimeZone();
        timezonePlaceEnd = activityDTO.getEndPlace().getTimeZone();

        activityName = activityDTO.getName();
        edtActivityName.setText(activityName);
        idActivity = activityDTO.getId();

        place = activityDTO.getStartPlace();
        placeStartID = activityDTO.getStartPlace().getGooglePlaceId();
        activityStartLocationName = activityDTO.getStartPlace().getName();
        activityStartAddress = activityDTO.getStartPlace().getAddress();
        edtUpdateActivityStartLocationName.setText(activityStartLocationName);
        edtUpdateActivityStartAddress.setText(activityStartAddress);

        placeEnd = activityDTO.getEndPlace();
        placeEndID = activityDTO.getEndPlace().getGooglePlaceId();
        activityEndLocationName = activityDTO.getEndPlace().getName();
        activityEndAddress = activityDTO.getEndPlace().getAddress();
        edtUpdateActivityEndLocationName.setText(activityEndLocationName);
        edtUpdateActivityEndAddress.setText(activityEndAddress);

        dateStart = ZonedDateTimeUtil.convertDateToStringASIA(activityDTO.getStartAt());
        timeStart = ZonedDateTimeUtil.convertTimeToString(activityDTO.getStartAt());

        try {
            dateEnd = ZonedDateTimeUtil.convertDateToStringASIA(activityDTO.getEndAt());
            timeEnd = ZonedDateTimeUtil.convertTimeToString(activityDTO.getEndAt());
            edtUpdateActivityEndTime.setText(timeEnd);
            edtUpdateActivityEndDate.setText(dateEnd);
        }catch (Exception e){
            e.printStackTrace();
        }
        edtUpdateActivityStartDate.setText(dateStart);
        edtUpdateActivityStartTime.setText(timeStart);


        imgUpdateActivityBack.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                finish();
            }
        });


        edtUpdateActivityStartTime.setFocusable(false);
        edtUpdateActivityEndTime.setFocusable(false);
        edtUpdateActivityStartDate.setFocusable(false);
        edtUpdateActivityEndDate.setFocusable(false);
        if (groupStatus.equals(GroupStatus.EXECUTING)) {
            edtActivityName.setFocusable(false);
            edtUpdateActivityStartLocationName.setOnClickListener(null);
            edtUpdateActivityEndLocationName.setOnClickListener(null);
            edtUpdateActivityEndDate.setOnClickListener(null);
            edtUpdateActivityEndTime.setOnClickListener(null);
            edtUpdateActivityStartTime.setOnClickListener(null);
            edtUpdateActivityStartDate.setOnClickListener(null);
        } else{

            edtUpdateActivityEndLocationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UpdateTransportationActivity.this, PlaceAutoCompleteActivity.class);
                    intent.putExtra(GTABundle.SEARCHTYPE, 2);
                    startActivityForResult(intent, UPDATE_ACTIVITY_ENDPLACE_REQUEST_CODE);
                }

            });

            edtUpdateActivityStartLocationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UpdateTransportationActivity.this, PlaceAutoCompleteActivity.class);
                    intent.putExtra(GTABundle.SEARCHTYPE, 2);
                    startActivityForResult(intent, UPDATE_ACTIVITY_STARTPLACE_REQUEST_CODE);
                }
            });


            edtUpdateActivityStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTimeStart();
                }
            });


            edtUpdateActivityEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTimeEnd();
                }
            });


            edtUpdateActivityStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDateGo();
                }
            });


            edtUpdateActivityEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDateEnd();
                }
            });
        }



        edtUpdateActivityType.setFocusable(false);


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
        edtUpdateActivityStartLocationName.setFocusable(false);

        edtUpdateActivityEndLocationName.setFocusable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1111:
                onResultStartPlace(resultCode, data, GTABundle.RESUTLPLACEEDITTRIP);
                break;
            case 1112:
                onResultPlace(resultCode, data, GTABundle.RESUTLPLACEEDITTRIP);
                break;
            case GTABundle.UPDATE_ACTIVITY_DOCUMENT_CODE:
                onResultDocument(resultCode, data, GTABundle.UPDATE_ACTIVITY_DOCUMENT);
        }

    }

    public void onResultPlace(int resultCode, Intent data, String key) {
        if (resultCode == RESULT_OK) {
            placeEnd = (PlaceDTO) data.getSerializableExtra(key);
            edtUpdateActivityEndLocationName.setVisibility(View.VISIBLE);
            activityEndLocationName = placeEnd.getName();
            activityEndAddress = placeEnd.getAddress();
            placeEndID = placeEnd.getGooglePlaceId();
            timezonePlaceEnd = placeEnd.getTimeZone();

            edtUpdateActivityEndLocationName.setText(activityEndLocationName);
            edtUpdateActivityEndAddress.setText(activityEndAddress);

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
            edtUpdateActivityStartLocationName.setVisibility(View.VISIBLE);
            activityStartLocationName = place.getName();
            activityStartAddress = place.getAddress();
            placeStartID = place.getGooglePlaceId();
            edtUpdateActivityStartLocationName.setText(activityStartLocationName);
            edtUpdateActivityStartAddress.setText(activityStartAddress);
            timezonePlaceStart = place.getTimeZone();

        }
    }


    public void clickUpdateActivity() {
        try {
            TimeZone tz = TimeZone.getDefault();
            String timez = tz.getDisplayName();
            if (!checkNull() || !checkValidationSave(timeStart, timeEnd, dateStart, dateEnd, timez)) {
                return;
            }else {
                if(!checkActivityTime()){
                    return;
                }
                planId = SharePreferenceUtils.getIntSharedPreference(UpdateTransportationActivity.this, GTABundle.PLANID);
                mUpdateActivityDayPresenter.UpdateActivity(planId, getActivityResponseDTOFromForm());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private ActivityDTO getActivityResponseDTOFromForm() {
        activityName = edtActivityName.getText().toString();
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(idActivity);
        activityDTO.setName(activityName);
        activityDTO.setIdType(idType);
        activityDTO.setStartAt(ZonedDateTimeUtil.convertStringToDateOrTime(day1));
        activityDTO.setEndAt(ZonedDateTimeUtil.convertStringToDateOrTime(day2));
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setGooglePlaceId(placeStartID);
        activityDTO.setStartPlace(placeDTO);
        activityDTO.setDocumentList(documentDTOList);
        PlaceDTO placeDTO1 = new PlaceDTO();
        if(placeEndID == null){
            placeDTO1.setGooglePlaceId(placeStartID);
        }else {
            placeDTO1.setGooglePlaceId(placeEndID);
        }
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
                        edtUpdateActivityStartDate.setText(dateStart);

                    }
                }, mYear, mMonth, mDay);
        try {
            if (place != null ) {
                Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoJourneyUTC);
                Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndJourneyUTC);
                Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, place.getTimeZone());
                Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, place.getTimeZone());
                datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
            } else {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoJourney).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndJourney).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoJourney).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndJourney).getTime());
        }
        datePickerDialog.show();
    }

    public boolean validDateStart() {
        String dateStart = edtUpdateActivityStartDate.getText().toString().trim();
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
            edtUpdateActivityEndDate.setText("");
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
                            edtUpdateActivityEndDate.setText(dateEnd);
                        }
                    }, mYear, mMonth, mDay);
            try {
                if (placeEnd != null) {
                    Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoJourneyUTC);
                    Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndJourneyUTC);
                    Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, placeEnd.getTimeZone());
                    Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, placeEnd.getTimeZone());
                    datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                    datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
                } else {
                    datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoJourney).getTime());
                    datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndJourney).getTime());
                }
            } catch (Exception e) {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoJourney).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndJourney).getTime());
            }
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
                edtUpdateActivityStartTime.setText(timeStart);
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
                edtUpdateActivityEndTime.setText(timeEnd);
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }


    private void showActivityTypeDialog() {
        final Dialog dialog = new Dialog(UpdateTransportationActivity.this);
        dialog.setContentView(R.layout.row_dialog_activitytype);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioGroup radioGroup = dialog.findViewById(R.id.rdGroup);
        RadioButton rdActivity = dialog.findViewById(R.id.rdActivity);
        RadioButton rdAccomadation = dialog.findViewById(R.id.rdAccomodation);
        ImageView imgDialogActivity = dialog.findViewById(R.id.imgDiaLogActivity);
        imgDialogActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        rdActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                idType = ActivityType.ACTIVITY;
                edtUpdateActivityType.setText("Activity");
                idType = 1;

            }
        });


        rdAccomadation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                idType = ActivityType.ACTIVITY;
                edtUpdateActivityType.setText("Accomodation");
                idType = 3;

            }
        });

        dialog.show();
    }



    public boolean checkActivityTime(){
        Date date1 = ZonedDateTimeUtil.convertStringToDateOrTime(day1);
        Date date2 = ZonedDateTimeUtil.convertStringToDateOrTime(day2);
        for (int i = 0; i < activityDTOList.size(); i++) {
            if (activityDTOList.get(i).getId().equals(activityDTO.getId())) {
                activityDTOList.remove(i);
                break;
            }
        }

        Date startUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date1, timezonePlaceStart);
        Date endUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date2, timezonePlaceEnd);



        Date journeyStartUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoJourneyUTC);
        Date journeyEndUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndJourneyUTC);
        if (startUTC.getTime() < journeyStartUTC.getTime() || endUTC.getTime() > journeyEndUTC.getTime()) {
            DialogShowErrorMessage.showValidationDialog(this, "Date Time! Out of the journey");
            return false;
        }



        for (ActivityDTO activityDTO : activityDTOList) {
            Date stratActivity = activityDTO.getStartUtcAt();
            Date endActivity = activityDTO.getEndUtcAt();
            if (startUTC.getTime() < stratActivity.getTime()
                    && endUTC.getTime() < stratActivity.getTime()) {
            } else if (startUTC.getTime() > endActivity.getTime()
                    && endUTC.getTime() > endActivity.getTime()) {
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(Html.fromHtml("<font color='#FDD017'>This transportation time  is coinciding with another transportation time, do you still want to update?</font>"));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planId = SharePreferenceUtils.getIntSharedPreference(UpdateTransportationActivity.this, GTABundle.PLANID);
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
        Date endUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date2, timezonePlaceEnd);
        long distanceTime = endUTC.getTime() - startUTC.getTime();
        long diffDays = distanceTime / (24 * 60 * 60 * 1000);
        long diffM = distanceTime / (60 * 1000);
        if (startUTC.after(endUTC)) {
            DialogShowErrorMessage.showValidationDialog(this, "Time start must be before end time");
            return false;
        }else if(diffDays >= 1){
            DialogShowErrorMessage.showValidationDialog(this, "Transportation is too long");
            return false;
        }else if(diffM < 2){
            DialogShowErrorMessage.showValidationDialog(this, "Transportation is too short!");
            return false;
        }

        return true;

    }

    private boolean checkNull() {
        String activityNameUpdate = edtActivityName.getText().toString();
        if (activityNameUpdate.isEmpty() && activityNameUpdate.length() == 0) {
            DialogShowErrorMessage.showValidationDialog(this, "Please input transportation Name");
            return false;
        }
        else  if(placeStartID.equals(placeEndID)){
            DialogShowErrorMessage.showValidationDialog(this,"Place start other place end!");
            return false;
        }
        else {
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
        }catch (Exception e){
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