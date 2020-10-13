package com.fpt.gta.feature.managegroup.editgroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.feature.managetransaction.edittransaction.CurrencyActivity;
import com.fpt.gta.feature.searchautocomplete.PlaceAutoCompleteActivity;
import com.fpt.gta.presenter.UpdateGroupPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.UpdateGroupView;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditGroupActivity extends AppCompatActivity implements View.OnClickListener, UpdateGroupView {
    private TextInputEditText txtEditGroupName, txtEditPointStart;
    private ImageView imgEditGroupSubmit;
    private TextInputEditText txtEditDateStart, txtEditDateEnd;
    private int mYear, mMonth, mDay;
    private Calendar calendarStart, calendarEnd;
    SimpleDateFormat simpleDateFormat;
    private GroupResponseDTO mGroup;
    private String placeGoodleID, placeEdit;
    private Integer id;
    private UpdateGroupPresenter mUpdateGroupPresenter;
    private ImageView imgBackEditGroup;
    private static final int EDIT_GROUP_ACTIVITY_REQUEST_CODE = 0;
    private PlaceDTO place;
    private Date dateGo, dateEnd;
    private TextInputEditText txtEditCurrency;
    private CurrencyDTO currencyDTO;
    private String calEndTime;
    private SimpleDateFormat simpleDateTimeFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        initView();
        initData();
        initPlace();
    }

    public void initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GTABundle.KEYGOOGLE);
        }

        txtEditPointStart.setFocusable(false);
        txtEditPointStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditGroupActivity.this, PlaceAutoCompleteActivity.class);
                intent.putExtra(GTABundle.SEARCHTYPE, 1);
                startActivityForResult(intent, EDIT_GROUP_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_GROUP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = (PlaceDTO) data.getSerializableExtra(GTABundle.RESUTLPLACEEDITTRIP);
                txtEditPointStart.setText(place.getName());
                placeGoodleID = place.getGooglePlaceId();
            }
        }if (requestCode == GTABundle.EDIT_GROUP_CURRENCY_CODE){
            if (resultCode == RESULT_OK){
                currencyDTO = (CurrencyDTO) data.getSerializableExtra(GTABundle.EDIT_GROUP_CURRENCY);
                txtEditCurrency.setText(currencyDTO.getCode());
            }
        }
    }

    public void initView() {
        Bundle bundle = getIntent().getExtras();
        mGroup = (GroupResponseDTO) bundle.getSerializable(GTABundle.KEY);
        currencyDTO = mGroup.getCurrency();
        txtEditGroupName = findViewById(R.id.txtEditGroupName);
        txtEditCurrency = findViewById(R.id.txtEditCurrency);
        txtEditDateEnd = findViewById(R.id.txtEditDateEnd);
        txtEditDateStart = findViewById(R.id.txtEditDateStart);
        txtEditPointStart = findViewById(R.id.txtEditPointStart);
        imgEditGroupSubmit = findViewById(R.id.imgEditGroupSubmit);
        txtEditCurrency.setText(currencyDTO.getCode());
        imgBackEditGroup = findViewById(R.id.imgBackEditGroup);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateTimeFormat = new SimpleDateFormat("dd-MM-yyyy 23:59:59");
    }

    public void initData() {
        txtEditCurrency.setFocusable(false);
        txtEditCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditGroupActivity.this, CurrencyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CALLING_ACTIVITY", "EditGroupActivity");
                bundle.putSerializable(GTABundle.EDIT_GROUP_CURRENCY, currencyDTO);
                intent.putExtras(bundle);
                startActivityForResult(intent, GTABundle.EDIT_GROUP_CURRENCY_CODE);
            }
        });
        imgEditGroupSubmit.setOnClickListener(this::onClick);
        txtEditDateStart.setFocusable(false);
        txtEditDateStart.setOnClickListener(this::onClick);

        txtEditDateEnd.setFocusable(false);
        txtEditDateEnd.setOnClickListener(this::onClick);

        imgBackEditGroup.setOnClickListener(this::onClick);
        id = mGroup.getId();
        placeEdit = mGroup.getStartPlace().getGooglePlaceId();
        txtEditGroupName.setText(mGroup.getName() + "");
        txtEditPointStart.setText(mGroup.getStartPlace().getName() + "");
        dateGo = mGroup.getStartAt();
        txtEditDateStart.setText(ZonedDateTimeUtil.convertDateToStringASIA(dateGo));
        dateEnd = mGroup.getEndAt();
        txtEditDateEnd.setText(ZonedDateTimeUtil.convertDateToStringASIA(dateEnd));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgEditGroupSubmit:
                clickUpdateGroup();
                break;
            case R.id.txtEditDateStart:
                getDateGo();
                break;
            case R.id.txtEditDateEnd:
                getDateEnd();
                break;
            case R.id.imgBackEditGroup:
                finish();
                break;
        }
    }

    public void clickUpdateGroup() {
        if (!checkNull()) {
            return;
        } else {
            mUpdateGroupPresenter = new UpdateGroupPresenter(this, this);
            mUpdateGroupPresenter.updateGroup(getGroupResponseDTOFromForm());
        }
    }


    private boolean checkNull() {
        String nameGroup = txtEditGroupName.getText().toString();
        String namePlaceStart = txtEditPointStart.getText().toString();
        String dateStart = txtEditDateStart.getText().toString();
        String dateEnds = txtEditDateEnd.getText().toString();
        Date pickedStartDate =  ZonedDateTimeUtil.convertStringToDateOrTime(dateStart);
        Date pickedEndDate = ZonedDateTimeUtil.convertStringToDateOrTime(dateEnds);
        if (pickedStartDate.getTime() > pickedEndDate.getTime()) {
            DialogShowErrorMessage.showValidationDialog(this,"Start day must be before end day");
            return false;
        }
        else if (nameGroup.length() == 0 && nameGroup.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this,"Please input Name Group");
            return false;
        } else if (namePlaceStart.length() == 0 && namePlaceStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this,"Please choose Place Start");
            return false;
        } else if (dateStart.length() == 0 && dateStart.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this,"Please pick Day Start");
            return false;
        } else if (dateEnds.length() == 0 && dateEnds.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(this,"Please pick Day End");
            return false;
        }else {
            return true;
        }

    }


    private GroupResponseDTO getGroupResponseDTOFromForm() {
        String nameGroup = txtEditGroupName.getText().toString();
        String namePlaceStart = txtEditPointStart.getText().toString();
        String dateStart = txtEditDateStart.getText().toString();
        String dateEnd = txtEditDateEnd.getText().toString();
        GroupResponseDTO groupResponseDTO = new GroupResponseDTO();
        groupResponseDTO.setName(nameGroup);
        groupResponseDTO.setId(id);
        groupResponseDTO.setCurrency(currencyDTO);
        groupResponseDTO.setStartAt(ZonedDateTimeUtil.convertStringToDateOrTime(dateStart));
        if(calEndTime != null){
            groupResponseDTO.setEndAt(ZonedDateTimeUtil.convertStringToDateOrTime(calEndTime));
        }else {
            groupResponseDTO.setEndAt(mGroup.getEndAt());
        }

        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setName(namePlaceStart);
        if (placeGoodleID == null) {
            placeDTO.setGooglePlaceId(placeEdit);
        } else {
            placeDTO.setGooglePlaceId(placeGoodleID);
        }
        groupResponseDTO.setStartPlace(placeDTO);
        return groupResponseDTO;
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
                        txtEditDateStart.setText(simpleDateFormat.format(calendarStart.getTime()));
                    }
                }, mYear, mMonth, mDay);
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
                            calEndTime = simpleDateTimeFormat.format(calendarEnd.getTime());
                            txtEditDateEnd.setText(simpleDateFormat.format(calendarEnd.getTime()));
                        }

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

    }




    @Override
    public void updateGroupSuccess(String messageSuccess) {
        finish();
    }

    @Override
    public void updateGroupFail(String message) {
        DialogShowErrorMessage.showValidationDialog(this, message);
    }

}