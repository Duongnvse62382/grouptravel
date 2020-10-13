package com.fpt.gta.feature.managetrip.addtrip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.feature.searchautocomplete.PlaceAutoCompleteActivity;
import com.fpt.gta.presenter.CreateTripPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateTripView;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CreateTripActivity extends AppCompatActivity implements CreateTripView {

    private ImageView imgBack, imgTripImage;
    private LinearLayout lnDateStart, lnDateEnd, lnlSearchResult;
    private LinearLayout lnlTimeStart, lnlTimeEnd;
    private TextView txtCreateDateStart, txtCreateDateEnd, txtPlace, txtPlaceTripAddress;
    private TextView txtCreateTimeStart, txtCreateTimeEnd;
    private TextView txtTimeZone, txtTimeZoneUTC;
    private TextView txtCreateDateStartUTC, txtCreateTimeStartUTC, txtCreateDateEndUTC, txtCreateTimeEndUTC;
    private LinearLayout lnDateStartUTC, lnDateEndUTC;
    private LinearLayout lnlTimeStartUTC, lnlTimeEndUTC;
    private LinearLayout lnlTimeZonePoint;
    private String timeStart, timeEnd;
    private String timeStartUTC, timeEndUTC;
    private EditText edtSearchPlace;
    private ImageView imgAddCitySubmit;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart, calendarEnd, calendarTimeStart, calendarTimeEnd;
    private SimpleDateFormat simpleDateFormat, simpleTimeFormat;
    private String placeID;
    private CreateTripPresenter mCreateTripPresenter;
    private int groupId;
    private static final int CREATE_TRIP_ACTIVITY_REQUEST_CODE = 0;
    private String dateGo, dateEnd;
    private String dateGoUTC, dateEndGUTC;
    private String calGo, calEnd;
    private String calGoUTC, calEndUTC;
    private String calEndTime;
    private SimpleDateFormat simpleDateTimeFormat;
    private PlaceDTO place;
    private String imgUri = null;
    private String dateStartTrip, timeStartTrip;
    private String dateEndTrip, timeEndTrip;

    private String dateStartTripUTC, timeStartTripUTC;
    private String dateEndTripUTC, timeEndTripUTC;

    private String namePlaceStart = "";
    private List<TripReponseDTO> tripReponseDTOList;
    private FirebaseDatabase databaseTrip;
    private DatabaseReference listenerTrip;
    private String timeZone;
    private String timeZoneJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        initview();
        initPlace();
        lnlSearchResult.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }
        edtSearchPlace.setFocusable(false);
        edtSearchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateTripActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 1);
                startActivityForResult(intent, CREATE_TRIP_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_TRIP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                lnlSearchResult.setVisibility(View.VISIBLE);
                place = (PlaceDTO) data.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
                namePlaceStart = place.getName();
                txtPlace.setText(namePlaceStart);
                placeID = place.getGooglePlaceId();
                txtPlaceTripAddress.setText(place.getAddress());
                timeZone = place.getTimeZone();
                if (place.getPlaceImageList().size() != 0) {
                    imgUri = place.getPlaceImageList().get(0).getUri();
                    ImageLoaderUtil.loadImage(this, imgUri, imgTripImage);
                } else {
                    Picasso.get().load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(imgTripImage);
                }

                if(!place.getTimeZone().equals(timeZoneJourney)){
                    lnlTimeZonePoint.setVisibility(View.VISIBLE);
                }else {
                    lnlTimeZonePoint.setVisibility(View.GONE);
                }

                txtTimeZone.setText(place.getTimeZone());
                calGo = null;
                calEnd = null;
                timeEnd = null;
                timeStart = null;

                calGoUTC = null;
                calEndUTC = null;
                timeEndUTC = null;
                timeStartUTC = null;

                txtCreateDateStart.setText("Day Start");
                txtCreateTimeStart.setText("Time Start");
                txtCreateTimeEnd.setText("Day End");
                txtCreateDateEnd.setText("Time End");

                txtCreateDateStartUTC.setText("Day Start");
                txtCreateTimeStartUTC.setText("Time Start");
                txtCreateTimeEndUTC.setText("Day End");
                txtCreateDateEndUTC.setText("Time End");
            }
        }
    }


    public void initview() {
        dateGo = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEGO);
        dateEnd = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEEND);

        dateGoUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEGOUTC);
        dateEndGUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEENDUTC);
        timeZoneJourney = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.TIMEZONEGROUP);

        Bundle bundle = getIntent().getExtras();
        tripReponseDTOList = (List<TripReponseDTO>) bundle.getSerializable(GTABundle.KEYTRIPLIST);
        groupId = (Integer) bundle.getInt("groupID");

        lnDateStart = findViewById(R.id.lnlDateStart);
        lnDateEnd = findViewById(R.id.lnlDateEnd);
        txtCreateDateStart = findViewById(R.id.txtCreateDateStart);
        txtCreateDateEnd = findViewById(R.id.txtCreateDateEnd);

        lnlTimeStart = findViewById(R.id.lnlTimeStart);
        txtCreateTimeStart = findViewById(R.id.txtCreateTimeStart);
        txtTimeZone = findViewById(R.id.txtTimeZone);
        txtTimeZoneUTC = findViewById(R.id.txtTimeZoneUTC);
        txtCreateTimeEnd = findViewById(R.id.txtCreateTimeEnd);

        txtCreateDateStartUTC = findViewById(R.id.txtCreateDateStartUTC);
        txtCreateTimeStartUTC = findViewById(R.id.txtCreateTimeStartUTC);

        txtCreateDateEndUTC = findViewById(R.id.txtCreateDateEndUTC);
        txtCreateTimeEndUTC = findViewById(R.id.txtCreateTimeEndUTC);

        lnlTimeEnd = findViewById(R.id.lnlTimeEnd);

        lnDateStartUTC = findViewById(R.id.lnlDateStartUTC);
        lnDateEndUTC = findViewById(R.id.lnlDateEndUTC);
        lnlTimeStartUTC = findViewById(R.id.lnlTimeStartUTC);
        lnlTimeEndUTC = findViewById(R.id.lnlTimeEndUTC);
        lnlTimeZonePoint = findViewById(R.id.lnlTimeZonePoint);

        imgBack = findViewById(R.id.imgBack);
        imgTripImage = findViewById(R.id.imgTripImage);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");
        edtSearchPlace = findViewById(R.id.editSearch);
        txtPlace = findViewById(R.id.txtPlace);
        txtPlaceTripAddress = findViewById(R.id.txtPlaceTripAddress);
        imgAddCitySubmit = findViewById(R.id.imgAddCitySubmit);
        lnlSearchResult = findViewById(R.id.lnlSearchResult);
        databaseTrip = FirebaseDatabase.getInstance();
        listenerTrip = databaseTrip.getReference(String.valueOf(groupId)).child("listener").child("reloadTrip");
        txtTimeZoneUTC.setText(timeZoneJourney);
        lnlTimeZonePoint.setVisibility(View.GONE);
    }


    public void clickCreateTrip() {
        try {
            if (!isValidDateRange()) {
                return;
            }
            mCreateTripPresenter = new CreateTripPresenter(this, this);
            mCreateTripPresenter.createTrip(groupId, getTripResponseDTOFromForm());
        } catch (Exception e) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick DateTime");
            e.printStackTrace();
        }
    }


    private boolean checkNull() {
        String dateStart = txtCreateDateStart.getText().toString().trim();
        String dateEnd = txtCreateDateEnd.getText().toString().trim();
        if (namePlaceStart.length() == 0 && namePlaceStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please choose Place");
            return false;
        } else if (dateStart.length() == 0 && dateStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick  Day Start");
            return false;
        } else if (dateEnd.length() == 0 && dateEnd.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick Day End");
            return false;
        } else {
            return true;
        }
    }


    private boolean isValidDateRange() {
        if (!checkNull()) {
            return false;
        }
        dateEndTrip = txtCreateDateEnd.getText().toString();
        dateStartTrip = txtCreateDateStart.getText().toString();
        timeStartTrip = txtCreateTimeStart.getText().toString();
        timeEndTrip = txtCreateTimeEnd.getText().toString();
        String dayStrat = dateStartTrip + " " + timeStartTrip;
        String dayEnd = dateEndTrip + " " + timeEndTrip;
        Date tripStart = ZonedDateTimeUtil.convertStringToDateOrTime(dayStrat);
        Date tripEnd = ZonedDateTimeUtil.convertStringToDateOrTime(dayEnd);
        Date pickedStartDate = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStart, timeZone);
        Date pickedEndDate = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEnd, timeZone);
        if (pickedStartDate.getTime() > pickedEndDate.getTime()) {
            DialogShowErrorMessage.showValidationDialog(this, "Start day must be before end day");
            return false;
        }

        long distanceTime = pickedEndDate.getTime() - pickedStartDate.getTime();
        long diffHouse = distanceTime / (60 * 60 * 1000);
        if (diffHouse < 12) {
            DialogShowErrorMessage.showValidationDialog(this, "Time to go at least half day");
            return false;
        }

        Date journeyStartUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoUTC);
        Date journeyEndUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndGUTC);
        if (pickedStartDate.getTime() < journeyStartUTC.getTime() || pickedEndDate.getTime() > journeyEndUTC.getTime()) {
            DialogShowErrorMessage.showValidationDialog(this, "Date Time! Out of the journey");
            return false;
        }


        for (TripReponseDTO tripReponseDTO : tripReponseDTOList) {
            Date stratTripTimeZone = tripReponseDTO.getStartUtcAt();
            Date endTripTimeZone = tripReponseDTO.getEndUtcAt();
            if (pickedStartDate.getTime() < stratTripTimeZone.getTime()
                    && pickedEndDate.getTime() < stratTripTimeZone.getTime()) {
            } else if (pickedStartDate.getTime() > endTripTimeZone.getTime()
                    && pickedEndDate.getTime() > endTripTimeZone.getTime()) {
            } else {
                DialogShowErrorMessage.showValidationDialog(this, "You have planned on this days!");
                return false;
            }
        }
        return true;
    }

    public void initData() {
        lnDateStart.setOnClickListener(this::onClick);
        lnDateEnd.setOnClickListener(this::onClick);
        lnlTimeStart.setOnClickListener(this::onClick);
        lnlTimeEnd.setOnClickListener(this::onClick);


        lnDateStartUTC.setOnClickListener(this::onClick);
        lnDateEndUTC.setOnClickListener(this::onClick);
        lnlTimeStartUTC.setOnClickListener(this::onClick);
        lnlTimeEndUTC.setOnClickListener(this::onClick);

        imgBack.setOnClickListener(this::onClick);
        imgAddCitySubmit.setOnClickListener(this::onClick);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.lnlDateStart:
                getDateGo();
                break;
            case R.id.lnlDateEnd:
                getDateEnd();
                break;
            case R.id.lnlTimeEnd:
                getTimeEnd();
                break;
            case R.id.lnlTimeStart:
                getTimeStart();
                break;


            case R.id.lnlDateStartUTC:
                getDateGoUTC();
                break;
            case R.id.lnlDateEndUTC:
                getDateEndUTC();
                break;
            case R.id.lnlTimeEndUTC:
                getTimeEndUTC();
                break;
            case R.id.lnlTimeStartUTC:
                getTimeStartUTC();
                break;


            case R.id.imgBack:
                finish();
                break;
            case R.id.imgAddCitySubmit:
                clickCreateTrip();
        }
    }

    public void getTimeStart() {
        String dateStart = txtCreateDateStart.getText().toString();
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dateStart.length() < 10) {
            Toast.makeText(this, "Choose Day first!", Toast.LENGTH_SHORT).show();
            return;
        }
        calendarTimeStart = Calendar.getInstance();
        int hour = calendarTimeStart.get(Calendar.HOUR_OF_DAY);
        int minutes = calendarTimeStart.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendarTimeStart.set(Calendar.HOUR_OF_DAY, i);
                calendarTimeStart.set(Calendar.MINUTE, i1);
                timeStart = simpleTimeFormat.format(calendarTimeStart.getTime());
                txtCreateTimeStart.setText(timeStart);

                timeStartTrip = txtCreateTimeStart.getText().toString();
                dateStartTrip = txtCreateDateStart.getText().toString();
                String dayStrat = dateStartTrip + " " + timeStartTrip;
                Date tripStart = ZonedDateTimeUtil.convertStringToDateOrTime(dayStrat);
                Date timezoneStratUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStart, place.getTimeZone());
                Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratUtcTrip, timeZoneJourney);
                txtCreateDateStartUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratUtcJourey));
                txtCreateTimeStartUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratUtcJourey));
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    public void getTimeEnd() {
        String dateEnd = txtCreateDateEnd.getText().toString();
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dateEnd.length() < 10) {
            Toast.makeText(this, "Choose Day first!", Toast.LENGTH_SHORT).show();
            return;
        }
        calendarTimeEnd = Calendar.getInstance();
        int hour = calendarTimeEnd.get(Calendar.HOUR_OF_DAY);
        int minutes = calendarTimeEnd.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendarTimeEnd.set(Calendar.HOUR_OF_DAY, i);
                calendarTimeEnd.set(Calendar.MINUTE, i1);
                timeEnd = simpleTimeFormat.format(calendarTimeEnd.getTime());
                txtCreateTimeEnd.setText(timeEnd);
                dateEndTrip = txtCreateDateEnd.getText().toString();
                timeEnd = txtCreateTimeEnd.getText().toString();
                String dayEnd = dateEndTrip + " " + timeEnd;
                Date tripEnd = ZonedDateTimeUtil.convertStringToDateOrTime(dayEnd);
                Date timezoneEndUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEnd, place.getTimeZone());
                Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndUtcTrip, timeZoneJourney);
                txtCreateDateEndUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndUtcJourey));
                txtCreateTimeEndUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndUtcJourey));
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }


    public void getDateGo() {
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        }
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
                        calGo = simpleDateFormat.format(calendarStart.getTime());
                        calGo = ZonedDateTimeUtil.convertDateToStringASIA(calendarStart.getTime());
                        txtCreateDateStart.setText(calGo);
                        if (timeStart == null) {
                            txtCreateTimeStart.setText("00:00");
                        }
                        timeStartTrip = txtCreateTimeStart.getText().toString();
                        dateStartTrip = txtCreateDateStart.getText().toString();
                        String dayStrat = dateStartTrip + " " + timeStartTrip;
                        Date tripStart = ZonedDateTimeUtil.convertStringToDateOrTime(dayStrat);
                        Date timezoneStratUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStart, place.getTimeZone());
                        Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratUtcTrip, timeZoneJourney);
                        txtCreateDateStartUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratUtcJourey));
                        txtCreateTimeStartUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratUtcJourey));
                    }
                }, mYear, mMonth, mDay);

        try {
            if (place != null) {
                Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoUTC);
                Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndGUTC);
                Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, place.getTimeZone());
                Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, place.getTimeZone());
                datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
            } else {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
        }
        datePickerDialog.show();
    }

    public void getDateEnd() {
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        }

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
                        calEnd = simpleDateFormat.format(calendarEnd.getTime());
                        calEndTime = simpleDateTimeFormat.format(calendarEnd.getTime());
                        txtCreateDateEnd.setText(calEnd);

                        if (timeEnd == null) {
                            txtCreateTimeEnd.setText("23:59");
                        }
                        dateEndTrip = txtCreateDateEnd.getText().toString();
                        timeEnd = txtCreateTimeEnd.getText().toString();
                        String dayEnd = dateEndTrip + " " + timeEnd;
                        Date tripEnd = ZonedDateTimeUtil.convertStringToDateOrTime(dayEnd);
                        Date timezoneEndUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEnd, place.getTimeZone());
                        Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndUtcTrip, timeZoneJourney);
                        txtCreateDateEndUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndUtcJourey));
                        txtCreateTimeEndUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndUtcJourey));
                    }

                }, mYear, mMonth, mDay);
        try {
            if (place != null) {
                Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoUTC);
                Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndGUTC);
                Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, place.getTimeZone());
                Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, place.getTimeZone());
                datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
            } else {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
        }
        datePickerDialog.show();

    }









    public void getTimeStartUTC() {
        String dateStartUtc = txtCreateDateStartUTC.getText().toString();
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dateStartUtc.length() < 10) {
            Toast.makeText(this, "Choose Day first!", Toast.LENGTH_SHORT).show();
            return;
        }
        calendarTimeStart = Calendar.getInstance();
        int hour = calendarTimeStart.get(Calendar.HOUR_OF_DAY);
        int minutes = calendarTimeStart.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendarTimeStart.set(Calendar.HOUR_OF_DAY, i);
                calendarTimeStart.set(Calendar.MINUTE, i1);
                timeStartUTC = simpleTimeFormat.format(calendarTimeStart.getTime());
                txtCreateTimeStartUTC.setText(timeStartUTC);

                timeStartTripUTC = txtCreateTimeStartUTC.getText().toString();
                dateStartTripUTC = txtCreateDateStartUTC.getText().toString();
                String dayStratUTC = dateStartTripUTC + " " + timeStartTripUTC;
                Date tripStartUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayStratUTC);
                Date timezoneStratTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStartUTC, timeZoneJourney);
                Date timezoneStratJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratTrip, place.getTimeZone());
                txtCreateDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratJourey));
                txtCreateTimeStart.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratJourey));
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    public void getTimeEndUTC() {
        String dateEndUtuc = txtCreateDateEndUTC.getText().toString();
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dateEndUtuc.length() <  10) {
            Toast.makeText(this, "Choose Day first!", Toast.LENGTH_SHORT).show();
            return;
        }
        calendarTimeEnd = Calendar.getInstance();
        int hour = calendarTimeEnd.get(Calendar.HOUR_OF_DAY);
        int minutes = calendarTimeEnd.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendarTimeEnd.set(Calendar.HOUR_OF_DAY, i);
                calendarTimeEnd.set(Calendar.MINUTE, i1);
                timeEndUTC = simpleTimeFormat.format(calendarTimeEnd.getTime());
                txtCreateTimeEndUTC.setText(timeEndUTC);

                timeEndTripUTC = txtCreateTimeEndUTC.getText().toString();
                dateEndTripUTC = txtCreateDateEndUTC.getText().toString();
                String dayEndUTC = dateEndTripUTC + " " + timeEndTripUTC;
                Date tripEndUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayEndUTC);
                Date timezoneEndTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEndUTC, timeZoneJourney);
                Date timezoneEndJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndTrip, place.getTimeZone());
                txtCreateDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndJourey));
                txtCreateTimeEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndJourey));
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }


    public void getDateGoUTC() {
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        }
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
                        calGoUTC = simpleDateFormat.format(calendarStart.getTime());
                        calGoUTC = ZonedDateTimeUtil.convertDateToStringASIA(calendarStart.getTime());
                        txtCreateDateStartUTC.setText(calGoUTC);
                        if (timeStartUTC == null) {
                            txtCreateTimeStartUTC.setText("00:00");
                        }

                        timeStartTripUTC = txtCreateTimeStartUTC.getText().toString();
                        dateStartTripUTC = txtCreateDateStartUTC.getText().toString();
                        String dayStratUTC = dateStartTripUTC + " " + timeStartTripUTC;
                        Date tripStartUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayStratUTC);
                        Date timezoneStratTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStartUTC, timeZoneJourney);
                        Date timezoneStratJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratTrip, place.getTimeZone());
                        txtCreateDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratJourey));
                        txtCreateTimeStart.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratJourey));


                    }
                }, mYear, mMonth, mDay);

        try {
            if (timeZoneJourney != null) {
                Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoUTC);
                Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndGUTC);
                Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, timeZoneJourney);
                Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, timeZoneJourney);
                datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
            } else {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
        }
        datePickerDialog.show();
    }

    public void getDateEndUTC() {
        if (place == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        }

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
                        calEndUTC = simpleDateFormat.format(calendarEnd.getTime());
                        calEndTime = simpleDateTimeFormat.format(calendarEnd.getTime());
                        txtCreateDateEndUTC.setText(calEndUTC);

                        if (timeEndUTC == null) {
                            txtCreateTimeEndUTC.setText("23:59");
                        }
                        timeEndTripUTC = txtCreateTimeEndUTC.getText().toString();
                        dateEndTripUTC = txtCreateDateEndUTC.getText().toString();
                        String dayEndUTC = dateEndTripUTC + " " + timeEndTripUTC;
                        Date tripEndUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayEndUTC);
                        Date timezoneEndTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEndUTC, timeZoneJourney);
                        Date timezoneEndJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndTrip, place.getTimeZone());
                        txtCreateDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndJourey));
                        txtCreateTimeEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndJourey));
                    }

                }, mYear, mMonth, mDay);
        try {
            if (timeZoneJourney != null) {
                Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoUTC);
                Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndGUTC);
                Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, timeZoneJourney);
                Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, timeZoneJourney);
                datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
            } else {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEnd).getTime());
        }
        datePickerDialog.show();

    }


    private TripReponseDTO getTripResponseDTOFromForm() {
        dateEndTrip = txtCreateDateEnd.getText().toString();
        dateStartTrip = txtCreateDateStart.getText().toString();
        timeStartTrip = txtCreateTimeStart.getText().toString();
        timeEndTrip = txtCreateTimeEnd.getText().toString();
        String dayStrat = dateStartTrip + " " + timeStartTrip;
        String dayEnd = dateEndTrip + " " + timeEndTrip + ":59";
        TripReponseDTO tripReponseDTO = new TripReponseDTO();
        tripReponseDTO.setStartAt(ZonedDateTimeUtil.convertStringToDateOrTime(dayStrat));
        tripReponseDTO.setEndAt(ZonedDateTimeUtil.convertStringToDateOrTime(dayEnd));
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setName(namePlaceStart);
        placeDTO.setGooglePlaceId(placeID);
        tripReponseDTO.setStartPlace(placeDTO);
        return tripReponseDTO;
    }


    @Override
    public void createTripSuccess(String s) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerTrip.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void createTripFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }


    @Override
    public void showError(String message) {
        DialogShowErrorMessage.showValidationDialog(this, message);
    }
}