package com.fpt.gta.feature.managetransaction.edittransaction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Currency;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.presenter.PrintAllCurrencyPresenter;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.view.PrintAllCurrencyView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

public class CurrencyActivity extends AppCompatActivity implements PrintAllCurrencyView {
    private List<CurrencyDTO> mCurrencyDTOList;
    private IndexFastScrollRecyclerView rcvCurrency;
    private PrintAllCurrencyPresenter mPrintAllCurrencyPresenter;
    private CurrencyAdaper currencyAdaper;
    private CurrencyDTO currencyDTO;
    private String CALLING_ACTIVITY;
    private ImageView imgCurrentBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        initView();
        initData();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        CALLING_ACTIVITY = (String) bundle.get("CALLING_ACTIVITY");
        if (bundle.containsKey(GTABundle.EDIT_CURRENCY)) {
            currencyDTO = (CurrencyDTO) bundle.getSerializable(GTABundle.EDIT_CURRENCY);
        } else if (bundle.containsKey(GTABundle.ADD_CURRENCY)) {
            currencyDTO = (CurrencyDTO) bundle.getSerializable(GTABundle.ADD_CURRENCY);
        }else if (bundle.containsKey(GTABundle.ADD_GROUP_CURRENCY)) {
            currencyDTO = (CurrencyDTO) bundle.getSerializable(GTABundle.ADD_GROUP_CURRENCY);
        }else if (bundle.containsKey(GTABundle.EDIT_GROUP_CURRENCY)) {
            currencyDTO = (CurrencyDTO) bundle.getSerializable(GTABundle.EDIT_GROUP_CURRENCY);
        }
        rcvCurrency = findViewById(R.id.fast_scroller_recycler);
        imgCurrentBack = findViewById(R.id.imgCurrentBack);
        rcvCurrency.setIndexBarCornerRadius(3);
        rcvCurrency.setIndexBarTransparentValue((float) 0.4);
        rcvCurrency.setIndexbarMargin(4);
        rcvCurrency.setIndexBarColor(R.color.colorBackGround);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rcvCurrency.setLayoutManager(layoutManager);

    }

    private void initData() {
        mPrintAllCurrencyPresenter = new PrintAllCurrencyPresenter(CurrencyActivity.this, CurrencyActivity.this);
        mPrintAllCurrencyPresenter.printAllCurrency();
        imgCurrentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUI(){
        if(currencyAdaper ==null){
            currencyAdaper = new CurrencyAdaper(mCurrencyDTOList,CurrencyActivity.this);
            rcvCurrency.setAdapter(currencyAdaper);
            currencyAdaper.OnItemClickListener(new CurrencyAdaper.OnItemClickListener() {
                @Override
                public void onItemClick(CurrencyDTO currencyDTO, int pos) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CurrencyActivity.this);
                    builder.setMessage("Are you sure to choose this Currency?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onClickActivityResult(currencyDTO);
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
        }else {
            currencyAdaper.notifyDataSetChanged();
        }
    }

    public void onClickActivityResult(CurrencyDTO currencyDTO) {
        if (CALLING_ACTIVITY.equals("EditTransactionActivity")) {
            onClickEditCurrency(currencyDTO);
        } else if (CALLING_ACTIVITY.equals("AddTransactionActivity")) {
            onClickAddCurrency(currencyDTO);
        } else if (CALLING_ACTIVITY.equals("AddGroupActivity")) {
            onClickAddGroupCurrency(currencyDTO);
        } else if (CALLING_ACTIVITY.equals("EditGroupActivity")) {
            onClickEditGroupCurrency(currencyDTO);
        }
    }
    private void onClickEditCurrency(CurrencyDTO currencyDTO){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.EDIT_CURRENCY, (Serializable) currencyDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onClickAddCurrency(CurrencyDTO currencyDTO){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ADD_CURRENCY, (Serializable) currencyDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onClickAddGroupCurrency(CurrencyDTO currencyDTO){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.ADD_GROUP_CURRENCY, (Serializable) currencyDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onClickEditGroupCurrency(CurrencyDTO currencyDTO){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GTABundle.EDIT_GROUP_CURRENCY, (Serializable) currencyDTO);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void printAllCurrencySuccess(List<CurrencyDTO> currencyDTOList) {
        mCurrencyDTOList = new ArrayList<>();
        mCurrencyDTOList =currencyDTOList;
        updateUI();
    }

    @Override
    public void printAllCurrencyFail(String messageFail) {

    }
}