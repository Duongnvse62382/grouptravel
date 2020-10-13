package com.fpt.gta.feature.managegroup.addgroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.LatLongDTO;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.feature.managetransaction.edittransaction.CurrencyActivity;
import com.fpt.gta.feature.managetrip.overviewtrip.TripOverviewActivity;
import com.fpt.gta.feature.searchautocomplete.PlaceAutoCompleteActivity;
import com.fpt.gta.presenter.CreateGroupPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateGroupView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddGroupActivity extends AppCompatActivity implements View.OnClickListener, CreateGroupView {
    private TextInputEditText edtGroupCreateDateStart, edtGroupCreateDateEnd, edtAddDateEnd;
    private ImageView imgAddGroupSubmit;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart, calendarEnd;
    SimpleDateFormat simpleDateFormat;
    private LinearLayout lnlGroupDateStart, lnlGroupDateEnd;
    private ImageView imgDetailsActivityBack;
    private CreateGroupPresenter mCreateGroupPresenter;
    private TextInputEditText edtPlaceStart, edtAddGroupName;
    private String placeID;
    private LinearLayout lnlAddCurrencyMoney;
    private CurrencyDTO currencyDTO;
    private TextInputEditText edtAddGroupCurrency;

    private String nameGroup;
    private String namePlaceStart;
    private String dateStart;
    private String dateEnd;
    private static final int ADD_GROUP_ACTIVITY_REQUEST_CODE = 123;
    private String calEndTime;
    private SimpleDateFormat simpleDateTimeFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        initView();
        initData();
        initPlace();
    }

    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }

        edtPlaceStart.setFocusable(false);
        edtPlaceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddGroupActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 1);
                startActivityForResult(intent, ADD_GROUP_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    public void initView() {

        edtGroupCreateDateStart = findViewById(R.id.txtGroupCreateDateStart);
        edtAddGroupCurrency = findViewById(R.id.txtAddGroupCurrency);
        edtAddGroupCurrency.setFocusable(false);
        lnlAddCurrencyMoney = findViewById(R.id.lnlAddCurrencyMoney);
        edtGroupCreateDateEnd = findViewById(R.id.txtGroupCreateDateEnd);
        lnlGroupDateStart = findViewById(R.id.lnlGroupDateStart);
        lnlGroupDateEnd = findViewById(R.id.lnlGroupDateEnd);
        edtAddDateEnd = findViewById(R.id.txtGroupCreateDateEnd);
        imgAddGroupSubmit = findViewById(R.id.imgAddGroupSubmit);
        imgDetailsActivityBack = findViewById(R.id.imgDetailsActivityBack);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateTimeFormat = new SimpleDateFormat("dd-MM-yyyy 23:59:59");
        edtPlaceStart = findViewById(R.id.edtAddPointStart);
        edtAddGroupName = findViewById(R.id.edtAddGroupName);

        edtGroupCreateDateStart.setFocusable(false);
        edtGroupCreateDateEnd.setFocusable(false);
        edtAddGroupCurrency.setFocusable(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_GROUP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PlaceDTO place;
                place = (PlaceDTO) data.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
                namePlaceStart = place.getName();
                edtPlaceStart.setText(namePlaceStart);
                placeID = place.getGooglePlaceId();
            }
        }else if (requestCode == GTABundle.ADD_GROUP_CURRENCY_CODE){
            if (resultCode == RESULT_OK){
                currencyDTO = (CurrencyDTO) data.getSerializableExtra(GTABundle.ADD_GROUP_CURRENCY);
                edtAddGroupCurrency.setText(currencyDTO.getCode());
            }
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
                        edtGroupCreateDateStart.setText(simpleDateFormat.format(calendarStart.getTime()));
                        dateStart = edtGroupCreateDateStart.getText().toString();
                        try {
                            sumDay();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void getDateEnd() {
        if (!validDateStart()) {
            edtGroupCreateDateEnd.setText("");
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
                            calEndTime = simpleDateTimeFormat.format(calendarEnd.getTime());
                            try {
                                sumDay();
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
            datePickerDialog.show();
        }
    }

    public void sumDay() {
        int sum = (int) ((calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis()) / (1000 * 60 * 60 * 24));
        if (sum < 0) {
            edtGroupCreateDateEnd.setText("");
        } else {
            edtGroupCreateDateEnd.setText(simpleDateFormat.format(calendarEnd.getTime()));
            dateEnd = edtGroupCreateDateEnd.getText().toString();
        }
    }


    private boolean checkNull() {
        String nameGroup = edtAddGroupName.getText().toString();
        String namePlaceStart = edtPlaceStart.getText().toString();
        String dateStart = edtGroupCreateDateStart.getText().toString().trim();
        String dateEnd = edtGroupCreateDateEnd.getText().toString().trim();
        String addCurrency = edtAddGroupCurrency.getText().toString().trim();
        if (nameGroup.length() == 0 && nameGroup.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please input Name Journey");
            return false;
        } else if (namePlaceStart.length() == 0 && namePlaceStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please choose Place Start");
            return false;
        } else if (dateStart.length() == 0 && dateStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick Day Start");
            return false;
        } else if (dateEnd.length() == 0 && dateEnd.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick Day End");
            return false;
        }else if (addCurrency.length() == 0 && addCurrency.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this, "Please pick Journey currency");
            return false;
        } else {
            return true;
        }
    }


    public boolean validDateStart() {
        String dateStart = edtGroupCreateDateStart.getText().toString().trim();
        if (dateStart.isEmpty()) {
            Toast.makeText(this, "Please pick Day start!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    public void initData() {
        edtAddGroupCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddGroupActivity.this, CurrencyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "AddGroupActivity");
                bundle.putSerializable(GTABundle.ADD_GROUP_CURRENCY, currencyDTO);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.ADD_GROUP_CURRENCY_CODE);
            }
        });
        imgDetailsActivityBack.setOnClickListener(this);
        edtGroupCreateDateStart.setOnClickListener(this::onClick);
        edtGroupCreateDateEnd.setOnClickListener(this::onClick);
        imgAddGroupSubmit.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgDetailsActivityBack:
                finish();
                break;
            case R.id.txtGroupCreateDateStart:
                getDateGo();
                break;
            case R.id.txtGroupCreateDateEnd:
                getDateEnd();
                break;
            case R.id.imgAddGroupSubmit:
                clickCreateGroup();
                break;
        }
    }

    public void clickCreateGroup() {
        if (!checkNull()) {
            return;
        } else {
            mCreateGroupPresenter = new CreateGroupPresenter(this, this);
            mCreateGroupPresenter.createGroup(getGroupResponseDTOFromForm());
        }

    }


    private GroupResponseDTO getGroupResponseDTOFromForm() {
        nameGroup = edtAddGroupName.getText().toString();
        namePlaceStart = edtPlaceStart.getText().toString();
        dateStart = edtGroupCreateDateStart.getText().toString();
        dateEnd = edtGroupCreateDateEnd.getText().toString();
        GroupResponseDTO groupResponseDTO = new GroupResponseDTO();
        groupResponseDTO.setName(nameGroup);
        groupResponseDTO.setStartAt(ZonedDateTimeUtil.convertStringToDateOrTime(dateStart));
        groupResponseDTO.setEndAt(ZonedDateTimeUtil.convertStringToDateOrTime(calEndTime));
        groupResponseDTO.setCurrency(currencyDTO);
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setName(namePlaceStart);
        placeDTO.setGooglePlaceId(placeID);
        groupResponseDTO.setStartPlace(placeDTO);
        return groupResponseDTO;
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

    public void SendGroupCurrency(CurrencyDTO currencyDTO) {
        Gson gson = new Gson();
        String groupCurrency = gson.toJson(currencyDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.GROUP_CURRENCY_SHARE, groupCurrency);
    }

    @Override
    public void createGroupSuccess(GroupResponseDTO groupResponseDTO) {
        String timeZoneStart = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getStartAt());
        String timeZoneEnd = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getEndAt());
        String timeZoneStartUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getStartUtcAt());
        String timeZoneEndUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getEndUtcAt());
        String timezone = groupResponseDTO.getStartPlace().getTimeZone();
        LatLongDTO latLongDTO = new LatLongDTO(groupResponseDTO.getStartPlace().getName(), groupResponseDTO.getStartPlace().getLatitude().doubleValue(), groupResponseDTO.getStartPlace().getLongitude().doubleValue());
        Gson gson = new Gson();
        String json = gson.toJson(latLongDTO);
        SendLatLong(json);
        SendDateGo(timeZoneStart);
        SendDateEnd(timeZoneEnd);
        SendDateGoUTC(timeZoneStartUTC);
        SendDateEndUTC(timeZoneEndUTC);
        SendTimeZone(timezone);
        SendData(groupResponseDTO.getId());
        Intent intent1 = new Intent(this, TripOverviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYGROUP, groupResponseDTO);
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.IDGROUP, groupResponseDTO.getId());
        CurrencyDTO currencyDTOAdd = groupResponseDTO.getCurrency();
        SendGroupCurrency(currencyDTOAdd);
        String journeyDTOS = gson.toJson(groupResponseDTO);
        SharePreferenceUtils.saveStringSharedPreference(this, GTABundle.JOURNEYOJECT, journeyDTOS);
        Integer groupStatus = groupResponseDTO.getIdStatus();
        SharePreferenceUtils.saveIntSharedPreference(this, GTABundle.GROUPOBJECTSTATUS, groupStatus);
        intent1.putExtras(bundle);
        startActivity(intent1);
        finish();
    }

    @Override
    public void CreateGroupFail(String message) {
        DialogShowErrorMessage.showValidationDialog(this, message);
    }
}