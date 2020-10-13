package com.fpt.gta.feature.managetrip.overviewtrip;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fpt.gta.R;;
import com.fpt.gta.data.dto.LatLongDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.feature.managegroup.documentgroup.DocumentGroupManageActivity;
import com.fpt.gta.feature.managetask.TaskOverviewActivity;
import com.fpt.gta.feature.managetrip.addtrip.CreateTripActivity;
import com.fpt.gta.feature.managetrip.edittrip.EditTripActivity;
import com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity.SuggestAndVoteActivity;
import com.fpt.gta.feature.managetrip.map.MapActivity;
import com.fpt.gta.presenter.PrinaAllTripPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.fpt.gta.view.TripOverviewView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class TripManageFragment extends Fragment implements View.OnClickListener, TripOverviewView, PrintMemberInGroupView, TripSection.ClickListener {


    private View mView;
    private ImageView imgAddTrip, imgGoogleMap, imgDocument, imgTaskOverView;
    private RecyclerView recyclerViewTrip;
    private List<TripReponseDTO> tripDTOList;
    private int groupId;
    private List<MemberDTO> memberList;
    private int isAdmin;
    private String isIdAdmin;
    private GoogleMap gMap;
    private List<LatLongDTO> latLngList = new ArrayList<>();
    private Polyline polyline = null;
    private PrinaAllTripPresenter prinaAllTripPresenter;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private List<TripReponseDTO> tripGoingToList;
    private List<TripReponseDTO> tripGoingList;
    private List<TripReponseDTO> tripGoneList;

    private FirebaseDatabase databaseTrip;
    private DatabaseReference listenerTrip;
    private ValueEventListener tripValueEventListener;
    private String timeZoneJourney;
    Integer groupStatus;

    public TripManageFragment() {
    }

    public static TripManageFragment newInstance() {
        TripManageFragment fragment = new TripManageFragment();
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
        mView = inflater.inflate(R.layout.fragment_trip, container, false);
        return mView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initalView();
        initalData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initalView();
        initalData();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadFagment();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (tripValueEventListener != null) {
                listenerTrip.removeEventListener(tripValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadFagment() {
        tripValueEventListener = listenerTrip.addValueEventListener(new ValueEventListener() {
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

    public void initalData() {
        timeZoneJourney = SharePreferenceUtils.getStringSharedPreference(getContext(), GTABundle.TIMEZONEGROUP);
        groupStatus = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.GROUPSTATUS);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(getContext(), this);
        imgAddTrip.setOnClickListener(this::onClick);
        imgDocument.setOnClickListener(this::onClick);
        imgTaskOverView.setOnClickListener(this::onClick);
    }


    public void initalView() {
        imgTaskOverView = mView.findViewById(R.id.imgTaskOverView);
        imgDocument = mView.findViewById(R.id.imgDocumentOverView);
        imgAddTrip = mView.findViewById(R.id.imgAddTrip);
        imgAddTrip.setVisibility(View.GONE);
        imgGoogleMap = mView.findViewById(R.id.imgOverViewMap);
        imgGoogleMap.setOnClickListener(this::onClick);
        imgGoogleMap.setVisibility(View.GONE);
        recyclerViewTrip = mView.findViewById(R.id.recyclerViewTrip);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewTrip.setLayoutManager(linearLayoutManager);
        imgTaskOverView.setVisibility(View.GONE);
        databaseTrip = FirebaseDatabase.getInstance();
        groupId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        listenerTrip = databaseTrip.getReference(String.valueOf(groupId)).child("listener").child("reloadTrip");
    }

    public void goneWithNotAdmin() {
        prinaAllTripPresenter = new PrinaAllTripPresenter(getContext(), this);
        prinaAllTripPresenter.getAllTripByGroupId(groupId);
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int i;

        if (!groupStatus.equals(GroupStatus.PLANNING)) {
            imgAddTrip.setVisibility(View.GONE);
            if(groupStatus.equals(GroupStatus.EXECUTING)){
                imgTaskOverView.setVisibility(View.VISIBLE);
            }
            for (i = 0; i <= memberList.size() - 1; i++) {
                try {
                    int roleMember = memberList.get(i).getIdRole();
                    String member = memberList.get(i).getPerson().getFirebaseUid();
                    if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                        isAdmin = roleMember;
                        isIdAdmin = member;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            for (i = 0; i <= memberList.size(); i++) {
                try {
                    int roleMember = memberList.get(i).getIdRole();
                    String member = memberList.get(i).getPerson().getFirebaseUid();
                    if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                        imgAddTrip.setVisibility(View.VISIBLE);
                        isAdmin = roleMember;
                        isIdAdmin = member;
                        break;
                    }
                } catch (Exception e) {
                    Log.d("TripManageFragment", "PrintMemberSuccess: " + e.getMessage());
                }
            }
        }


    }

    public void SendIsAdmin(int admin) {
        SharePreferenceUtils.saveIntSharedPreference(getContext(), GTABundle.ISADMIN, admin);
    }

    public void SendIsAdminUserId(String idAdmin) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.ISIDADMIN, idAdmin);
    }

    private void updateUI() {
        recyclerViewTrip.setAdapter(sectionedAdapter);
        sectionedAdapter.notifyDataSetChanged();
    }

    public void SendDateGo(String dateGo) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATETRIPGO, dateGo);
    }

    public void SendDateEnd(String dateEnd) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATETRIPEND, dateEnd);
    }


    public void SendDateGoUTC(String dateGoUTC) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATETRIPGOUTC, dateGoUTC);
    }

    public void SendDateEndUTC(String dateEndUTC) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATETRIPENDUTC, dateEndUTC);
    }

    public void sendTripTimeZone(String timeZone) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.TIMEZONETRIP, timeZone);
    }


    public void onClickEdit(TripReponseDTO tripReponseDTO) {
        Intent intent = new Intent(this.getActivity(), EditTripActivity.class);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable(GTABundle.KEYTRIP, tripReponseDTO);
        bundle1.putSerializable(GTABundle.KEYTRIPLIST, (Serializable) tripDTOList);
        intent.putExtras(bundle1);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgAddTrip:
                onClickImgAddTrip(groupId);
                break;
            case R.id.imgOverViewMap:
                Intent intent = new Intent(getActivity(), MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listLastLong", (Serializable) latLngList);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.imgDocumentOverView:
                onClickImgDocument();
                break;
            case R.id.imgTaskOverView:
                Intent intentToTaskOverView = new Intent(getContext(), TaskOverviewActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable(GTABundle.KEYTRIPLIST, (Serializable) tripDTOList);
                intentToTaskOverView.putExtras(bundle1);
                startActivity(intentToTaskOverView);
        }
    }

    public void onClickImgAddTrip(int groupId) {
        Intent intent = new Intent(getContext(), CreateTripActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("groupID", groupId);
        bundle.putSerializable(GTABundle.KEYTRIPLIST, (Serializable) tripDTOList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onClickImgDocument() {
        Intent intent = new Intent(getContext(), DocumentGroupManageActivity.class);
        startActivity(intent);
    }


    public void onTripClick(TripReponseDTO tripReponseDTO) {
        Intent intent1 = new Intent(this.getActivity(), SuggestAndVoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYTRIP_onClickItem, tripReponseDTO);
        bundle.putSerializable(GTABundle.KEYOWNER, (Serializable) memberList);
        intent1.putExtras(bundle);
        startActivity(intent1);
    }

    public void SendData(int id) {
        SharePreferenceUtils.saveIntSharedPreference(getContext(), GTABundle.IDTRIP, id);
    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        if (memberDTOList != null) {
            memberList = new ArrayList<>();
            memberList = memberDTOList;
            goneWithNotAdmin();
        }
    }

    @Override
    public void PrintMemberFail(String message) {

    }


    @Override
    public void onSucessFul(List<TripReponseDTO> tripReponseDTOList) {
        if (tripReponseDTOList != null) {
            this.tripDTOList = new ArrayList<>();
            tripDTOList = tripReponseDTOList;
            Collections.sort(tripDTOList, new Comparator<TripReponseDTO>() {
                @Override
                public int compare(TripReponseDTO o1, TripReponseDTO o2) {
                    return o1.getStartUtcAt().compareTo(o2.getStartUtcAt());
                }
            });
            Gson gson = new Gson();
            try {
                String latlong = SharePreferenceUtils.getStringSharedPreference(getContext(), "LATLONGDTO");
                LatLongDTO groupLatLng = gson.fromJson(latlong, LatLongDTO.class);
                latLngList.clear();
                latLngList.add(groupLatLng);
                for (int i = 0; i < tripDTOList.size(); i++) {
                    LatLongDTO latLng = new LatLongDTO(tripReponseDTOList.get(i).getStartPlace().getName(), tripReponseDTOList.get(i).getStartPlace().getLatitude().doubleValue(), tripReponseDTOList.get(i).getStartPlace().getLongitude().doubleValue());
                    latLngList.add(latLng);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            sectionedAdapter = new SectionedRecyclerViewAdapter();
            tripGoingToList = new ArrayList<>();
            tripGoingList = new ArrayList<>();
            tripGoneList = new ArrayList<>();

            Date currentDate = new Date(Instant.now().toEpochMilli());
            TimeZone tz = TimeZone.getDefault();
            for (TripReponseDTO tripReponseDTO : tripDTOList) {
                Date dateStart = tripReponseDTO.getStartUtcAt();
                Date dateEnd = tripReponseDTO.getEndUtcAt();
                Date dateStartGoing = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateStart, tz.getID());
                Date dateEndGoing = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEnd, tz.getID());
                if ((currentDate.getTime() > dateStartGoing.getTime() && currentDate.getTime() < dateEndGoing.getTime())
                ) {
                    tripGoingList.add(tripReponseDTO);
                } else if (currentDate.before(dateStartGoing)) {
                    tripGoingToList.add(tripReponseDTO);
                } else if (currentDate.after(dateEndGoing)) {
                    tripGoneList.add(tripReponseDTO);
                }
            }

            if (tripGoingList.size() > 0) {
                sectionedAdapter.addSection(new TripSection(tripGoingList, getContext(), isAdmin, "On Going", R.mipmap.ongoing, this));
            }

            if (tripGoingToList.size() > 0) {
                sectionedAdapter.addSection(new TripSection(tripGoingToList, getContext(), isAdmin, "In Coming", R.mipmap.goingto, this));
            }

            if (tripGoneList.size() > 0) {
                sectionedAdapter.addSection(new TripSection(tripGoneList, getContext(), isAdmin, "Passed", R.mipmap.gone, this));
            }
            updateUI();
        }
    }

    @Override
    public void onFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void showError(String message) {

    }


    @Override
    public void onItemRootViewClicked(@NonNull TripSection section, int itemAdapterPosition, TripReponseDTO tripReponseDTO) {
        onTripClick(tripReponseDTO);
        String timeZoneStart = ZonedDateTimeUtil.convertDateToStringASIA(tripReponseDTO.getStartAt());
        String timeZoneEnd = ZonedDateTimeUtil.convertDateToStringASIA(tripReponseDTO.getEndAt());
        SendDateGo(timeZoneStart);
        SendDateEnd(timeZoneEnd);

        String timeTripStartUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(tripReponseDTO.getStartUtcAt());
        String timeTripEndUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(tripReponseDTO.getEndUtcAt());
        SendDateGoUTC(timeTripStartUTC);
        SendDateEndUTC(timeTripEndUTC);

        sendTripTimeZone(tripReponseDTO.getStartPlace().getTimeZone());
        SendIsAdmin(isAdmin);
        SendIsAdminUserId(isIdAdmin);
        SendData(tripReponseDTO.getId());
    }

    @Override
    public void onItemEditTripClicked(@NonNull TripSection section, int itemAdapterPosition, TripReponseDTO tripReponseDTO) {
        onClickEdit(tripReponseDTO);
    }

}