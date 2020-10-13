package com.fpt.gta.feature.manageplan.addactivity;

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
import android.widget.TextView;
import android.widget.TimePicker;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.feature.manageplan.documentactivity.ActivityDocumentActivity;
import com.fpt.gta.feature.manageplan.updateactivity.UpdateActivity;
import com.fpt.gta.feature.searchautocomplete.PlaceAutoCompleteActivity;
import com.fpt.gta.presenter.AddActivityDayPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.AddActivityDayView;
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

public class AddActivity extends AppCompatActivity implements AddActivityDayView {
    private EditText edtActivityType, edtEndActivityLocationName, edtStartActivityLocationName, edtEndPlaceActivityAddress, edtStartPlaceActivityAddress, edtActivityStartDate, edtActivityEndDate, edtActivityStartTime, edtActivityEndTime, edtActivityName;
    private String activityType = "", activityName = "";
    private String placeStartID, timeStart = "", dateStart = "", activityStartAddress, activityStartLocationName = "";
    private String placeEndID, timeEnd = "", dateEnd = "", activityEndLocationName = "EndPlace", activityEndAddress;
    private int mYear, mMonth, mDay;
    private Button btnSaveAddActivity;
    private Calendar calendarStart, calendarEnd, calendarTimeStart, calendarTimeEnd;
    SimpleDateFormat simpleDateFormat, simpleTimeFormat;
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private ImageView imgAddActivityBack, imgAddActivityDocument;
    private static final int ADD_ACTIVITY_ENDPLACE_REQUEST_CODE = 1233;
    private static final int ADD_ACTIVITY_STARTPLACE_REQUEST_CODE = 1234;
    private AddActivityDayPresenter addActivityDayPresenter;
    private PlaceDTO place, placeEnd;
    private int planId;
    private String day1, day2;
    private Integer idType;
    private String dateGoTrip, dateEndTrip;
    private String dateGoTripUTC, dateEndTripUTC;

    private LinearLayout lnlAddPlaceActivity, lnlSubPlaceActivity;
    private ImageView imgAddActivityPlace, imgSubActivityPlace;
    private List<DocumentDTO> documentDTOList = new ArrayList<>();
    private List<ActivityDTO> activityDTOList = new ArrayList<>();
    private String dayActivity;
    private Integer idGroup;
    private Integer idTrip;
    private FirebaseDatabase databaseActivity;
    private DatabaseReference listenerActivity;

    private String timezonePlaceStart, timezonePlaceEnd;
    private String timezoneTrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day_plan);
        initView();
        initData();
        initPlace();
    }


    public void initView() {
        imgAddActivityDocument = findViewById(R.id.imgAddActivityDocument);
        dateGoTrip = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPGO);
        dateEndTrip = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPEND);

        dateGoTripUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPGOUTC);
        dateEndTripUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATETRIPENDUTC);

        idTrip = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDTRIP);
        imgAddActivityBack = findViewById(R.id.imgAddActivityBack);
        edtEndActivityLocationName = findViewById(R.id.edtaddActivityEndLocationName);
        edtActivityName = findViewById(R.id.edtaddActivityName);
        edtStartActivityLocationName = findViewById(R.id.edtaddActivityStartLocationName);
        edtEndPlaceActivityAddress = findViewById(R.id.edtaddEndActivityAddress);
        edtStartPlaceActivityAddress = findViewById(R.id.edtaddStartPlaceActivityAddress);
        edtActivityEndDate = findViewById(R.id.edtAddActivityEndDate);
        edtActivityEndTime = findViewById(R.id.edtAddActivityEndTime);
        edtActivityStartDate = findViewById(R.id.edtAddActivityStartDate);
        edtActivityStartTime = findViewById(R.id.edtAddActivityStartTime);
        edtActivityType = findViewById(R.id.edtaddActivityType);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");
        btnSaveAddActivity = findViewById(R.id.btnSaveAddActivity);

        lnlAddPlaceActivity = findViewById(R.id.lnlAddPlaceActivity);
        lnlSubPlaceActivity = findViewById(R.id.lnlSubPlaceActivity);
        imgAddActivityPlace = findViewById(R.id.imgAddActivityPlace);
        imgSubActivityPlace = findViewById(R.id.imgSubActivityPlace);

        addActivityDayPresenter = new AddActivityDayPresenter(this, this);
        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseActivity = FirebaseDatabase.getInstance();
        listenerActivity = databaseActivity.getReference(String.valueOf(idGroup)).child("listener").child("reloadActivity");

        timezoneTrip = SharePreferenceUtils.getStringSharedPreference(this, GTABundle.TIMEZONETRIP);
    }


    public void initData() {
        dayActivity = SharePreferenceUtils.getStringSharedPreference(this, GTABundle.DATEACTIVITY);
        dateStart = dayActivity;
        dateEnd = dayActivity;
        edtActivityStartDate.setText(dateStart);
        edtActivityEndDate.setText(dateEnd);
        Bundle bundle = getIntent().getExtras();
        activityDTOList = (List<ActivityDTO>) bundle.getSerializable(GTABundle.ACTIVITYLIST);
        imgAddActivityDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, ActivityDocumentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "AddActivityDayActivity");
                bundle.putSerializable(GTABundle.ADD_ACTIVITY_DOCUMENT, (Serializable) documentDTOList);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.ADD_ACTIVITY_DOCUMENT_CODE);
            }
        });

        edtActivityType.setText("Activity");
        idType = 1;

        imgAddActivityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edtActivityStartTime.setFocusable(false);
        edtActivityStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTimeStart();
            }
        });

        edtActivityEndTime.setFocusable(false);
        edtActivityEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTimeEnd();
            }
        });

        edtActivityStartDate.setFocusable(false);
//        edtActivityStartDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getDateGo();
//            }
//        });

        edtActivityEndDate.setFocusable(false);
//        edtActivityEndDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getDateEnd();
//            }
//        });

        edtActivityType.setFocusable(false);
        edtActivityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivityTypeDialog();
            }
        });


        btnSaveAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TimeZone tz = TimeZone.getDefault();
                    String timez = tz.getDisplayName();
                    if (!checkNull() || !checkValidationSave(timeStart, timeEnd, dateStart, dateEnd, timez)) {
                        return;
                    } else {
                        if (!checkActivityTime()) {
                            return;
                        } else {
                            clickCreateActivity();
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        lnlSubPlaceActivity.setVisibility(View.GONE);
        edtEndActivityLocationName.setVisibility(View.GONE);
        edtEndPlaceActivityAddress.setVisibility(View.GONE);
        lnlAddPlaceActivity.setVisibility(View.GONE);
        imgAddActivityPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnlAddPlaceActivity.setVisibility(View.GONE);
                lnlSubPlaceActivity.setVisibility(View.VISIBLE);
                edtEndActivityLocationName.setVisibility(View.VISIBLE);
                edtEndPlaceActivityAddress.setVisibility(View.VISIBLE);
                activityEndLocationName = "";
                edtEndActivityLocationName.setText("");
                edtEndPlaceActivityAddress.setText("");
            }
        });

        imgSubActivityPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnlAddPlaceActivity.setVisibility(View.VISIBLE);
                lnlSubPlaceActivity.setVisibility(View.GONE);
                edtEndActivityLocationName.setVisibility(View.GONE);
                edtEndPlaceActivityAddress.setVisibility(View.GONE);
                placeEndID = null;
                timezonePlaceEnd = null;
                activityEndLocationName = "PlaceEnd";
            }
        });


    }


    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }

        edtStartActivityLocationName.setFocusable(false);
        edtStartActivityLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 2);
                intent.putExtra(GTABundle.IDTRIP, idTrip);
                startActivityForResult(intent, ADD_ACTIVITY_STARTPLACE_REQUEST_CODE);
            }
        });

        edtEndActivityLocationName.setFocusable(false);
        edtEndActivityLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 2);
                intent.putExtra(GTABundle.IDTRIP, idTrip);
                startActivityForResult(intent, ADD_ACTIVITY_ENDPLACE_REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1233:
                onResultPlace(resultCode, data, GTABundle.RESUTLPLACEEDITTRIP);
                break;
            case 1234:
                onResultStartPlace(resultCode, data, GTABundle.RESUTLPLACEEDITTRIP);
                break;
            case GTABundle.ADD_ACTIVITY_DOCUMENT_CODE:
                if (resultCode == RESULT_OK) {
                    documentDTOList = (List<DocumentDTO>) data.getSerializableExtra(GTABundle.ADD_ACTIVITY_DOCUMENT);
                }
        }
    }

    public void onResultPlace(int resultCode, Intent data, String key) {
        if (resultCode == RESULT_OK) {
            placeEnd = (PlaceDTO) data.getSerializableExtra(key);
            edtEndActivityLocationName.setVisibility(View.VISIBLE);
            activityEndLocationName = placeEnd.getName();
            edtEndActivityLocationName.setText(activityEndLocationName);
            activityEndAddress = placeEnd.getAddress();
            edtEndPlaceActivityAddress.setText(activityEndAddress);
            placeEndID = placeEnd.getGooglePlaceId();
            timezonePlaceEnd = placeEnd.getTimeZone();
        }
    }


    public void onResultStartPlace(int resultCode, Intent data, String key) {
        if (resultCode == RESULT_OK) {
            place = (PlaceDTO) data.getSerializableExtra(key);
            edtStartActivityLocationName.setVisibility(View.VISIBLE);
            activityStartLocationName = place.getName();
            edtStartActivityLocationName.setText(activityStartLocationName);
            activityStartAddress = place.getAddress();
            edtStartPlaceActivityAddress.setText(activityStartAddress);
            placeStartID = place.getGooglePlaceId();
            timezonePlaceStart = place.getTimeZone();
        }
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
                        edtActivityStartDate.setText(dateStart);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTrip).getTime());
        datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTrip).getTime());
        datePickerDialog.show();
    }


    public void getDateEnd() {
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
                        edtActivityEndDate.setText(dateEnd);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTrip).getTime());
        datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTrip).getTime());
        datePickerDialog.show();

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
                edtActivityStartTime.setText(timeStart);
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    public void clickCreateActivity() {
        planId = SharePreferenceUtils.getIntSharedPreference(AddActivity.this, GTABundle.PLANID);
        addActivityDayPresenter.AddActivity(planId, getActivityResponseDTOFromForm());

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
                edtActivityEndTime.setText(timeEnd);
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    private void showValidationDialog(String error) {
        final Dialog dialog = new Dialog(AddActivity.this);
        dialog.setContentView(R.layout.row_validation_dialog);
        TextView txtError = dialog.findViewById(R.id.txtErrorValidation);
        Button btnOK = dialog.findViewById(R.id.btnOkValidation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        txtError.setText(error);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showActivityTypeDialog() {
        final Dialog dialog = new Dialog(AddActivity.this);
        dialog.setContentView(R.layout.row_dialog_activitytype);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioButton rdActivity = dialog.findViewById(R.id.rdActivity);
        RadioButton rdFoodAndBeverage = dialog.findViewById(R.id.rdFoodAndBeverage);
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
                activityType = ActivityType.ACTIVITY + "";
                edtActivityType.setText("Activity");
                idType = 1;


            }
        });

        rdFoodAndBeverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                activityType = ActivityType.FOODANDBEVERAGE + "";
                edtActivityType.setText("Food And Beverage");
                idType = 4;
            }
        });

        dialog.show();
    }

    public boolean checkActivityTime() {
        Date date1 = ZonedDateTimeUtil.convertStringToDateOrTime(day1);
        Date date2 = ZonedDateTimeUtil.convertStringToDateOrTime(day2);
        Date startUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date1, timezonePlaceStart);
        Date endUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date2, timezonePlaceStart);

        Date dateStartTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoTripUTC);
        Date convertedDateStartUTC = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateStartTripUtc, timezonePlaceStart);

        Date dateEndTripUtc = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndTripUTC);
        Date convertedDateEndUTC = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndTripUtc, timezonePlaceStart);

        if (date1.getTime() < convertedDateStartUTC.getTime() || date2.getTime() > convertedDateEndUTC.getTime()) {
            DialogShowErrorMessage.showValidationDialog(this, "Date Time! Out of the City");
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
                builder.setMessage(Html.fromHtml("<font color='#FDD017'>This activity time  is coinciding with another activity time, do you still want to create?</font>"));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickCreateActivity();
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

    private boolean checkValidationSave(String timeStart, String timeEnd, String dateStart, String dateEnd, String timeZone) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        day1 = dateStart + " " + timeStart;
        day2 = dateEnd + " " + timeEnd;
        Date date1 = simpleDateFormat.parse(day1);
        Date date2 = simpleDateFormat.parse(day2);
        Date startUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date1, timezonePlaceStart);
        Date endUTC = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(date2, timezonePlaceStart);
        long distanceTime = endUTC.getTime() - startUTC.getTime();
        long diffDays = distanceTime / (24 * 60 * 60 * 1000);
        if (startUTC.after(endUTC)) {
            DialogShowErrorMessage.showValidationDialog(this, "Start time must be before end time");
            return false;
        }

        return true;

    }


    private boolean checkNull() {
        String nameActivity = edtActivityName.getText().toString();
        if (nameActivity.isEmpty()) {
            showValidationDialog("Please input Activity Name");
            return false;
        } else if (activityEndLocationName == "" && activityEndLocationName.length() == 0) {
            showValidationDialog("Please choose Activity place end");
            return false;
        } else if (activityStartLocationName == "" && activityStartLocationName.length() == 0) {
            showValidationDialog("Please choose Activity place");
            return false;
        } else if (dateStart.length() == 0 && dateStart == "") {
            showValidationDialog("Please input Activity Day Start");
            return false;
        } else if (dateEnd.length() == 0 && dateEnd == "") {
            showValidationDialog("Please input Activity Day End");
            return false;
        } else if (timeStart.length() == 0 && timeStart == "") {
            showValidationDialog("Please pick activity time start”");
            return false;
        } else if (timeEnd.length() == 0 && timeEnd == "") {
            showValidationDialog("Please pick activity time end”");
            return false;
        } else if (timezonePlaceStart == null) {
            DialogShowErrorMessage.showValidationDialog(this, "Time zone is null");
            return false;
        } else if (!timezonePlaceStart.equals(timezoneTrip)) {
            DialogShowErrorMessage.showValidationDialog(this, "This place is in different time zone");
            return false;
        } else {
            return true;
        }
    }

    private ActivityDTO getActivityResponseDTOFromForm() {
        activityName = edtActivityName.getText().toString();
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setName(activityName);
        activityDTO.setIdType(idType);
        Boolean isTooFarStart = place.getIsTooFar();
        activityDTO.setIsTooFar(isTooFarStart);
        activityDTO.getIsTooFar();
        activityDTO.setDocumentList(documentDTOList);
        activityDTO.setStartAt(ZonedDateTimeUtil.convertStringToDateOrTime(day1));
        activityDTO.setEndAt(ZonedDateTimeUtil.convertStringToDateOrTime(day2));
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setGooglePlaceId(placeStartID);
        activityDTO.setStartPlace(placeDTO);
        PlaceDTO placeDTO1 = new PlaceDTO();
        placeDTO1.setGooglePlaceId(placeStartID);
        activityDTO.setEndPlace(placeDTO1);
        return activityDTO;
    }


    @Override
    public void AddActivitySuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerActivity.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void AddActivityFail(String message) {
        DialogShowErrorMessage.showValidationDialog(this, message);
    }


}