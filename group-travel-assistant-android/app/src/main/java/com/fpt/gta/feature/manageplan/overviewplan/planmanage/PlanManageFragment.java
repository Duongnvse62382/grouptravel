package com.fpt.gta.feature.manageplan.overviewplan.planmanage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.ActivityDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.PlanDTO;
import com.fpt.gta.data.dto.PlanStatus;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.manageplan.overviewplan.MemberDialogAdapter;
import com.fpt.gta.feature.manageplan.plandetails.PlanDetailsActivity;
import com.fpt.gta.feature.manageplan.plansuggested.PlanSuggestedActivity;
import com.fpt.gta.presenter.ChangeVoteDeadlinePresenter;
import com.fpt.gta.presenter.CreatePlanIntTripPresenter;
import com.fpt.gta.presenter.CreateVotePlanPresenter;
import com.fpt.gta.presenter.DeletePlanInTripPresenter;
import com.fpt.gta.presenter.DeleteVotePlanPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.presenter.PrintPlanInTripPresenter;
import com.fpt.gta.presenter.PrintTripDetailPresenter;
import com.fpt.gta.presenter.SuggestedPlanPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.ChangeVoteDeadlineView;
import com.fpt.gta.view.CreatePlanInTripView;
import com.fpt.gta.view.CreateVotePlanView;
import com.fpt.gta.view.DeletePlanInTripView;
import com.fpt.gta.view.DeleteVotePlanView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.fpt.gta.view.PrintPlanInTripView;
import com.fpt.gta.view.PrintTripDetailView;
import com.fpt.gta.view.SuggestedPlanView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;


public class PlanManageFragment extends Fragment implements View.OnClickListener, PrintPlanInTripView, CreatePlanInTripView,
        DeletePlanInTripView, CreateVotePlanView, DeleteVotePlanView, PrintMemberInGroupView, SuggestedPlanView, ChangeVoteDeadlineView, PrintTripDetailView {
    private ViewPager viewPager;
    private PlanApdater planApdater;
    private ImageView imgShowDialogMember, imgChooseTimeEndVote;
    private View mView;
    private ImageView imgAddPlan;
    private List<MemberDTO> personDTOList;
    private List<PlanDTO> plansDTOList;
    private List<ActivityDTO> activityDTOList = new ArrayList<>();
    private PrintPlanInTripPresenter mPrintPlanInTripPresenter;
    private CreatePlanIntTripPresenter mCreatePlanIntTripPresenter;
    private DeletePlanInTripPresenter mDeletePlanInTripPresenter;
    private CreateVotePlanPresenter mCreateVotePlanPresenter;
    private DeleteVotePlanPresenter mDeleteVotePlanPresenter;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private ChangeVoteDeadlinePresenter mChangeVoteDeadlinePresenter;
    private SuggestedPlanPresenter mSuggestedPlanPresenter;
    private PrintTripDetailPresenter mPrintTripDetailPresenter;
    private String dayEndvote;
    private Calendar calendarVote, calendarTimeVote;
    private SimpleDateFormat simpleDateFormat, simpleTimeFormat;
    private int mYear, mMonth, mDay;
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private Date dateTimeEndVoteTrip;
    private Integer isAdmin;
    private String dateVote;
    private String timeVote;
    private Integer iTrip, idGroup;
    private List<PlanDTO> originalPlanDTOList = new ArrayList<>();
    private TripReponseDTO tripReponseDTODetail;
    private FirebaseDatabase databasePlan;
    private DatabaseReference listenerPlan;
    private DatabaseReference listenerPlanElected;
    private DatabaseReference listenerTripDetail;

    private ValueEventListener planValueEventListener = null;
    private ValueEventListener tripDeatilValueEventListener = null;
    private ValueEventListener planElectedValueEventListener = null;
    private TextView txtTimeDown;
    private CountDownTimer timer;


    public PlanManageFragment() {

    }

    public static PlanManageFragment newInstance() {
        PlanManageFragment fragment = new PlanManageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plan_overview, container, false);
        return mView;
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
        try {
            if (planValueEventListener != null) {
                listenerPlan.removeEventListener(planValueEventListener);
            }
            if (planElectedValueEventListener != null) {
                listenerPlanElected.removeEventListener(planElectedValueEventListener);
            }
            if (tripDeatilValueEventListener != null) {
                listenerTripDetail.removeEventListener(tripDeatilValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(timer != null){
            timer.cancel();
        }

    }

    public void initView() {
        imgAddPlan = mView.findViewById(R.id.imgAddPlan);
        viewPager = mView.findViewById(R.id.viewPager);
        imgShowDialogMember = mView.findViewById(R.id.imgShowDialogMember);
        imgChooseTimeEndVote = mView.findViewById(R.id.imgChooseTimeEndVote);
        txtTimeDown = mView.findViewById(R.id.txtTimeDown);
        iTrip = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDTRIP);
        idGroup = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        databasePlan = FirebaseDatabase.getInstance();
        listenerPlan = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlan");
        listenerPlanElected = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadPlanElected");
        listenerTripDetail = databasePlan.getReference(String.valueOf(idGroup)).child("listener").child("reloadTripDetail");

        mCreatePlanIntTripPresenter = new CreatePlanIntTripPresenter(getContext(), PlanManageFragment.this);
        mDeletePlanInTripPresenter = new DeletePlanInTripPresenter(getContext(), PlanManageFragment.this);
        mDeleteVotePlanPresenter = new DeleteVotePlanPresenter(getContext(), PlanManageFragment.this);
        mCreateVotePlanPresenter = new CreateVotePlanPresenter(getContext(), PlanManageFragment.this);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(getContext(), PlanManageFragment.this);
        mSuggestedPlanPresenter = new SuggestedPlanPresenter(getContext(), PlanManageFragment.this);
        mChangeVoteDeadlinePresenter = new ChangeVoteDeadlinePresenter(getContext(), PlanManageFragment.this);
        mPrintTripDetailPresenter = new PrintTripDetailPresenter(getContext(), PlanManageFragment.this);
        mPrintPlanInTripPresenter = new PrintPlanInTripPresenter(getContext(), PlanManageFragment.this);
        mPrintTripDetailPresenter.getTripDetail(iTrip);
    }

    public void initData() {
        imgAddPlan.setOnClickListener(this::onClick);
        imgShowDialogMember.setOnClickListener(this::onClick);
        imgChooseTimeEndVote.setOnClickListener(this::onClick);
        isAdmin = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.ISADMIN);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");
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

        tripDeatilValueEventListener = listenerTripDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintTripDetailPresenter.getTripDetail(iTrip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
        mPrintMemberInGroupPresenter.printMemberInGroup(idGroup);

    }


    private void counter(TripReponseDTO tripReponseDTO) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        Date currentDate = new Date();
        try {
            if (tripReponseDTO.getVoteEndAt() != null) {
                dateTimeEndVoteTrip = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(tripReponseDTO.getVoteEndAt(), timez);
                long diff = dateTimeEndVoteTrip.getTime() - currentDate.getTime();
                if (currentDate.getTime() < dateTimeEndVoteTrip.getTime()) {
                    timer = new CountDownTimer(diff, 1000) {
                        public void onTick(long millisUntilFinished) {
                            int days = (int) (millisUntilFinished / (24 * 60 * 60 * 1000));
                            int seconds = (int) (millisUntilFinished / 1000) % 60;
                            int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                            int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                            if (days < 1) {
                                txtTimeDown.setText(hours + ":" + minutes + ":" + seconds);
                                if (hours == 0 && minutes == 0) {
                                    txtTimeDown.setTextColor(Color.parseColor("#FF0000"));

                                }
                            } else {
                                txtTimeDown.setText(days + "Day");
                            }
                        }

                        public void onFinish() {
                            txtTimeDown.setTextColor(Color.parseColor("#56a8a2"));
                            txtTimeDown.setText("end");
                            mPrintTripDetailPresenter.getTripDetail(iTrip);
                            mPrintPlanInTripPresenter.printPlanInTrip(iTrip);
                        }
                    };
                    timer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateUI() {
        if (planApdater == null) {
            List<PlanDTO> planElectedFilter = new ArrayList<>();
            for (PlanDTO planDTO : plansDTOList) {
                if (planDTO.getIdStatus().equals(PlanStatus.PUBLIC)) {
                    planElectedFilter.add(planDTO);
                }
            }
            plansDTOList = planElectedFilter;
            planApdater = new PlanApdater(getContext(), plansDTOList);
            viewPager.setAdapter(planApdater);
            viewPager.setPadding(90, 0, 120, 0);
            planApdater.setOnPlanClickListener(new PlanApdater.OnPlanClickListener() {
                @Override
                public void onPlanClickListener(PlanDTO planDTO, int position) {

                }
            });
            planApdater.setOnDeletePlanClickListener(new PlanApdater.OnDeletePlanClickListener() {
                @Override
                public void onDeleteGroupClickListener(PlanDTO planDTO, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure to delete this Plan?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDeletePlanInTripPresenter.deletePlanInTrip(planDTO.getId());
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
            });

            TimeZone tz = TimeZone.getDefault();
            String timez = tz.getID();
            Date currentDate = new Date();
            try {
                if (tripReponseDTODetail.getVoteEndAt() != null) {
                    dateTimeEndVoteTrip = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(tripReponseDTODetail.getVoteEndAt(), timez);
                    if (currentDate.getTime() < dateTimeEndVoteTrip.getTime()) {
                        planApdater.setOnVotePlanClickListener(new PlanApdater.OnVotePlanClickListener() {
                            @Override
                            public void onVotePlanClickListener(PlanDTO planDTO, int position) {
                                mCreateVotePlanPresenter.createVotePlan(planDTO.getId());
                            }
                        });

                        planApdater.setOnUnVotePlanClickListener(new PlanApdater.OnUnVotePlanClickListener() {
                            @Override
                            public void onUnVotePlanClickListener(PlanDTO planDTO, int position) {
                                mDeleteVotePlanPresenter.deleteVotePlan(planDTO.getId());
                            }
                        });
                    }else {
                        planApdater.setOnVotePlanClickListener(null);
                        planApdater.setOnUnVotePlanClickListener(null);
                    }
                }

                if (tripReponseDTODetail.getVoteEndAt() == null) {
                    planApdater.setOnVotePlanClickListener(new PlanApdater.OnVotePlanClickListener() {
                        @Override
                        public void onVotePlanClickListener(PlanDTO planDTO, int position) {
                            mCreateVotePlanPresenter.createVotePlan(planDTO.getId());
                        }
                    });

                    planApdater.setOnUnVotePlanClickListener(new PlanApdater.OnUnVotePlanClickListener() {
                        @Override
                        public void onUnVotePlanClickListener(PlanDTO planDTO, int position) {
                            mDeleteVotePlanPresenter.deleteVotePlan(planDTO.getId());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {


            TimeZone tz = TimeZone.getDefault();
            String timez = tz.getID();
            Date currentDate = new Date();
            try {
                if (tripReponseDTODetail.getVoteEndAt() != null) {
                    dateTimeEndVoteTrip = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(tripReponseDTODetail.getVoteEndAt(), timez);
                    if (currentDate.getTime() < dateTimeEndVoteTrip.getTime()) {
                        planApdater.setOnVotePlanClickListener(new PlanApdater.OnVotePlanClickListener() {
                            @Override
                            public void onVotePlanClickListener(PlanDTO planDTO, int position) {
                                mCreateVotePlanPresenter.createVotePlan(planDTO.getId());
                            }
                        });

                        planApdater.setOnUnVotePlanClickListener(new PlanApdater.OnUnVotePlanClickListener() {
                            @Override
                            public void onUnVotePlanClickListener(PlanDTO planDTO, int position) {
                                mDeleteVotePlanPresenter.deleteVotePlan(planDTO.getId());
                            }
                        });
                    }else {
                        planApdater.setOnVotePlanClickListener(null);
                        planApdater.setOnUnVotePlanClickListener(null);
                    }
                }

                if (tripReponseDTODetail.getVoteEndAt() == null) {
                    planApdater.setOnVotePlanClickListener(new PlanApdater.OnVotePlanClickListener() {
                        @Override
                        public void onVotePlanClickListener(PlanDTO planDTO, int position) {
                            mCreateVotePlanPresenter.createVotePlan(planDTO.getId());
                        }
                    });

                    planApdater.setOnUnVotePlanClickListener(new PlanApdater.OnUnVotePlanClickListener() {
                        @Override
                        public void onUnVotePlanClickListener(PlanDTO planDTO, int position) {
                            mDeleteVotePlanPresenter.deleteVotePlan(planDTO.getId());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            planApdater.notifyChangeData(plansDTOList);
        }

    }


    private void showMemberPlanDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_dialog_member);
        LinearLayout lnlChooseAllPlan = dialog.findViewById(R.id.lnlChooseAllPlan);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rcvMemberDialog = dialog.findViewById(R.id.rcvDialogMember);
        rcvMemberDialog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        MemberDialogAdapter memberDialogAdapter = new MemberDialogAdapter(personDTOList, getContext());
        rcvMemberDialog.setAdapter(memberDialogAdapter);
        lnlChooseAllPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrintPlanInTripPresenter = new PrintPlanInTripPresenter(getContext(), PlanManageFragment.this);
                mPrintPlanInTripPresenter.printPlanInTrip(iTrip);
                dialog.dismiss();
            }
        });

        memberDialogAdapter.setOnEditItemClickListener(new MemberDialogAdapter.OnItemEditClickListener() {
            @Override
            public void onItemEditClickListener(MemberDTO personDTO, int position) {
                String memberInGroup = personDTO.getPerson().getFirebaseUid();
                List<PlanDTO> filterPlanDTOList = new ArrayList<>();
                for (PlanDTO planDTO : originalPlanDTOList) {
                    String ownerPlan = planDTO.getOwner().getPerson().getFirebaseUid();
                    if (memberInGroup.equals(ownerPlan)) {
                        filterPlanDTOList.add(planDTO);
                    }
                }
                plansDTOList = filterPlanDTOList;
                updateUI();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showTowOptionCreatePlan() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_create_plan_suggested);
        Button btnCreatePlanSuggested = dialog.findViewById(R.id.btnCreatePlanSuggested);
        Button btnPlanBlank = dialog.findViewById(R.id.btnPlanBlank);

        btnCreatePlanSuggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuggestedPlanPresenter.suggestedPlan(iTrip);
                dialog.dismiss();
            }
        });


        btnPlanBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreatePlanIntTripPresenter.createPlanIntrip(iTrip, activityDTOList);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgAddPlan:
                showTowOptionCreatePlan();
                break;
            case R.id.imgShowDialogMember:
                showMemberPlanDialog();
                break;
            case R.id.imgChooseTimeEndVote:
                showSetVotePlanDialog();
                break;
        }
    }

    private void showSetVotePlanDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_dialog_votetimeend);
        EditText edtChangeVoteDay = dialog.findViewById(R.id.edtChangeVoteDay);
        EditText edtChangeVoteTime = dialog.findViewById(R.id.edtChangeVoteTime);
        Button btnSetVoteTime = dialog.findViewById(R.id.btnChangeVoteTime);
        btnSetVoteTime.setVisibility(View.GONE);
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();

        try {
            if (tripReponseDTODetail.getVoteEndAt() != null) {
                dateTimeEndVoteTrip = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(tripReponseDTODetail.getVoteEndAt(), timez);
                String dateEndVoteTrip = ZonedDateTimeUtil.convertDateToStringASIA(dateTimeEndVoteTrip);
                String timeEndVoteTrip = ZonedDateTimeUtil.convertDateToStringTime(dateTimeEndVoteTrip);
                edtChangeVoteDay.setText(dateEndVoteTrip);
                edtChangeVoteTime.setText(timeEndVoteTrip);
            } else {
                edtChangeVoteDay.setText("");
                edtChangeVoteTime.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        edtChangeVoteDay.setFocusable(false);
        edtChangeVoteTime.setFocusable(false);

        if (isAdmin.equals(MemberRole.ADMIN)) {
            btnSetVoteTime.setVisibility(View.VISIBLE);
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
        }


        btnSetVoteTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeVotes = edtChangeVoteTime.getText().toString();
                String dateVotes = edtChangeVoteDay.getText().toString();
                try {
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
            plansDTOList = new ArrayList<>();
            plansDTOList = planDTOList;
            updateUI();
            originalPlanDTOList = planDTOList;

            for (PlanDTO planDTO : planDTOList) {
                if (planDTO.getIdStatus().equals(4)) {
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
                    break;
                }
            }
        }
    }


    @Override
    public void printPlanFail(String messageFail) {

    }

    @Override
    public void deletePlanSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerPlan.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void createVotePlanSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerPlan.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "Like", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void createVotePlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }


    @Override
    public void deleteVotePlanSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerPlan.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "Unlike", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteVotePlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void getIdPlan(PlanDTO planDTO) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerPlan.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharePreferenceUtils.saveIntSharedPreference(this.getActivity(), GTABundle.PLANID, planDTO.getId());
        Intent intent = new Intent(this.getActivity(), PlanDetailsActivity.class);
        Gson gson = new Gson();
        String planDTOS = gson.toJson(planDTO);
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.PLANOJECT, planDTOS);
        startActivity(intent);
    }


    @Override
    public void createPlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        personDTOList = new ArrayList<>();
        personDTOList = memberDTOList;
    }

    @Override
    public void PrintMemberFail(String message) {

    }

    @Override
    public void suggestedPlanSuccess(List<ActivityDTO> activityDTOList) {
        Intent intent = new Intent(this.getActivity(), PlanSuggestedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ACTIVITYOJECT, (Serializable) activityDTOList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void suggestedPlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void changeVotePlanSuccess(String messageSS) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerTripDetail.setValue(change);
            listenerPlan.setValue(change);
            if(timer != null){
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void changeVotePlanFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void getTripDetailSuccess(TripReponseDTO tripReponseDTO) {
        if (tripReponseDTO != null) {
            tripReponseDTODetail = tripReponseDTO;
            counter(tripReponseDTO);
        }
    }

    @Override
    public void getTripDetailFail(String message) {
    }
}