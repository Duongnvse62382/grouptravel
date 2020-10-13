package com.fpt.gta.feature.managetrip.manageconfirm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.managebudget.ConfirmBudgetGroupActivity;
import com.fpt.gta.feature.managetransaction.edittransaction.EditTransactionActivity;
import com.fpt.gta.presenter.DeleteTransactionPresenter;
import com.fpt.gta.presenter.MakePlanningPresenter;
import com.fpt.gta.presenter.MakeReadyPresenter;
import com.fpt.gta.presenter.PrintGroupByIdPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.InternetHelper;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.MakePlanningView;
import com.fpt.gta.view.MakeReadyView;
import com.fpt.gta.view.PrintGroupByIdView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmElectedFragment extends Fragment implements PrintMemberInGroupView, MakeReadyView, PrintGroupByIdView, MakePlanningView {
    private View mView;
    private int groupId;
    private RecyclerView rcvConfirmMember;
    private ConfirmMemberElectedAdapter mConfirmMemberElectedAdapter;
    private List<MemberDTO> mMemberDTOList;
    private List<MemberDTO> memberActiveList;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private MakeReadyPresenter makeReadyPresenter;
    private DatabaseReference reloadGroupStatus;
    private DatabaseReference reloadReady;
    private FirebaseDatabase mDatabase;
    private ValueEventListener confirmElectedValueEventListener = null;
    private TextInputEditText edtViewBudgetJourneyActivity, edtViewBudgetJourneyAccommodation, edtViewBudgetJourney, edtViewBudgetJourneyTransportation, edtViewBudgetJourneyFood, edtViewBudgetJourneyTotal;
    private TextView txtViewCurrencyJourney1, txtViewCurrencyJourney2, txtViewCurrencyJourney3, txtViewCurrencyJourney4, txtViewCurrencyJourney5;
    private PrintGroupByIdPresenter mPrintGroupByIdPresenter;
    private GroupResponseDTO mGroupResponseDTO;
    private BigDecimal activityGroup = BigDecimal.ZERO;
    private BigDecimal accommodationGroup = BigDecimal.ZERO;
    private BigDecimal transportationGroup = BigDecimal.ZERO;
    private BigDecimal foodGroup = BigDecimal.ZERO;
    private BigDecimal totalGroup = BigDecimal.ZERO;
    private CircleImageView imgMakePlaning;
    private MakePlanningPresenter mMakePlanningPresenter;

    public ConfirmElectedFragment() {

    }

    public static ConfirmElectedFragment newInstance() {
        ConfirmElectedFragment fragment = new ConfirmElectedFragment();
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
        mView = inflater.inflate(R.layout.fragment_confirm_elected, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadConfirmMember();
        loadGroup();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (confirmElectedValueEventListener != null) {
                reloadReady.removeEventListener(confirmElectedValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initData() {

        activityGroup = BigDecimal.ZERO;
        accommodationGroup = BigDecimal.ZERO;
        transportationGroup = BigDecimal.ZERO;
        foodGroup = BigDecimal.ZERO;
        totalGroup = BigDecimal.ZERO;

        activityGroup = mGroupResponseDTO.getActivityBudget();
        accommodationGroup = mGroupResponseDTO.getAccommodationBudget();
        transportationGroup = mGroupResponseDTO.getTransportationBudget();
        foodGroup = mGroupResponseDTO.getFoodBudget();
        totalGroup = totalGroup.add(activityGroup).add(accommodationGroup).add(transportationGroup).add(foodGroup);

        edtViewBudgetJourneyActivity.setText(ChangeValue.formatBigCurrency(activityGroup));
        edtViewBudgetJourneyAccommodation.setText(ChangeValue.formatBigCurrency(accommodationGroup));
        edtViewBudgetJourneyTransportation.setText(ChangeValue.formatBigCurrency(transportationGroup));
        edtViewBudgetJourneyFood.setText(ChangeValue.formatBigCurrency(foodGroup));
        edtViewBudgetJourneyTotal.setText(ChangeValue.formatBigCurrency(totalGroup));

        txtViewCurrencyJourney1.setText(mGroupResponseDTO.getCurrency().getCode());
        txtViewCurrencyJourney2.setText(mGroupResponseDTO.getCurrency().getCode());
        txtViewCurrencyJourney3.setText(mGroupResponseDTO.getCurrency().getCode());
        txtViewCurrencyJourney4.setText(mGroupResponseDTO.getCurrency().getCode());
        txtViewCurrencyJourney5.setText(mGroupResponseDTO.getCurrency().getCode());
        imgMakePlaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure to continue make planning journey?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (InternetHelper.isOnline(getContext()) == false) {
                            DialogShowErrorMessage.showDialogNoInternet(getContext(), "No Connection");
                        } else {
                            mMakePlanningPresenter.makePlanning(groupId);
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    public void loadConfirmMember() {
        confirmElectedValueEventListener = reloadReady.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void initView() {
        mDatabase = FirebaseDatabase.getInstance();
        groupId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        reloadReady = mDatabase.getReference(String.valueOf(groupId)).child("listener").child("reloadReady");
        rcvConfirmMember = mView.findViewById(R.id.rcvConfirmMember);

        txtViewCurrencyJourney1 = mView.findViewById(R.id.txtViewCurrencyJourney1);
        txtViewCurrencyJourney2 = mView.findViewById(R.id.txtViewCurrencyJourney2);
        txtViewCurrencyJourney3 = mView.findViewById(R.id.txtViewCurrencyJourney3);
        txtViewCurrencyJourney4 = mView.findViewById(R.id.txtViewCurrencyJourney4);
        txtViewCurrencyJourney5 = mView.findViewById(R.id.txtViewCurrencyJourney5);

        imgMakePlaning = mView.findViewById(R.id.imgMakePlaning);
        imgMakePlaning.setVisibility(View.GONE);

        edtViewBudgetJourneyActivity = mView.findViewById(R.id.edtViewBudgetJourneyActivity);
        edtViewBudgetJourneyAccommodation = mView.findViewById(R.id.edtViewBudgetJourneyAccommodation);
        edtViewBudgetJourneyTransportation = mView.findViewById(R.id.edtViewBudgetJourneyTransportation);
        edtViewBudgetJourneyFood = mView.findViewById(R.id.edtViewBudgetJourneyFood);
        edtViewBudgetJourneyTotal = mView.findViewById(R.id.edtViewBudgetJourneyTotal);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvConfirmMember.setLayoutManager(linearLayoutManager);
        makeReadyPresenter = new MakeReadyPresenter(getContext(), this);
        mMakePlanningPresenter = new MakePlanningPresenter(getContext(), this);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(getContext(), this);
        mPrintGroupByIdPresenter = new PrintGroupByIdPresenter(getContext(), this);

        disableEditText(edtViewBudgetJourneyActivity);
        disableEditText(edtViewBudgetJourneyAccommodation);
        disableEditText(edtViewBudgetJourneyTransportation);
        disableEditText(edtViewBudgetJourneyFood);
        disableEditText(edtViewBudgetJourneyTotal);

        loadGroup();
    }

    public void loadGroup() {
        mPrintGroupByIdPresenter.getGroupById(groupId);
    }

    public void loadMember() {
        mPrintMemberInGroupPresenter.printMemberInGroup(groupId);
    }

    public void makeMeReady(Integer idGroup, boolean isReady) {
        makeReadyPresenter.makeMeReady(idGroup, isReady);
    }

    public void updateUI() {
        if (mConfirmMemberElectedAdapter == null) {
            mConfirmMemberElectedAdapter = new ConfirmMemberElectedAdapter(memberActiveList, getActivity());
            rcvConfirmMember.setAdapter(mConfirmMemberElectedAdapter);
            mConfirmMemberElectedAdapter.setmOnClickConfirmElected(new ConfirmMemberElectedAdapter.OnClickConfirmElected() {
                @Override
                public void onClickConfirmElected(MemberDTO memberDTO, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you unready to start journey?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (InternetHelper.isOnline(getContext()) == false) {
                                DialogShowErrorMessage.showDialogNoInternet(getContext(), "No Connection");
                            } else {
                                boolean isUnReady = false;
                                makeMeReady(groupId, isUnReady);
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            });
            mConfirmMemberElectedAdapter.setmOnClickUnConfirmElected(new ConfirmMemberElectedAdapter.OnClickUnConfirmElected() {
                @Override
                public void onClickUnConfirmElected(MemberDTO memberDTO, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you ready to start journey?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (InternetHelper.isOnline(getContext()) == false) {
                                DialogShowErrorMessage.showDialogNoInternet(getContext(), "No Connection");
                            } else {
                                boolean isReady = true;
                                makeMeReady(groupId, isReady);
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            });
        } else {
            mConfirmMemberElectedAdapter.notifyChangeData(memberActiveList);
        }
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {

        if (memberDTOList != null) {
            memberActiveList = new ArrayList<>();
            for (MemberDTO memberDTO : memberDTOList) {
                if (memberDTO.getIdStatus().compareTo(1) == 0) {
                    memberActiveList.add(memberDTO);
                }
            }
            boolean checkAdmin = checkValidAdmin();
            if (checkAdmin == true) {
                imgMakePlaning.setVisibility(View.VISIBLE);
            } else {
                imgMakePlaning.setVisibility(View.GONE);
            }
        }
        initData();
        updateUI();

    }

    public boolean checkValidAdmin() {
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (MemberDTO memberDTO : memberActiveList) {
            try {
                int roleMember = memberDTO.getIdRole();
                String member = memberDTO.getPerson().getFirebaseUid();
                if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                    return true;
                }
            } catch (Exception e) {
                Log.d("TripManageFragment", "PrintMemberSuccess: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void PrintMemberFail(String message) {

    }

    @Override
    public void makeReadySuccess(String messageSuccess) {
        Toast.makeText(getContext(), "Ready Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void makeReadyFail(String messageFail) {
        Toast.makeText(getContext(), "Ready Fail", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void printGroupByIdSS(GroupResponseDTO groupResponseDTO) {
        if (groupResponseDTO != null) {
            mGroupResponseDTO = groupResponseDTO;
        }
        loadMember();
    }

    @Override
    public void printGroupByIdFail(String messageFail) {

    }

    @Override
    public void makePlanningSuccess(String messageSuccess) {
        Toast.makeText(getContext(), "Planning Continue", Toast.LENGTH_SHORT).show();
        startActivity(getActivity().getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        getActivity().finish();
    }

    @Override
    public void makePlanningFail(String messageFail) {
        Toast.makeText(getContext(), "Planning Fail", Toast.LENGTH_SHORT).show();

    }
}