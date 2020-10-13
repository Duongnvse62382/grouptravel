package com.fpt.gta.feature.managetrip.edittrip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.fpt.gta.presenter.DeleteTripPresenter;
import com.fpt.gta.presenter.UpdateTripPresenter;
import com.fpt.gta.util.DateManagement;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.DeleteTripView;
import com.fpt.gta.view.UpdateTripView;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EditTripActivity extends AppCompatActivity implements View.OnClickListener, UpdateTripView, DeleteTripView {

    private TextView txtPlaceAddress, txtPlaceTrip;
    private EditText edtEditTripName;
    private ImageView imgUpdateCitySubmit;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart, calendarEnd;
    private SimpleDateFormat simpleDateFormat;
    private String placeID = null;
    private TripReponseDTO mTrip;
    private int id;
    private String placeIdEdit;
    private ImageView imgTripImageUpdate;
    private UpdateTripPresenter mUpdateTripPresenter;
    private DeleteTripPresenter mDeleteTripPresenter;
    private ImageView imgDeleteTrip, imgBackEditTrip;
    private static final int EDIT_TRIP_ACTIVITY_REQUEST_CODE = 0;
    private PlaceDTO place;
    private String uriPlace;
    private String dateGo, dateEndG;
    private String dateGoUTC, dateEndGUTC;
    private String dateStartTrip;
    private String dateEndTrip;
    private List<TripReponseDTO> tripReponseDTOList;
    private String calEndTime;
    private SimpleDateFormat simpleDateTimeFormat;
    private Integer idGroup;
    private FirebaseDatabase databaseTrip;
    private DatabaseReference listenerTrip;
    private String timeZone;


    private LinearLayout lnlEditTripDelete, lnlEditDateTripStart, lnlEditDateTripEnd;
    private LinearLayout lnlEditTimeStart, lnlEditTimeEnd;
    private LinearLayout lnlEditDateStartUTC, lnlEditTimeStartUTC;
    private LinearLayout lnlDateEndUTC, lnlEditTimeEndUTC;
    private TextView txtEditTripDateStart, txtEditTripDateEnd;
    private TextView txtEditTimeStart, txtEditTimeEnd;
    private TextView txtEditDateStartUTC, txtEditTimeStartUTC;
    private TextView txtEditDateEndUTC, txtEditTimeEndUTC, txtTimeZone, txtTimeZoneUTC;

    private LinearLayout lnlTimeZonePoint;
    private String timeStart, timeEnd;
    private String timeStartUTC, timeEndUTC;
    private Calendar calendarTimeStart, calendarTimeEnd;

    private SimpleDateFormat simpleTimeFormat;
    private String calGo = "time", calEnd = "time";
    private String calGoUTC = "time", calEndUTC = "time";
    private String timeStartTrip;
    private String timeEndTrip;

    private String dateStartTripUTC, timeStartTripUTC;
    private String dateEndTripUTC, timeEndTripUTC;
    private String timeZoneJourney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        initView();
        initData();
        initPlace();

    }

    public void initView() {
        dateGo = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEGO);
        dateEndG = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEEND);

        dateGoUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEGOUTC);
        dateEndGUTC = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.DATEENDUTC);
        timeZoneJourney = SharePreferenceUtils.getStringSharedPreference(getApplicationContext(), GTABundle.TIMEZONEGROUP);

        Bundle bundle = getIntent().getExtras();
        mTrip = (TripReponseDTO) bundle.getSerializable(GTABundle.KEYTRIP);
        tripReponseDTOList = (List<TripReponseDTO>) bundle.getSerializable(GTABundle.KEYTRIPLIST);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");

        imgTripImageUpdate = findViewById(R.id.imgTripImageUpdate);
        txtEditTripDateStart = findViewById(R.id.textEditDayStart);
        txtEditTripDateEnd = findViewById(R.id.txtEditTripDateEnd);
        imgUpdateCitySubmit = findViewById(R.id.imgUpdateCitySubmit);
        lnlEditTripDelete = findViewById(R.id.lnlEditTripDelete);
        lnlEditDateTripEnd = findViewById(R.id.lnlEditDateTripEnd);
        edtEditTripName = findViewById(R.id.edtEditTripName);
        lnlEditDateTripStart = findViewById(R.id.lnlEditDateTripStart);
        imgDeleteTrip = findViewById(R.id.imgDeleteTrip);
        imgBackEditTrip = findViewById(R.id.imgBackEditTrip);
        txtPlaceAddress = findViewById(R.id.txtPlaceAddress);
        txtPlaceTrip = findViewById(R.id.txtPlaceTrip);

        lnlEditTimeStart = findViewById(R.id.lnlEditTimeStart);
        lnlEditTimeEnd = findViewById(R.id.lnlEditTimeEnd);
        lnlEditDateStartUTC = findViewById(R.id.lnlEditDateStartUTC);
        lnlEditTimeStartUTC = findViewById(R.id.lnlEditTimeStartUTC);
        lnlDateEndUTC = findViewById(R.id.lnlDateEndUTC);
        lnlEditTimeEndUTC = findViewById(R.id.lnlEditTimeEndUTC);
        lnlTimeZonePoint = findViewById(R.id.lnlTimeZonePoint);

        txtEditTimeStart = findViewById(R.id.txtEditTimeStart);
        txtEditTimeEnd = findViewById(R.id.txtEditTimeEnd);
        txtEditDateStartUTC = findViewById(R.id.txtEditDateStartUTC);
        txtEditTimeStartUTC = findViewById(R.id.txtEditTimeStartUTC);
        txtEditDateEndUTC = findViewById(R.id.txtEditDateEndUTC);
        txtEditTimeEndUTC = findViewById(R.id.txtEditTimeEndUTC);
        txtTimeZone = findViewById(R.id.txtTimeZone);
        txtTimeZoneUTC = findViewById(R.id.txtTimeZoneUTC);
    }

    public void initData() {
        lnlEditDateTripEnd.setOnClickListener(this::onClick);
        lnlEditDateTripStart.setOnClickListener(this::onClick);
        lnlEditTimeStart.setOnClickListener(this::onClick);
        lnlEditTimeEnd.setOnClickListener(this::onClick);
        lnlEditTimeStartUTC.setOnClickListener(this::onClick);
        lnlEditTimeEndUTC.setOnClickListener(this::onClick);
        lnlEditDateStartUTC.setOnClickListener(this::onClick);
        lnlDateEndUTC.setOnClickListener(this::onClick);

        imgUpdateCitySubmit.setOnClickListener(this::onClick);
        imgDeleteTrip.setOnClickListener(this::onClick);
        imgBackEditTrip.setOnClickListener(this::onClick);


        id = mTrip.getId();
        placeIdEdit = mTrip.getStartPlace().getGooglePlaceId();
        String name = mTrip.getStartPlace().getName();
        dateStartTrip = ZonedDateTimeUtil.convertDateToStringASIA(mTrip.getStartAt());
        dateEndTrip = ZonedDateTimeUtil.convertDateToStringASIA(mTrip.getEndAt());

        timeStartTrip = ZonedDateTimeUtil.convertDateToStringTime(mTrip.getStartAt());
        timeEndTrip = ZonedDateTimeUtil.convertDateToStringTime(mTrip.getEndAt());

        edtEditTripName.setText(name);
        timeZone = mTrip.getStartPlace().getTimeZone();
        calEndTime = ZonedDateTimeUtil.convertDateTimeHmsToString(mTrip.getEndAt());
        txtEditTripDateStart.setText(dateStartTrip);
        txtEditTripDateEnd.setText(dateEndTrip);
        txtEditTimeStart.setText(timeStartTrip);
        txtEditTimeEnd.setText(timeEndTrip);

        dateStartTrip = txtEditTripDateStart.getText().toString();
        dateEndTrip = txtEditTripDateEnd.getText().toString();
        try {
            uriPlace = mTrip.getStartPlace().getPlaceImageList().get(0).getUri();
            ImageLoaderUtil.loadImage(this, uriPlace, imgTripImageUpdate);
        } catch (Exception e) {
            e.getMessage();
        }

        Date timezoneStratUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(mTrip.getStartAt(), mTrip.getStartPlace().getTimeZone());
        Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratUtcTrip, timeZoneJourney);
        txtEditDateStartUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratUtcJourey));
        txtEditTimeStartUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratUtcJourey));


        Date timezoneEndUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(mTrip.getEndAt(), mTrip.getStartPlace().getTimeZone());
        Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndUtcTrip, timeZoneJourney);
        txtEditDateEndUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndUtcJourey));
        txtEditTimeEndUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndUtcJourey));


        if (!mTrip.getStartPlace().getTimeZone().equals(timeZoneJourney)) {
            lnlTimeZonePoint.setVisibility(View.VISIBLE);
        } else {
            lnlTimeZonePoint.setVisibility(View.GONE);
        }

        txtTimeZone.setText(mTrip.getStartPlace().getTimeZone());
        txtTimeZoneUTC.setText(timeZoneJourney);

        txtPlaceAddress.setText(mTrip.getStartPlace().getAddress());
        txtPlaceTrip.setText(mTrip.getStartPlace().getName());

        idGroup = SharePreferenceUtils.getIntSharedPreference(this, GTABundle.IDGROUP);
        databaseTrip = FirebaseDatabase.getInstance();
        listenerTrip = databaseTrip.getReference(String.valueOf(idGroup)).child("listener").child("reloadTrip");
    }

    private TripReponseDTO getTripResponseDTOFromForm() {
        dateEndTrip = txtEditTripDateEnd.getText().toString();
        dateStartTrip = txtEditTripDateStart.getText().toString();
        timeStartTrip = txtEditTimeStart.getText().toString();
        timeEndTrip = txtEditTimeEnd.getText().toString();
        String dayStrat = dateStartTrip + " " + timeStartTrip;
        String dayEnd = dateEndTrip + " " + timeEndTrip + ":59";

        String namePlaceStart = edtEditTripName.getText().toString();
        TripReponseDTO tripReponseDTO = new TripReponseDTO();
        tripReponseDTO.setId(id);
        tripReponseDTO.setStartAt(ZonedDateTimeUtil.convertStringToDateOrTime(dayStrat));
        tripReponseDTO.setEndAt(ZonedDateTimeUtil.convertStringToDateOrTime(dayEnd));


        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setName(namePlaceStart);
        if (placeID == null) {
            placeDTO.setGooglePlaceId(placeIdEdit);
        } else {
            placeDTO.setGooglePlaceId(placeID);
        }
        tripReponseDTO.setStartPlace(placeDTO);
        return tripReponseDTO;
    }


    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }
        edtEditTripName.setFocusable(false);
        edtEditTripName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTripActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 1);
                startActivityForResult(intent, EDIT_TRIP_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_TRIP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = (PlaceDTO) data.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
                edtEditTripName.setText(place.getName());
                placeID = place.getGooglePlaceId();
                txtPlaceAddress.setText(place.getAddress());
                txtPlaceTrip.setText(place.getName());
                timeZone = place.getTimeZone();
                if (place.getPlaceImageList().size() != 0) {
                    uriPlace = place.getPlaceImageList().get(0).getUri();
                    ImageLoaderUtil.loadImage(this, uriPlace, imgTripImageUpdate);
                } else {
                    Picasso.get().load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(imgTripImageUpdate);
                }


                try {
                    if (!place.getTimeZone().equals(timeZoneJourney)) {
                        lnlTimeZonePoint.setVisibility(View.VISIBLE);
                    } else {
                        lnlTimeZonePoint.setVisibility(View.GONE);
                    }
                    txtTimeZone.setText(place.getTimeZone());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                calGo = null;
                calEnd = null;
                timeEnd = null;
                timeStart = null;

                calGoUTC = null;
                calEndUTC = null;
                timeEndUTC = null;
                timeStartUTC = null;

                txtEditTripDateStart.setText("Day Start");
                txtEditTimeStart.setText("Time Start");
                txtEditTripDateEnd.setText("Day End");
                txtEditTimeEnd.setText("Time End");

                txtEditDateStartUTC.setText("Day Start");
                txtEditTimeStartUTC.setText("Time Start");
                txtEditTimeEndUTC.setText("Day End");
                txtEditDateEndUTC.setText("Time End");
            }

        }
    }


    public void getTimeStart() {
        String dateStart = txtEditTripDateStart.getText().toString();
        if (mTrip.getStartPlace().getTimeZone() == null) {
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
                txtEditTimeStart.setText(timeStart);

                timeStartTrip = txtEditTimeStart.getText().toString();
                dateStartTrip = txtEditTripDateStart.getText().toString();
                String dayStrat = dateStartTrip + " " + timeStartTrip;
                Date tripStart = ZonedDateTimeUtil.convertStringToDateOrTime(dayStrat);
                if (place != null) {
                    Date timezoneStratUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStart, place.getTimeZone());
                    Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratUtcTrip, timeZoneJourney);
                    txtEditDateStartUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratUtcJourey));
                    txtEditTimeStartUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratUtcJourey));
                } else {
                    Date timezoneStratUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStart, mTrip.getStartPlace().getTimeZone());
                    Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratUtcTrip, timeZoneJourney);
                    txtEditDateStartUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratUtcJourey));
                    txtEditTimeStartUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratUtcJourey));
                }

            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    public void getTimeEnd() {
        String dateEnd = txtEditTripDateEnd.getText().toString();
        if (mTrip.getStartPlace().getTimeZone() == null) {
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
                txtEditTimeEnd.setText(timeEnd);
                dateEndTrip = txtEditTripDateEnd.getText().toString();
                timeEnd = txtEditTimeEnd.getText().toString();
                String dayEnd = dateEndTrip + " " + timeEnd;
                Date tripEnd = ZonedDateTimeUtil.convertStringToDateOrTime(dayEnd);
                if (place != null) {
                    Date timezoneEndUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEnd, place.getTimeZone());
                    Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndUtcTrip, timeZoneJourney);
                    txtEditDateEndUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndUtcJourey));
                    txtEditTimeEndUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndUtcJourey));
                } else {
                    Date timezoneEndUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEnd, mTrip.getStartPlace().getTimeZone());
                    Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndUtcTrip, timeZoneJourney);
                    txtEditDateEndUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndUtcJourey));
                    txtEditTimeEndUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndUtcJourey));
                }

            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }


    public void getDateGo() {
        if (mTrip.getStartPlace().getTimeZone() == null) {
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
                        txtEditTripDateStart.setText(calGo);
                        if (timeStart == null) {
                            txtEditTimeStart.setText("00:00");
                        }
                        timeStartTrip = txtEditTimeStart.getText().toString();
                        dateStartTrip = txtEditTripDateStart.getText().toString();
                        String dayStrat = dateStartTrip + " " + timeStartTrip;
                        Date tripStart = ZonedDateTimeUtil.convertStringToDateOrTime(dayStrat);
                        if (place != null) {
                            Date timezoneStratUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStart, place.getTimeZone());
                            Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratUtcTrip, timeZoneJourney);
                            txtEditDateStartUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratUtcJourey));
                            txtEditTimeStartUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratUtcJourey));
                        } else {
                            Date timezoneStratUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStart, mTrip.getStartPlace().getTimeZone());
                            Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratUtcTrip, timeZoneJourney);
                            txtEditDateStartUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratUtcJourey));
                            txtEditTimeStartUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratUtcJourey));
                        }

                    }
                }, mYear, mMonth, mDay);

        try {
            if (mTrip.getStartPlace().getTimeZone() != null) {
                Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoUTC);
                Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndGUTC);
                if (place != null) {
                    Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, place.getTimeZone());
                    Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, place.getTimeZone());
                    datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                    datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
                } else {
                    Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, mTrip.getStartPlace().getTimeZone());
                    Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, mTrip.getStartPlace().getTimeZone());
                    datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                    datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
                }


            } else {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
        }
        datePickerDialog.show();
    }

    public void getDateEnd() {
        if (mTrip.getStartPlace().getTimeZone() == null) {
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
                        txtEditTripDateEnd.setText(calEnd);

                        if (timeEnd == null) {
                            txtEditTimeEnd.setText("23:59");
                        }
                        dateEndTrip = txtEditTripDateEnd.getText().toString();
                        timeEnd = txtEditTimeEnd.getText().toString();
                        String dayEnd = dateEndTrip + " " + timeEnd;
                        Date tripEnd = ZonedDateTimeUtil.convertStringToDateOrTime(dayEnd);
                        if (place != null) {
                            Date timezoneEndUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEnd, place.getTimeZone());
                            Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndUtcTrip, timeZoneJourney);
                            txtEditDateEndUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndUtcJourey));
                            txtEditTimeEndUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndUtcJourey));
                        } else {
                            Date timezoneEndUtcTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEnd, mTrip.getStartPlace().getTimeZone());
                            Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndUtcTrip, timeZoneJourney);
                            txtEditDateEndUTC.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndUtcJourey));
                            txtEditTimeEndUTC.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndUtcJourey));
                        }

                    }

                }, mYear, mMonth, mDay);
        try {
            if (mTrip.getStartPlace().getTimeZone() != null) {
                Date dateGoUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateGoUTC);
                Date dateEndUtcObject = ZonedDateTimeUtil.convertStringToDateOrTime(dateEndGUTC);
                if (place != null) {
                    Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, place.getTimeZone());
                    Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, place.getTimeZone());
                    datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                    datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
                } else {
                    Date convertedDateGo = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateGoUtcObject, mTrip.getStartPlace().getTimeZone());
                    Date convertedDateEnd = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEndUtcObject, mTrip.getStartPlace().getTimeZone());
                    datePickerDialog.getDatePicker().setMinDate(convertedDateGo.getTime());
                    datePickerDialog.getDatePicker().setMaxDate(convertedDateEnd.getTime());
                }
            } else {
                datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
        }
        datePickerDialog.show();

    }


    public void getTimeStartUTC() {
        String dateStartUtc = txtEditDateStartUTC.getText().toString();
        if (mTrip.getStartPlace().getTimeZone() == null) {
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
                txtEditTimeStartUTC.setText(timeStartUTC);

                timeStartTripUTC = txtEditTimeStartUTC.getText().toString();
                dateStartTripUTC = txtEditDateStartUTC.getText().toString();
                String dayStratUTC = dateStartTripUTC + " " + timeStartTripUTC;
                Date tripStartUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayStratUTC);
                Date timezoneStratTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStartUTC, timeZoneJourney);
                if (place != null) {
                    Date timezoneStratJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratTrip, place.getTimeZone());
                    txtEditTripDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratJourey));
                    txtEditTimeStart.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratJourey));
                } else {
                    Date timezoneStratJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratTrip, mTrip.getStartPlace().getTimeZone());
                    txtEditTripDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratJourey));
                    txtEditTimeStart.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratJourey));
                }
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    public void getTimeEndUTC() {
        String dateEndUtc = txtEditDateEndUTC.getText().toString();
        if (mTrip.getStartPlace().getTimeZone() == null) {
            Toast.makeText(this, "Choose City first!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dateEndUtc.length() < 10) {
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
                txtEditTimeEndUTC.setText(timeEndUTC);

                timeEndTripUTC = txtEditTimeEndUTC.getText().toString();
                dateEndTripUTC = txtEditDateEndUTC.getText().toString();
                String dayEndUTC = dateEndTripUTC + " " + timeEndTripUTC;
                Date tripEndUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayEndUTC);
                Date timezoneEndTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEndUTC, timeZoneJourney);
                if (place != null) {
                    Date timezoneEndJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndTrip, place.getTimeZone());
                    txtEditTripDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndJourey));
                    txtEditTimeEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndJourey));
                } else {
                    Date timezoneEndJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndTrip, mTrip.getStartPlace().getTimeZone());
                    txtEditTripDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndJourey));
                    txtEditTimeEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndJourey));
                }
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }


    public void getDateGoUTC() {
        if (mTrip.getStartPlace().getTimeZone() == null) {
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
                        txtEditDateStartUTC.setText(calGoUTC);
                        if (timeStartUTC == null) {
                            txtEditTimeStartUTC.setText("00:00");
                        }

                        timeStartTripUTC = txtEditTimeStartUTC.getText().toString();
                        dateStartTripUTC = txtEditDateStartUTC.getText().toString();
                        String dayStratUTC = dateStartTripUTC + " " + timeStartTripUTC;
                        Date tripStartUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayStratUTC);
                        Date timezoneStratTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripStartUTC, timeZoneJourney);
                        if (place != null) {
                            Date timezoneStratJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratTrip, place.getTimeZone());
                            txtEditTripDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratJourey));
                            txtEditTimeStart.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratJourey));
                        } else {
                            Date timezoneStratJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneStratTrip, mTrip.getStartPlace().getTimeZone());
                            txtEditTripDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneStratJourey));
                            txtEditTimeStart.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneStratJourey));
                        }


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
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
        }
        datePickerDialog.show();
    }

    public void getDateEndUTC() {
        if (mTrip.getStartPlace().getTimeZone() == null) {
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
                        txtEditDateEndUTC.setText(calEndUTC);

                        if (timeEndUTC == null) {
                            txtEditTimeEndUTC.setText("23:59");
                        }
                        timeEndTripUTC = txtEditTimeEndUTC.getText().toString();
                        dateEndTripUTC = txtEditDateEndUTC.getText().toString();
                        String dayEndUTC = dateEndTripUTC + " " + timeEndTripUTC;
                        Date tripEndUTC = ZonedDateTimeUtil.convertStringToDateOrTime(dayEndUTC);
                        Date timezoneEndTrip = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(tripEndUTC, timeZoneJourney);

                        if (place != null) {
                            Date timezoneEndJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndTrip, place.getTimeZone());
                            txtEditTripDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndJourey));
                            txtEditTimeEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndJourey));
                        } else {
                            Date timezoneEndJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(timezoneEndTrip, mTrip.getStartPlace().getTimeZone());
                            txtEditTripDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(timezoneEndJourey));
                            txtEditTimeEnd.setText(ZonedDateTimeUtil.convertDateToStringTime(timezoneEndJourey));
                        }

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
                datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
            }
        } catch (Exception e) {
            datePickerDialog.getDatePicker().setMinDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateGo).getTime());
            datePickerDialog.getDatePicker().setMaxDate(ZonedDateTimeUtil.convertStringToDateOrTime(dateEndG).getTime());
        }
        datePickerDialog.show();

    }

    private boolean isValidDateRange() {
        dateEndTrip = txtEditTripDateEnd.getText().toString();
        dateStartTrip = txtEditTripDateStart.getText().toString();
        timeStartTrip = txtEditTimeStart.getText().toString();
        timeEndTrip = txtEditTimeEnd.getText().toString();
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

        for (int i = 0; i < tripReponseDTOList.size(); i++) {
            if (tripReponseDTOList.get(i).getId().equals(mTrip.getId())) {
                tripReponseDTOList.remove(i);
                break;
            }
        }
        for (TripReponseDTO tripReponseDTO : tripReponseDTOList) {
            Date stratTripTimeZone = tripReponseDTO.getStartUtcAt();
            Date endTripTimeZone = tripReponseDTO.getEndUtcAt();
            if (pickedStartDate.getTime() < stratTripTimeZone.getTime()
                    && pickedEndDate.getTime() < stratTripTimeZone.getTime()) {
            } else if (pickedStartDate.getTime() > endTripTimeZone.getTime()
                    && pickedEndDate.getTime() > endTripTimeZone.getTime()) {
            } else {
                DialogShowErrorMessage.showValidationDialog(this, "You have planned on this days");
                return false;
            }
        }
        return true;
    }


    public void clickUpdateTrip() {

        try {
            if (!isValidDateRange() || !checkNull()) {
                return;
            }
            mUpdateTripPresenter = new UpdateTripPresenter(this, this);
            if (place != null) {
                if (!mTrip.getStartPlace().getGooglePlaceId().equals(place.getGooglePlaceId())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("The data in this city will be delete you still want to update?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mUpdateTripPresenter.updateTrip(getTripResponseDTOFromForm());
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    mUpdateTripPresenter.updateTrip(getTripResponseDTOFromForm());
                }

            } else {
                mUpdateTripPresenter.updateTrip(getTripResponseDTOFromForm());
            }
        } catch (Exception e) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick DateTime");
            e.printStackTrace();
        }


    }

    private boolean checkNull() {
        String dateStart = txtEditTripDateStart.getText().toString();
        String dateEnd = txtEditTripDateEnd.getText().toString();
        String namePlaceTrip = edtEditTripName.getText().toString();
        if (namePlaceTrip.length() == 0 && namePlaceTrip.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please choose Place");
            return false;
        } else if (dateStart.length() == 0 && dateStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick Day Start");
            return false;
        } else if (dateEnd.length() == 0 && dateEnd.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick Day End");
            return false;
        } else {
            return true;
        }
    }

    public void clickDeleteTrip() {
        mDeleteTripPresenter = new DeleteTripPresenter(this, this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this City?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteTripPresenter.deleteTrip(id);
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.lnlEditDateTripStart:
                getDateGo();
                break;
            case R.id.lnlEditDateTripEnd:
                getDateEnd();
                break;

            case R.id.lnlEditTimeStart:
                getTimeStart();
                break;
            case R.id.lnlEditTimeEnd:
                getTimeEnd();
                break;

            case R.id.lnlEditDateStartUTC:
                getDateGoUTC();
                break;
            case R.id.lnlDateEndUTC:
                getDateEndUTC();
                break;

            case R.id.lnlEditTimeStartUTC:
                getTimeStartUTC();
                break;
            case R.id.lnlEditTimeEndUTC:
                getTimeEndUTC();
                break;

            case R.id.imgUpdateCitySubmit:
                clickUpdateTrip();
                break;
            case R.id.imgDeleteTrip:
                clickDeleteTrip();
                break;
            case R.id.imgBackEditTrip:
                finish();
                break;

        }

    }

    @Override
    public void updateTripSuccess(String messageSuccess) {
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
    public void updateTripFail(String mesage) {
        DialogShowErrorMessage.showValidationDialog(this, mesage);
    }

    @Override
    public void deleteTripSuccess(String message) {
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
    public void deleteTripFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(this, messageFail);
    }
}