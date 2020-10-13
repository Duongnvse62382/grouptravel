package com.fpt.gta.feature.managebalance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.feature.managetransaction.addtransaction.AddTransactionActivity;
import com.fpt.gta.presenter.PrinaAllTripPresenter;
import com.fpt.gta.presenter.PrintAllTransactionPresenter;
import com.fpt.gta.presenter.PrintMemberInGroupPresenter;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.PrintAllTransactionView;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class BalanceFragment extends Fragment implements PrintAllTransactionView, PrintMemberInGroupView {
    private View mView;
    private RecyclerView rcvBalance, rcvPayBackFlow;
    private TextView txtMemberPaidAll, txtTotalMoneyPaid;
    private BalanceAdapter balanceAdapter;
    private PayBackFlowAdapter payBackFlowAdapter;
    private PrintAllTransactionPresenter mPrintAllTransactionPresenter;
    private List<TransactionDTO> mTransactionDTOList;
    private int groupId;
    private PrintMemberInGroupPresenter mPrintMemberInGroupPresenter;
    private List<MemberDTO> memberList;
    private int isAdmin;
    private CurrencyDTO currencyDTO;

    private FirebaseDatabase datebaseTransaction;
    private DatabaseReference listenerTransaction;
    private ValueEventListener transactionValueEventListener;


    public BalanceFragment() {

    }


    public static BalanceFragment newInstance() {
        BalanceFragment fragment = new BalanceFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void initalData() {
        String groupCurreny = SharePreferenceUtils.getStringSharedPreference(getActivity(), "GROUP_CURRENCY_SHARE");
        Gson gson = new Gson();
        currencyDTO = gson.fromJson(groupCurreny, CurrencyDTO.class);
        mPrintAllTransactionPresenter = new PrintAllTransactionPresenter(getContext(), this);
        mPrintMemberInGroupPresenter = new PrintMemberInGroupPresenter(getContext(), this);

        datebaseTransaction = FirebaseDatabase.getInstance();
        listenerTransaction = datebaseTransaction.getReference(String.valueOf(groupId)).child("listener").child("reloadTransactionUtc");
    }

    public void reloadFragment(){
        transactionValueEventListener = listenerTransaction.addValueEventListener(new ValueEventListener() {
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


    public void initalView() {
        groupId = SharePreferenceUtils.getIntSharedPreference(getContext(), GTABundle.IDGROUP);
        rcvBalance = mView.findViewById(R.id.rcvBalance);
        rcvPayBackFlow = mView.findViewById(R.id.rcvPayBackFlow);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvPayBackFlow.setLayoutManager(layoutManager);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvBalance.setLayoutManager(linearLayoutManager);
    }

    private void updateUI() {
        if (balanceAdapter == null) {
            balanceAdapter = new BalanceAdapter(getContext(), TransactionHandler.balanceOf(mTransactionDTOList));
            rcvBalance.setAdapter(balanceAdapter);
        } else {
            balanceAdapter.notifyChangeDataDetail(TransactionHandler.balanceOf(mTransactionDTOList));
        }
    }

    private void updatePayBackFlowUI() {
        if (payBackFlowAdapter == null) {
            payBackFlowAdapter = new PayBackFlowAdapter(TransactionHandler.getPaybackFlow(mTransactionDTOList), getContext());
            rcvPayBackFlow.setAdapter(payBackFlowAdapter);
            payBackFlowAdapter.setOnItemPayBackFlowClickListener(new PayBackFlowAdapter.OnItemPayBackFlowClickListener() {
                @Override
                public void onItemPayBackFlowClickListener(TransactionDTO transactionDTO, int position) {
                    Intent intent = new Intent(getContext(), AddTransactionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Calling_Transaction", "AddTransaction");
                    bundle.putSerializable("PayBackFlow", transactionDTO);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } else {
            payBackFlowAdapter.notifyChangeData(TransactionHandler.getPaybackFlow(mTransactionDTOList));
        }
    }

    public void goneWithNotAdmin() {
        String idFribase = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int i;
        for (i = 0; i <= memberList.size(); i++) {
            try {
                int roleMember = memberList.get(i).getIdRole();
                String member = memberList.get(i).getPerson().getFirebaseUid();
                if (roleMember == MemberRole.ADMIN && idFribase.equals(member)) {
                    isAdmin = roleMember;
                    break;
                }
            } catch (Exception e) {
                Log.d("", "PrintMemberSuccess: " + e.getMessage());
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_balance, container, false);
        return mView;
    }

    @Override
    public void printAllTransactionSuccess(List<TransactionDTO> transactionDTOList) {
        if (transactionDTOList != null) {
            mTransactionDTOList = new ArrayList<>();
            mTransactionDTOList = transactionDTOList;
            updateUI();
            updatePayBackFlowUI();
        }
    }


    @Override
    public void printAllTransactionFail(String messageFail) {

    }

    @Override
    public void PrintMemberSuccess(List<MemberDTO> memberDTOList) {
        if (memberDTOList != null) {
            memberList = new ArrayList<>();
            memberList = memberDTOList;
            goneWithNotAdmin();
            mPrintAllTransactionPresenter.printAllTransaction(groupId);
        }
    }

    @Override
    public void PrintMemberFail(String message) {

    }
}