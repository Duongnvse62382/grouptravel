package com.fpt.gta.feature.manageplan.overviewplan.planelected;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.PlanStatus;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.presenter.ChangeVoteDeadlinePresenter;
import com.fpt.gta.presenter.PrintPlanInTripPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.ChangeVoteDeadlineView;
import com.fpt.gta.view.PrintPlanInTripView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class PlanElectedFragment extends Fragment implements View.OnClickListener, PrintPlanInTripView, ChangeVoteDeadlineView {
    private ViewPager viewPager;
    private PlanElectedAdapter planApdater;
    private View mView;
    private List<PlanDTO> plansDTOList;
    private PrintPlanInTripPresenter mPrintPlanInTripPresenter;
    private ChangeVoteDeadlinePresenter mChangeVoteDeadlinePresenter;
    private ImageView imgChangeVotePlan;
    private Integer iTrip;
    private String dayEndvote;

    private Calendar calendarVote, calendarTimeVote;
    SimpleDateFormat simpleDateFormat, simpleTimeFormat;
    private int mYear, mMonth, mDay;
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private String dateVote;
    private String timeVote;
    private Integer idGroup;
    private FirebaseDatabase databasePlan;
    private DatabaseReference listenerPlan;
    private DatabaseReference listenerPlanElected;
    private ValueEventListener planValueEventListener;
    private ValueEventListener planElectedValueEventListener;

    public PlanElectedFragment() {

    }


    public static PlanElectedFragment newInstance() {
        PlanElectedFragment fragment = new PlanElectedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plan_elected, container, false);
        return mView;
    }

    public void initView() {
        viewPager = mView.findViewById(R.id.viewPager);
        imgChangeVotePlan = mView.findViewById(R.id.imgChangeVotePlan);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");
        iTrip = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDTRIP);
        imgChangeVotePlan.setVisibility(View.INVISIBLE);
        idGroup = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        databasePlan = FirebaseDatabase.getInstance();
        listenerPlan = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlan");
        listenerPlanElected = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlanElected");
    }

    public void initData() {
        Integer idAdmin = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.ISADMIN);
        Integer groupStatus = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.GROUPSTATUS);
        if(!groupStatus.equals(GroupStatus.PLANNING)){
            imgChangeVotePlan.setVisibility(View.INVISIBLE);
        }else {
            if (idAdmin.equals(MemberRole.ADMIN)) {
                imgChangeVotePlan.setVisibility(View.VISIBLE);
            } else {
                imgChangeVotePlan.setVisibility(View.INVISIBLE);
            }
        }

        imgChangeVotePlan.setOnClickListener(this::onClick);
        int colors_temp = getResources().getColor(R.color.colorTedarari);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.setBackgroundColor(colors_temp);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mPrintPlanInTripPresenter = new PrintPlanInTripPresenter(getContext(), this);

        planValueEventListener = listenerPlan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintPlanInTripPresenter.printPlanInTrip(iTrip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mChangeVoteDeadlinePresenter = new ChangeVoteDeadlinePresenter(getContext(), PlanElectedFragment.this);
    }

    public void updateUI() {
        if (planApdater == null) {
            planApdater = new PlanElectedAdapter(getContext(), plansDTOList);
            viewPager.setAdapter(planApdater);
            viewPager.setPadding(90, 0, 120, 0);
            planApdater.setOnPlanClickListener(new PlanElectedAdapter.OnPlanClickListener() {
                @Override
                public void onPlanClickListener(PlanDTO planDTO, int position) {

                }
            });
        } else {
            planApdater.notifyChangeDataElected(plansDTOList);
        }

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachListener();
    }

    private void detachListener(){
        try {
            if (planValueEventListener != null) {
                listenerPlan.removeEventListener(planValueEventListener);
            }
            if (planElectedValueEventListener != null) {
                listenerPlanElected.removeEventListener(planElectedValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgChangeVotePlan:
                showChangeVotePlanDialog();
                break;
        }
    }

    private void showChangeVotePlanDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_dialog_voteplan);
        EditText edtChangeVoteDay = dialog.findViewById(R.id.edtChangeVoteDay);
        EditText edtChangeVoteTime = dialog.findViewById(R.id.edtChangeVoteTime);
        Button btnChangeVoteTime = dialog.findViewById(R.id.btnChangeVoteTime);
        edtChangeVoteDay.setFocusable(false);
        edtChangeVoteDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarVote = Calendar.getInstance();
                mDay = calendarVote.get(Calendar.DATE);
                mMonth = calendarVote.get(Calendar.MONTH);
                mYear = calendarVote.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int dayOfMonth,
                                                  int monthOfYear, int year) {
                                calendarVote.set(dayOfMonth, monthOfYear, year);
                                dateVote = simpleDateFormat.format(calendarVote.getTime());
                                edtChangeVoteDay.setText(dateVote);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        edtChangeVoteTime.setFocusable(false);
        edtChangeVoteTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarTimeVote = Calendar.getInstance();
                int hour = calendarTimeVote.get(Calendar.HOUR_OF_DAY);
                int minutes = calendarTimeVote.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        calendarTimeVote.set(Calendar.HOUR_OF_DAY, i);
                        calendarTimeVote.set(Calendar.MINUTE, i1);
                        timeVote = simpleTimeFormat.format(calendarTimeVote.getTime());
                        edtChangeVoteTime.setText(timeVote);
                    }
                }, hour, minutes, true);
                timePickerDialog.show();
            }
        });

        btnChangeVoteTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeVotes = edtChangeVoteTime.getText().toString();
                String dateVotes = edtChangeVoteDay.getText().toString();
                try {
                    TimeZone tz = TimeZone.getDefault();
                    String timez = tz.getID();
                    if (!checkNull(dateVotes, timeVotes)) {
                        return;
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
                        dayEndvote = dateVotes + " " + timeVotes;
                        Date dateEndVotePlan = simpleDateFormat.parse(dayEndvote);


                        if (dateEndVotePlan.getTime() <= System.currentTimeMillis()) {
                            DialogShowErrorMessage.showValidationDialog(getContext(), "End vote time after current time");
                        } else {
                            dateEndVotePlan = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(dateEndVotePlan, timez);
                            TripReponseDTO tripReponseDTO = new TripReponseDTO();
                            tripReponseDTO.setId(iTrip);
                            tripReponseDTO.setVoteEndAt(dateEndVotePlan);
                            mChangeVoteDeadlinePresenter.changeVotePlan(tripReponseDTO);
                            dialog.dismiss();
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    public boolean checkNull(String dayVote, String timeVote) {
        if (dayVote.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(getContext(), "Pick Day");
            return false;
        } else if (timeVote.isEmpty()) {
            DialogShowErrorMessage.showValidationDialog(getContext(), "Pick Time ");
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void printPlanSuccess(List<PlanDTO> planDTOList) {

        if (planDTOList != null) {
            this.plansDTOList = new ArrayList<>();
            this.plansDTOList = planDTOList;

            List<PlanDTO> planElectedFilter = new ArrayList<>();
            for (PlanDTO planDTO : plansDTOList) {
                if (planDTO.getIdStatus().equals(PlanStatus.ELECTED)) {
                    planElectedFilter.add(planDTO);
                }
            }
            plansDTOList = planElectedFilter;
            updateUI();
            if(plansDTOList.size() == 0){
                planElectedValueEventListener = listenerPlanElected.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Long date = snapshot.getValue(Long.class);
                            startActivity(getActivity().getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }


    @Override
    public void printPlanFail(String messageFail) {

    }


    @Override
    public void changeVotePlanSuccess(String messageSS) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        detachListener();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerPlanElected.setValue(change);
            listenerPlan.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(getActivity().getIntent());
        getActivity().finish();

    }

    @Override
    public void changeVotePlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }
}
