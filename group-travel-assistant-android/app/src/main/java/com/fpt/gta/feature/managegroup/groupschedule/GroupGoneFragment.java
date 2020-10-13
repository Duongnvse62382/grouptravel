package com.fpt.gta.feature.managegroup.groupschedule;

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
import android.widget.LinearLayout;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.data.dto.LatLongDTO;
import com.fpt.gta.feature.managegroup.editgroup.EditGroupActivity;
import com.fpt.gta.feature.managetrip.overviewtrip.TripOverviewActivity;
import com.fpt.gta.presenter.PrintAllGroupPresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintAllGroupView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class GroupGoneFragment extends Fragment implements View.OnClickListener, PrintAllGroupView {


    private PrintAllGroupPresenter mPrintAllGroupPresenter;
    private RecyclerView recyclerViewGroup;
    private GroupGoneAdapter groupGoneAdapter;
    private List<GroupResponseDTO> groupList = new ArrayList<>();
    private View mView;
    private CurrencyDTO currencyDTO;
    private LatLongDTO latLongDTO;
    private LinearLayout lnlImageTravel;

    private int groupStatus;


    public GroupGoneFragment() {
    }

    public static GroupGoneFragment newInstance() {
        GroupGoneFragment fragment = new GroupGoneFragment();
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
        mView =  inflater.inflate(R.layout.fragment_group_gone, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initalView();
        initalData();
    }

    public void initalView() {
        recyclerViewGroup = mView.findViewById(R.id.recyclerViewGroupGone);
        lnlImageTravel = mView.findViewById(R.id.lnlImageTravel);
        lnlImageTravel.setVisibility(View.GONE);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);
    }

    public void initalData() {
        mPrintAllGroupPresenter = new PrintAllGroupPresenter(getContext(), this);
        mPrintAllGroupPresenter.printAllGroup();
    }
    

    private void updateUI() {
        if (groupGoneAdapter == null) {
            List<GroupResponseDTO> groupGone = new ArrayList<>();
            Date currentDateGoing = new Date(Instant.now().toEpochMilli());
            TimeZone tz = TimeZone.getDefault();
            for (GroupResponseDTO groupResponseDTO : groupList) {
                Date dateEnd = groupResponseDTO.getEndUtcAt();
                Date dateEndGoing = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(dateEnd, tz.getID());
                if(currentDateGoing.after(dateEndGoing)){
                    groupGone.add(groupResponseDTO);
                }
            }
            groupList = groupGone;
            if(groupList.size() == 0){
                lnlImageTravel.setVisibility(View.VISIBLE);
            }
            groupGoneAdapter = new GroupGoneAdapter(groupList, getContext());
            recyclerViewGroup.setAdapter(groupGoneAdapter);
            groupGoneAdapter.setOnEditItemClickListener(new GroupGoneAdapter.OnItemEditClickListener() {
                @Override
                public void onItemEditClickListener(GroupResponseDTO groupResponseDTO, int position) {
                    onClick(groupResponseDTO);
                }
            });

            groupGoneAdapter.setOnItemGroupClickListener(new GroupGoneAdapter.OnItemGroupClickListener() {
                @Override
                public void onItemGroupClickListener(GroupResponseDTO groupResponseDTO, int position) {
                    onGroupClick(groupResponseDTO);
                    String timeZoneStart = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getStartAt());
                    String timeZoneEnd = ZonedDateTimeUtil.convertDateToStringASIA(groupResponseDTO.getEndAt());
                    String timeZoneStartUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getStartUtcAt());
                    String timeZoneEndUTC = ZonedDateTimeUtil.convertDateTimeHmsToString(groupResponseDTO.getEndUtcAt());
                    String timezone = groupResponseDTO.getStartPlace().getTimeZone();
                    latLongDTO = new LatLongDTO();
                    latLongDTO.setPlaceName(groupResponseDTO.getStartPlace().getName());
                    latLongDTO.setLattitude(groupResponseDTO.getStartPlace().getLatitude().doubleValue());
                    latLongDTO.setLongtitue(groupResponseDTO.getStartPlace().getLongitude().doubleValue());
                    Gson gson = new Gson();
                    String json = gson.toJson(latLongDTO);
                    SendLatLong(json);
                    SendDateGo(timeZoneStart);
                    SendDateEnd(timeZoneEnd);
                    SendDateGoUTC(timeZoneStartUTC);
                    SendDateEndUTC(timeZoneEndUTC);
                    SendTimeZone(timezone);
                    SendData(groupResponseDTO.getId());
                    currencyDTO = groupResponseDTO.getCurrency();
                    SendGroupCurrency(currencyDTO);
                    SharePreferenceUtils.saveIntSharedPreference(getContext(), GTABundle.IDGROUP, groupResponseDTO.getId());
                    String journeyDTOS = gson.toJson(groupResponseDTO);
                    SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.JOURNEYOJECT, journeyDTOS);
                    groupStatus = groupResponseDTO.getIdStatus();
                    SharePreferenceUtils.saveIntSharedPreference(getContext(), GTABundle.GROUPOBJECTSTATUS, groupStatus);
                }
            });

        } else {
            groupGoneAdapter.notifyDataChangeGone(groupList);
        }
    }


    public void SendDateGo(String dateGo) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATEGO, dateGo);
    }
    public void SendDateEnd(String dateEnd) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATEEND, dateEnd);
    }

    public void SendDateGoUTC(String dateGoUTC) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATEGOUTC, dateGoUTC);
    }
    public void SendDateEndUTC(String dateEndUTC) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.DATEENDUTC, dateEndUTC);
    }

    public void SendTimeZone(String timeZone) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.TIMEZONEGROUP, timeZone);
    }

    public void SendLatLong(String latlong) {
        SharePreferenceUtils.saveStringSharedPreference(getContext(), "LATLONGDTO", latlong);
    }






    public void SendGroupCurrency(CurrencyDTO currencyDTO) {
        Gson gson = new Gson();
        String groupCurrency = gson.toJson(currencyDTO);
        SharePreferenceUtils.saveStringSharedPreference(getContext(), GTABundle.GROUP_CURRENCY_SHARE, groupCurrency);
    }

    public void onClick(GroupResponseDTO groupResponseDTO) {
        Intent intent = new Intent(this.getActivity(), EditGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEY, groupResponseDTO);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onGroupClick(GroupResponseDTO groupResponseDTO) {
        Intent intent1 = new Intent(this.getActivity(), TripOverviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.KEYGROUP, groupResponseDTO);
        intent1.putExtras(bundle);
        startActivity(intent1);
    }


    public void SendData(int id) {
        SharePreferenceUtils.saveIntSharedPreference(getContext(), GTABundle.IDGROUP, id);
    }




    @Override
    public void onClick(View v) {

    }

    @Override
    public void printAllGroupSuccess(List<GroupResponseDTO> groupResponseDTOList) {
        if (groupResponseDTOList != null) {
            groupList = new ArrayList<>();
            groupList = groupResponseDTOList;
            Collections.sort(groupList, new Comparator<GroupResponseDTO>() {
                @Override
                public int compare(GroupResponseDTO o1, GroupResponseDTO o2) {
                    return o1.getStartUtcAt().compareTo(o2.getStartUtcAt());
                }
            });
            updateUI();
        }
    }

    @Override
    public void printAllGroupFail(String messageFail) {
//        DialogShowErrorMessage.showValidationDialog(getContext(), "Fail");
    }

}




