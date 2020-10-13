package com.fpt.gta.feature.managetransaction.transactiontypeoverview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.feature.managetransaction.addtransaction.AddTransactionActivity;
import com.fpt.gta.feature.managetransaction.addtransaction.OCRTransaction.OcrTransactionActivity;
import com.fpt.gta.feature.managetransaction.transactiondetails.TransactionDetailsActivity;
import com.fpt.gta.presenter.PrintAllTransactionPresenter;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.PrintAllTransactionView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class TransactionOverViewFragment extends Fragment implements PrintAllTransactionView, TransactionSection.ClickListener, TransactionStatisticsSection.ClickListener {
    private android.view.View mView;
    private TextView txtSortByAmount, txtSortByDate, txtModeTransaction;
    private ImageView imgAddTransaction, imgAddImageTransaction, imgOptions;
    private RecyclerView recyclerViewTransaction;
    private int groupId;
    private LinearLayoutManager layoutManager;
    private List<TransactionDTO> validTransactionDTOList;
    private List<TransactionDTO> mTransactionList;
    private PrintAllTransactionPresenter mPrintAllTransactionPresenter;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private List<TransactionDTO> activityTypeList;
    private List<TransactionDTO> accommodationTypeList;
    private List<TransactionDTO> transportationTypeList;
    private List<TransactionDTO> foodTypeList;
    private List<TransactionDTO> individualList;
    private FirebaseDatabase databaseTransaction;
    private DatabaseReference listenerTransaction;
    private ValueEventListener transactionValueEventListener;
    BigDecimal activityTypeValue;
    BigDecimal accommodationTypeValue;
    BigDecimal transportationTypeValue;
    BigDecimal foodTypeValue;

    BigDecimal individualValue;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId;

    public TransactionOverViewFragment() {
        // Required empty public constructor
    }

    public static TransactionOverViewFragment newInstance() {
        TransactionOverViewFragment fragment = new TransactionOverViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initalView();
        initialData();
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        reloadFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (transactionValueEventListener != null) {
                listenerTransaction.removeEventListener(transactionValueEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_transaction_manage, container, false);
        return mView;
    }

    public void initalView() {
        groupId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        imgAddTransaction = mView.findViewById(R.id.imgAddTransaction);
        recyclerViewTransaction = mView.findViewById(R.id.recyclerViewTransaction);
        imgAddImageTransaction = mView.findViewById(R.id.imgAddImageTransaction);
        txtModeTransaction = mView.findViewById(R.id.txtModeTransaction);
        imgOptions = (ImageView) mView.findViewById(R.id.imgOptions);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewTransaction.setLayoutManager(linearLayoutManager);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        databaseTransaction = FirebaseDatabase.getInstance();
        listenerTransaction = databaseTransaction.getReference(String.valueOf(groupId)).child("listener").child("reloadTransactionUtc");
        mPrintAllTransactionPresenter = new PrintAllTransactionPresenter(getContext(), this);

    }


    public void initialData() {

        txtModeTransaction.setText("Related Me Mode");
        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogOptionsTransaction();
            }
        });

        imgAddImageTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OcrTransactionActivity.class);
                startActivity(intent);
            }
        });

        imgAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTransactionActivity.class);
                startActivity(intent);
            }
        });

    }


    public void reloadFragment() {

        transactionValueEventListener = listenerTransaction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Long date = snapshot.getValue(Long.class);
                    mPrintAllTransactionPresenter.printAllTransaction(groupId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void showBottomSheetDialogOptionsTransaction() {
        BottomSheetSortTransaction bottomSheetSortTransaction = new BottomSheetSortTransaction();
        bottomSheetSortTransaction.show(getActivity().getSupportFragmentManager(), "Options");
        bottomSheetSortTransaction.setBottomSheetListenerCallback(new BottomSheetSortTransaction.BottomSheetOptionsListenerCallback() {
            @Override
            public void onRowClick(int selectedPosition) {
                if (selectedPosition == 0) {
                    Toast.makeText(getContext(), "Show My Impact", Toast.LENGTH_SHORT).show();
                    showDetailsCalculation();
                    txtModeTransaction.setText("Show My Impact");

                } else if (selectedPosition == 1) {
                    Toast.makeText(getContext(), "Related Me", Toast.LENGTH_SHORT).show();
                    relatedMeMode();
                    txtModeTransaction.setText("Related Me");
                } else if (selectedPosition == 2) {
                    Toast.makeText(getContext(), "My Individual Only", Toast.LENGTH_SHORT).show();
                    individualMode();
                    txtModeTransaction.setText("My Individual Only");
                } else if (selectedPosition == 3) {
                    Toast.makeText(getContext(), "Group Share Only", Toast.LENGTH_SHORT).show();
                    groupMode();
                    txtModeTransaction.setText("Group Share Only");
                }
            }
        });
    }


    public void showDetailsCalculation() {
        sectionedAdapter = new SectionedRecyclerViewAdapter();
        activityTypeValue = new BigDecimal(BigInteger.ZERO);
        accommodationTypeValue = new BigDecimal(BigInteger.ZERO);
        transportationTypeValue = new BigDecimal(BigInteger.ZERO);
        foodTypeValue = new BigDecimal(BigInteger.ZERO);
        individualValue = new BigDecimal(BigInteger.ZERO);

        activityTypeList = new ArrayList<>();
        accommodationTypeList = new ArrayList<>();
        transportationTypeList = new ArrayList<>();
        foodTypeList = new ArrayList<>();

        for (TransactionDTO transactionDTO : validTransactionDTOList) {
            switch (transactionDTO.getIdCategory()) {
                case 1:
                    activityTypeList.add(transactionDTO);
                    break;
                case 2:
                    accommodationTypeList.add(transactionDTO);
                    break;
                case 3:
                    transportationTypeList.add(transactionDTO);
                    break;
                case 4:
                    foodTypeList.add(transactionDTO);
                    break;
            }
        }


        if (activityTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionStatisticsSection(getContext(), "Activity", R.mipmap.category, ChangeValue.formatCurrency(activityTypeValue.abs()), activityTypeList, this));
        }
        if (accommodationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionStatisticsSection(getContext(), "Accommodation", R.mipmap.accomodation, ChangeValue.formatCurrency(accommodationTypeValue.abs()), accommodationTypeList, this));
        }
        if (transportationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionStatisticsSection(getContext(), "Transportation", R.mipmap.transaportationicon, ChangeValue.formatCurrency(transportationTypeValue.abs()), transportationTypeList, this));
        }
        if (foodTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionStatisticsSection(getContext(), "Food", R.mipmap.fastfood, ChangeValue.formatCurrency(foodTypeValue.abs()), foodTypeList, this));
        }

        updateUI();

    }


    public void sortTransactionByDate() {
        validTransactionDTOList.sort((o1, o2) -> {
            return o1.getOccurAt().compareTo(o2.getOccurAt());
        });

        sectionedAdapter = new SectionedRecyclerViewAdapter();

        activityTypeList = new ArrayList<>();
        accommodationTypeList = new ArrayList<>();
        transportationTypeList = new ArrayList<>();
        foodTypeList = new ArrayList<>();

        for (TransactionDTO transactionDTO : validTransactionDTOList) {
            switch (transactionDTO.getIdCategory()) {
                case 1:
                    activityTypeList.add(transactionDTO);
                    break;
                case 2:
                    accommodationTypeList.add(transactionDTO);
                    break;
                case 3:
                    transportationTypeList.add(transactionDTO);
                    break;
                case 4:
                    foodTypeList.add(transactionDTO);
                    break;
            }
        }

        if (activityTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Activity", R.mipmap.category, activityTypeList, this));
        }
        if (accommodationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Accommodation", R.mipmap.accomodation, accommodationTypeList, this));
        }
        if (transportationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Transportation", R.mipmap.transaportationicon, transportationTypeList, this));
        }
        if (foodTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Food", R.mipmap.fastfood, foodTypeList, this));
        }

        updateUI();

    }

    public void groupMode() {
        sectionedAdapter = new SectionedRecyclerViewAdapter();

        activityTypeList = new ArrayList<>();
        accommodationTypeList = new ArrayList<>();
        transportationTypeList = new ArrayList<>();
        foodTypeList = new ArrayList<>();

        List<TransactionDTO> groupTransactionList = TransactionHandler.getGroupTransaction(validTransactionDTOList);

        for (TransactionDTO transactionDTO : groupTransactionList) {
            switch (transactionDTO.getIdCategory()) {
                case 1:
                    activityTypeList.add(transactionDTO);
                    break;
                case 2:
                    accommodationTypeList.add(transactionDTO);
                    break;
                case 3:
                    transportationTypeList.add(transactionDTO);
                    break;
                case 4:
                    foodTypeList.add(transactionDTO);
                    break;
            }
        }

        if (activityTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Activity", R.mipmap.category, activityTypeList, this));
        }
        if (accommodationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Accommodation", R.mipmap.accomodation, accommodationTypeList, this));
        }
        if (transportationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Transportation", R.mipmap.transaportationicon, transportationTypeList, this));
        }
        if (foodTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Food", R.mipmap.fastfood, foodTypeList, this));
        }

        updateUI();

    }

    public void individualMode() {

        List<TransactionDTO> individualListTransaction = new ArrayList<>();
        individualListTransaction = TransactionHandler.getIndividualTransaction(validTransactionDTOList, userId);

        sectionedAdapter = new SectionedRecyclerViewAdapter();


        activityTypeList = new ArrayList<>();
        accommodationTypeList = new ArrayList<>();
        transportationTypeList = new ArrayList<>();
        foodTypeList = new ArrayList<>();

        for (TransactionDTO transactionDTO : individualListTransaction) {
            switch (transactionDTO.getIdCategory()) {
                case 1:
                    activityTypeList.add(transactionDTO);
                    break;
                case 2:
                    accommodationTypeList.add(transactionDTO);
                    break;
                case 3:
                    transportationTypeList.add(transactionDTO);
                    break;
                case 4:
                    foodTypeList.add(transactionDTO);
                    break;
            }
        }

        if (activityTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Activity", R.mipmap.category, activityTypeList, this));
        }
        if (accommodationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Acommodation", R.mipmap.accomodation, accommodationTypeList, this));
        }
        if (transportationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Transportation", R.mipmap.transaportationicon, transportationTypeList, this));
        }
        if (foodTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Food", R.mipmap.fastfood, foodTypeList, this));
        }
        updateUI();
    }

    public void relatedMeMode() {

        List<TransactionDTO> relatedMeTransactionList = new ArrayList<>();
        relatedMeTransactionList = TransactionHandler.getTransactionRelatedMe(validTransactionDTOList, userId);

        sectionedAdapter = new SectionedRecyclerViewAdapter();


        activityTypeList = new ArrayList<>();
        accommodationTypeList = new ArrayList<>();
        transportationTypeList = new ArrayList<>();
        foodTypeList = new ArrayList<>();

        for (TransactionDTO transactionDTO : relatedMeTransactionList) {
            switch (transactionDTO.getIdCategory()) {
                case 1:
                    activityTypeList.add(transactionDTO);
                    break;
                case 2:
                    accommodationTypeList.add(transactionDTO);
                    break;
                case 3:
                    transportationTypeList.add(transactionDTO);
                    break;
                case 4:
                    foodTypeList.add(transactionDTO);
                    break;
            }
        }

        if (activityTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Activity", R.mipmap.category, activityTypeList, this));
        }
        if (accommodationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Acommodation", R.mipmap.accomodation, accommodationTypeList, this));
        }
        if (transportationTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Transportation", R.mipmap.transaportationicon, transportationTypeList, this));
        }
        if (foodTypeList.size() > 0) {
            sectionedAdapter.addSection(new TransactionSection(getContext(), "Food", R.mipmap.fastfood, foodTypeList, this));
        }
        updateUI();
    }


    private void updateUI() {
        recyclerViewTransaction.setAdapter(sectionedAdapter);
        sectionedAdapter.notifyDataSetChanged();
    }

    public boolean checkExits(TransactionDTO transactionDTO, String userId) {
        TransactionDTO.TransactionDetailDTO payerDTO = TransactionHandler.payerDetailOf(transactionDTO);
        List<TransactionDTO.TransactionDetailDTO> participantList = TransactionHandler.participantListOf(transactionDTO);
        TransactionDTO.TransactionDetailDTO groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(transactionDTO);
        if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        if (transactionDTO.getOwner().getPerson().getFirebaseUid().equals(userId)) {
            return true;
        } else if (payerDTO.getMember().getPerson().getFirebaseUid().equals(userId)) {
            return true;
        }
        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantList) {
            if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void printAllTransactionSuccess(List<TransactionDTO> transactionDTOList) {
        if (transactionDTOList != null) {
            mTransactionList = new ArrayList<>();
            mTransactionList = transactionDTOList;

            sectionedAdapter = new SectionedRecyclerViewAdapter();

            activityTypeList = new ArrayList<>();
            accommodationTypeList = new ArrayList<>();
            transportationTypeList = new ArrayList<>();
            foodTypeList = new ArrayList<>();

            validTransactionDTOList = new ArrayList<>();
            for (TransactionDTO transactionDTO : mTransactionList) {
                boolean check = checkExits(transactionDTO, userId);
                if (check == true) {
                    validTransactionDTOList.add(transactionDTO);
                }
            }

            for (TransactionDTO transactionDTO : validTransactionDTOList) {
                switch (transactionDTO.getIdCategory()) {
                    case 1:
                        activityTypeList.add(transactionDTO);
                        break;
                    case 2:
                        accommodationTypeList.add(transactionDTO);
                        break;
                    case 3:
                        transportationTypeList.add(transactionDTO);
                        break;
                    case 4:
                        foodTypeList.add(transactionDTO);
                        break;
                }
            }

            if (activityTypeList.size() > 0) {
                sectionedAdapter.addSection(new TransactionSection(getContext(), "Activity", R.mipmap.category, activityTypeList, this));
            }
            if (accommodationTypeList.size() > 0) {
                sectionedAdapter.addSection(new TransactionSection(getContext(), "Accommodation", R.mipmap.accomodation, accommodationTypeList, this));
            }
            if (transportationTypeList.size() > 0) {
                sectionedAdapter.addSection(new TransactionSection(getContext(), "Transportation", R.mipmap.transaportationicon, transportationTypeList, this));
            }
            if (foodTypeList.size() > 0) {
                sectionedAdapter.addSection(new TransactionSection(getContext(), "Food", R.mipmap.fastfood, foodTypeList, this));
            }
            txtModeTransaction.setText("Related Me Mode");

            updateUI();
        }
    }

    @Override
    public void printAllTransactionFail(String messageFail) {
    }

    public void onClickTransactionItem(TransactionDTO transactionDTO, int position) {
        Intent intent = new Intent(getContext(), TransactionDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.TRANSACTIONITEMCLICk, (Serializable) transactionDTO);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemRootViewClicked(@NonNull TransactionSection section, int itemAdapterPosition, TransactionDTO transactionDTO) {
        onClickTransactionItem(transactionDTO, itemAdapterPosition);
    }

    @Override
    public void onItemTransactionCalculations(@NonNull TransactionStatisticsSection section, int itemAdapterPosition, TransactionDTO transactionDTO) {
        onClickTransactionItem(transactionDTO, itemAdapterPosition);
    }
}