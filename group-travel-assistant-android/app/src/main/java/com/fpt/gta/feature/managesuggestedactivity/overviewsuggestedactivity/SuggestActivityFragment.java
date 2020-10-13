package com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.feature.managemember.MemberOverviewActivity;
import com.fpt.gta.feature.managesuggestedactivity.addsuggestedactivity.AddSuggestActivity;
import com.fpt.gta.feature.managesuggestedactivity.editsuggestedactivity.EditSuggestedActivity;
import com.fpt.gta.presenter.CreateSuggestedActivityPresenter;
import com.fpt.gta.presenter.CreateVoteSuggestedPresenter;
import com.fpt.gta.presenter.DeleteSuggestedActivityPresenter;
import com.fpt.gta.presenter.DeleteVoteSuggestedPresenter;
import com.fpt.gta.presenter.PrintSearchSuggestedPlacePresenter;
import com.fpt.gta.presenter.PrintSuggestedActivityPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateSuggestedActivityView;
import com.fpt.gta.view.CreateVoteSuggestedView;
import com.fpt.gta.view.DeleteSuggestedActivityView;
import com.fpt.gta.view.DeleteVoteSuggestedView;
import com.fpt.gta.view.PrintSearchSuggestedPlaceView;
import com.fpt.gta.view.PrintSuggestedActivityView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class SuggestActivityFragment extends Fragment implements View.OnClickListener,
        PrintSuggestedActivityView, DeleteSuggestedActivityView, CreateVoteSuggestedView, DeleteVoteSuggestedView, PrintSearchSuggestedPlaceView, CreateSuggestedActivityView, PlaceSuggestedSection.ClickListener {

    private PrintSuggestedActivityPresenter mPrintSuggestedActivityPresenter;
    private DeleteSuggestedActivityPresenter mDeleteSuggestedActivityPresenter;
    private CreateVoteSuggestedPresenter mCreateVoteSuggestedPresenter;
    private DeleteVoteSuggestedPresenter mDeleteVoteSuggestedPresenter;
    private RecyclerView recyclerSuggestedActivity;
    private SuggestAndVoteActivityAdapter suggestAndVoteActivityAdapter;
    private List<SuggestedActivityResponseDTO> suggestedActivityDTOList;
    private PrintSearchSuggestedPlacePresenter mPrintSearchSuggestedPlacePresenter;
    private CreateSuggestedActivityPresenter mCreateSuggestedActivityPresenter;
    private Integer idTrip;
    private Integer idGroup;
    private FirebaseDatabase databaseSuggested;
    private DatabaseReference listenerSuggestedRef;
    private ValueEventListener suggestedValueEventListener = null;
    private Button btnAddNewSuggested;

    private List<SuggestedActivityResponseDTO> suggestedDTOList = new ArrayList<>();
    private List<PlaceDTO> placesSuggestedDTOList;
    private Button btnAddChboxSuggested;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private List<PlaceDTO> placesSuggestedChosen;
    private List<PlaceDTO> placesSuggestedNotYet;
    private Integer groupStatus;

    private View mView;
    private ImageView imgAddSuggestActivity;


    public SuggestActivityFragment() {
    }


    public static SuggestActivityFragment newInstance() {
        SuggestActivityFragment fragment = new SuggestActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void initalView() {
        imgAddSuggestActivity = mView.findViewById(R.id.imgAddSuggestActivity);
        recyclerSuggestedActivity = mView.findViewById(R.id.recyclerSuggestActivity);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerSuggestedActivity.setLayoutManager(linearLayoutManager);
        idGroup = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        databaseSuggested = FirebaseDatabase.getInstance();
        listenerSuggestedRef = databaseSuggested.getReference(String.valueOf(idGroup)).child("listener").child("reloadSuggestedPlace");

    }

    public void initalData() {
        imgAddSuggestActivity.setOnClickListener(this::onClick);
        idTrip = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDTRIP);
        groupStatus = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.GROUPSTATUS);
        if(!groupStatus.equals(GroupStatus.PLANNING)){
            imgAddSuggestActivity.setVisibility(View.GONE);
        }else {
            imgAddSuggestActivity.setVisibility(View.VISIBLE);
        }
        mPrintSuggestedActivityPresenter = new PrintSuggestedActivityPresenter(getContext(), this);
        suggestedValueEventListener = listenerSuggestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintSuggestedActivityPresenter.getSuggestedActivity(idTrip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mPrintSearchSuggestedPlacePresenter = new PrintSearchSuggestedPlacePresenter(getContext(), this);
        mDeleteSuggestedActivityPresenter = new DeleteSuggestedActivityPresenter(getContext(), this);
        mCreateVoteSuggestedPresenter = new CreateVoteSuggestedPresenter(getContext(), this);
        mDeleteVoteSuggestedPresenter = new DeleteVoteSuggestedPresenter(getContext(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_suggest_activity, container, false);
        return mView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initalView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initalData();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (suggestedValueEventListener != null) {
                listenerSuggestedRef.removeEventListener(suggestedValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        if (suggestAndVoteActivityAdapter == null) {
            suggestAndVoteActivityAdapter = new SuggestAndVoteActivityAdapter(suggestedActivityDTOList, getContext());
            recyclerSuggestedActivity.setAdapter(suggestAndVoteActivityAdapter);
            suggestAndVoteActivityAdapter.setOnItemClickListener(new SuggestAndVoteActivityAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(SuggestedActivityResponseDTO suggestedActivityDTO, int position) {
                    onClick(suggestedActivityDTOList.get(position), position);
                }
            });

            suggestAndVoteActivityAdapter.setOnDeleteClickListenner(new SuggestAndVoteActivityAdapter.OnDeleteClickListenner() {
                @Override
                public void onDeleteClickListener(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
                    onClickDeleteSuggested(suggestedActivityDTOList.get(position), position);
                }
            });

            suggestAndVoteActivityAdapter.setOnEditClickListenner(new SuggestAndVoteActivityAdapter.OnEditClickListenner() {
                @Override
                public void onEditClickListener(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
                    onEditSuggested(suggestedActivityDTOList.get(position), position);
                }
            });
            suggestAndVoteActivityAdapter.setOnVoteSuggestedListenner(new SuggestAndVoteActivityAdapter.OnVoteSuggestedListenner() {
                @Override
                public void onVoteSugggestedListenner(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
                    clickVoteSuggested(suggestedActivityResponseDTO, position);
                }
            });

            suggestAndVoteActivityAdapter.setOnUnVoteSuggestedListenner(new SuggestAndVoteActivityAdapter.OnUnVoteSuggestedListenner() {
                @Override
                public void onUnVoteSugggestedListenner(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
                    clickUnVoteSuggested(suggestedActivityResponseDTO, position);
                }
            });
        } else {
            suggestAndVoteActivityAdapter.notifyChangeData(suggestedActivityDTOList);
        }
    }


    private void showPlaceSuggestedDialog() {
        suggestedDTOList = new ArrayList<>();
        final Dialog dialog = new Dialog(getContext());
        for (PlaceDTO placeDTO : placesSuggestedDTOList) {
            placeDTO.setSelected(false);
        }
        dialog.setContentView(R.layout.layout_dialog_place_suggested);
        btnAddNewSuggested = dialog.findViewById(R.id.btnAddNewSuggested);
        btnAddNewSuggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddSuggestActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        ImageView imgDissmiss = dialog.findViewById(R.id.imgDissmiss);
        imgDissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAddChboxSuggested = dialog.findViewById(R.id.btnSubmitCheckSuggested);
        btnAddChboxSuggested.setVisibility(View.GONE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rcvMemberDialog = dialog.findViewById(R.id.rcvDialogPlaceSuggested);
        rcvMemberDialog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        sectionedAdapter = new SectionedRecyclerViewAdapter();
        placesSuggestedChosen = new ArrayList<>();
        placesSuggestedNotYet = new ArrayList<>();
        for (PlaceDTO placeDTOs : placesSuggestedDTOList) {
            String idGooglePlaceSuggeted = placeDTOs.getGooglePlaceId();
            boolean isContaint = false;
            for (SuggestedActivityResponseDTO suggestedActivityResponseDTO : suggestedActivityDTOList) {
                String idGoogleSuggestedActivity = suggestedActivityResponseDTO.getStartPlace().getGooglePlaceId();
                if (idGooglePlaceSuggeted.equals(idGoogleSuggestedActivity)) {
                    isContaint = true;
                    break;
                }
            }
            if (isContaint) {
                placesSuggestedChosen.add(placeDTOs);
            } else {
                placesSuggestedNotYet.add(placeDTOs);
            }
        }


        if (placesSuggestedNotYet.size() > 0) {
            sectionedAdapter.addSection(new PlaceSuggestedSection(placesSuggestedNotYet, getContext(), "Place Not Yet Choose", R.mipmap.placelocationbg, this));
        }

        if (placesSuggestedChosen.size() > 0) {
            sectionedAdapter.addSection(new PlaceSuggestedSection(placesSuggestedChosen, getContext(), "Place Chosen", R.mipmap.placelocationbg, this));
        }

        rcvMemberDialog.setAdapter(sectionedAdapter);
        sectionedAdapter.notifyDataSetChanged();


        btnAddChboxSuggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreateSuggestedActivityPresenter = new CreateSuggestedActivityPresenter(getContext(), SuggestActivityFragment.this);
                prepareSuggestedActivityResponseDTOListFromForm();
                for (SuggestedActivityResponseDTO suggestedResponseDTO : suggestedDTOList) {
                    mCreateSuggestedActivityPresenter.createSuggestActivity(idTrip, suggestedResponseDTO);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void prepareSuggestedActivityResponseDTOListFromForm() {
        for (PlaceDTO placeDTO : placesSuggestedDTOList) {
            if (placeDTO.isSelected()) {
                SuggestedActivityResponseDTO suggestedActivityResponseDTO = new SuggestedActivityResponseDTO();
                String nameSuggestedActivity = placeDTO.getName();
                String googlePlaceID = placeDTO.getGooglePlaceId();
                suggestedActivityResponseDTO.setName(nameSuggestedActivity);
                suggestedActivityResponseDTO.setIdType(1);
                PlaceDTO placeDTOStart = new PlaceDTO();
                placeDTOStart.setGooglePlaceId(googlePlaceID);
                suggestedActivityResponseDTO.setStartPlace(placeDTOStart);
                PlaceDTO placeDTOEnd = new PlaceDTO();
                placeDTOEnd.setGooglePlaceId(googlePlaceID);
                suggestedActivityResponseDTO.setEndPlace(placeDTOEnd);
                suggestedDTOList.add(suggestedActivityResponseDTO);
            }
        }
    }


    public void onEditSuggested(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
        Intent intent = new Intent(this.getActivity(), EditSuggestedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYSUGGESTEDACTIVITY, (SuggestedActivityResponseDTO) suggestedActivityDTOList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onClick(SuggestedActivityResponseDTO suggestedActivityDTO, int position) {
    }

    public void onClickDeleteSuggested(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to delete this Suggested?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteSuggestedActivityPresenter.deleteSuggestedActivity(suggestedActivityResponseDTO.getId());
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

    public void clickVoteSuggested(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
        mCreateVoteSuggestedPresenter.createVote(suggestedActivityResponseDTO.getId());
    }

    public void clickUnVoteSuggested(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position) {
        mDeleteVoteSuggestedPresenter.deleteVote(suggestedActivityResponseDTO.getId());
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgAddSuggestActivity:
                mPrintSearchSuggestedPlacePresenter.getSearchSuggestedPlaceView(idTrip);
                break;
        }
    }

    @Override
    public void getSuggestedSuccess(List<SuggestedActivityResponseDTO> suggestedActivityResponseDTOList) {
        if (suggestedActivityResponseDTOList != null) {
            suggestedActivityDTOList = new ArrayList<>();
            suggestedActivityDTOList = suggestedActivityResponseDTOList;
            Comparator<SuggestedActivityResponseDTO> suggestedActivityDTOComparator = new Comparator<SuggestedActivityResponseDTO>() {
                @Override
                public int compare(SuggestedActivityResponseDTO o1, SuggestedActivityResponseDTO o2) {
                    return Integer.compare(o2.getVotedSuggestedActivityList().size(), o1.getVotedSuggestedActivityList().size());
                }
            };
            Collections.sort(suggestedActivityDTOList, suggestedActivityDTOComparator);
            updateUI();
        }

    }

    @Override
    public void getSuggestedFail(String message) {

    }

    @Override
    public void deleteSuggestedSuccess(String message) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerSuggestedRef.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteSuggestedFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void createVoteSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerSuggestedRef.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "Like", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void createVoteFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void deleteVoteSuccess(String message) {

        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerSuggestedRef.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "Unlike", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteVoteFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }

    @Override
    public void getSearchSuggestedPlaceSS(List<PlaceDTO> placeDTOList) {
        if (placeDTOList != null) {
            placesSuggestedDTOList = new ArrayList<>();
            placesSuggestedDTOList = placeDTOList;
            showPlaceSuggestedDialog();
        }
    }

    @Override
    public void getSearchSuggestedPlaceFail(String messageFail) {

    }


    @Override
    public void onItemRootViewClicked(int itemAdapterPosition, PlaceDTO placeDTO) {
        btnAddChboxSuggested.setVisibility(View.GONE);
        btnAddNewSuggested.setVisibility(View.VISIBLE);
        for (PlaceDTO responseDTO : placesSuggestedDTOList) {
            if (responseDTO.isSelected()) {
                btnAddChboxSuggested.setVisibility(View.VISIBLE);
                btnAddNewSuggested.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    public void createSuggestSuccess(String messageSuccess) {
        TimeZone tz = TimeZone.getDefault();
        String timez = tz.getID();
        try {
            Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
            Long change = dateNoti.getTime();
            listenerSuggestedRef.setValue(change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createSuggestFail(String messageFail) {
        DialogShowErrorMessage.showValidationDialog(getContext(), messageFail);
    }
}