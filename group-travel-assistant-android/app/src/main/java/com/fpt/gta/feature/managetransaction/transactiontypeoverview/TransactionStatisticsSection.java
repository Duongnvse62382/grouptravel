package com.fpt.gta.feature.managetransaction.transactiontypeoverview;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.util.ChangeValue;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;


final class TransactionStatisticsSection extends Section {

    private final String title;
    private final int imgSection;
    private final String sectionTotal;
    //    private final String currencyCode;
    private final List<TransactionDTO> list;
    private final ClickListener clickListener;
    private Context mContext;
    private CurrencyDTO currencyDTO;
    private String currencyCode;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId;


    TransactionStatisticsSection(Context context, @NonNull final String title, int imgSection, String sectionTotal, @NonNull final List<TransactionDTO> list,
                                 @NonNull final ClickListener clickListener) {

        super(SectionParameters.builder()
                .itemResourceId(R.layout.row_details_calculations)
                .headerResourceId(R.layout.section_ex1_header)
                .build());
        this.mContext = context;
        this.title = title;
        this.imgSection = imgSection;
        this.sectionTotal = sectionTotal;
        this.list = list;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new TransactionCalculationsViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        final TransactionCalculationsViewHolder itemHolder = (TransactionCalculationsViewHolder) holder;
        BigDecimal defaultCurrency = BigDecimal.ZERO;
        BigDecimal customCurrency = BigDecimal.ZERO;
        TransactionDTO transactionDTO = list.get(position);
        defaultCurrency = transactionDTO.getDefaultCurrencyRate();
        customCurrency = transactionDTO.getCustomCurrencyRate();
        if (customCurrency.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal impactedBalance = TransactionHandler.myImpactedBalance(transactionDTO, userId);
            String cleanString = ChangeValue.formatCurrency(TransactionHandler.payerDetailOf(transactionDTO).getAmount().abs().multiply(customCurrency));
            if (impactedBalance.compareTo(BigDecimal.ZERO) >= 0) {
                itemHolder.txtMoneyReceivable.setText("+" + ChangeValue.formatBigCurrency(impactedBalance) + " " + currencyDTO.getCode());
            } else {
                itemHolder.txtMoneyReceivable.setText(ChangeValue.formatBigCurrency(impactedBalance) + currencyDTO.getCode());
            }
            itemHolder.txtTransactionAmount.setText(cleanString + " " + currencyDTO.getCode());
        } else {
            BigDecimal impactedBalance = TransactionHandler.myImpactedBalance(transactionDTO, userId);
            String cleanString = ChangeValue.formatCurrency(TransactionHandler.payerDetailOf(transactionDTO).getAmount().abs().multiply(defaultCurrency));
            if (impactedBalance.compareTo(BigDecimal.ZERO) >= 0) {
                itemHolder.txtMoneyReceivable.setText("+" + ChangeValue.formatBigCurrency(impactedBalance) + " " + currencyDTO.getCode());
            } else {
                itemHolder.txtMoneyReceivable.setText(ChangeValue.formatBigCurrency(impactedBalance) + currencyDTO.getCode());
            }
            itemHolder.txtTransactionAmount.setText(cleanString + " " + currencyDTO.getCode());
        }
        if (transactionDTO.getCurrency().getCode().equals(currencyDTO.getCode())) {
            itemHolder.txtTransactionCurrencyOverView.setVisibility(View.GONE);
        } else {
            itemHolder.txtTransactionCurrencyOverView.setText(ChangeValue.formatCurrency(TransactionHandler.payerDetailOf(transactionDTO).getAmount().abs()) + " " + transactionDTO.getCurrency().getCode());
        }
        itemHolder.txtTransactionOwner.setSelected(true);
        itemHolder.txtTransactionOwner.setText("Created by" + " " + transactionDTO.getOwner().getPerson().getName());
        itemHolder.txtTransactionName.setText(transactionDTO.getName() + "");
        itemHolder.txtTransactionCreateAt.setText(ZonedDateTimeUtil.convertDateToStringASIA(transactionDTO.getOccurAt()) + "");
        itemHolder.txtTransactionPayer.setText("Paid by" + " " + TransactionHandler.payerDetailOf(transactionDTO).getMember().getPerson().getName());
        itemHolder.txtTransactionPayer.setSelected(true);
        itemHolder.txtImpactOnMyBalance.setText("Impact On My Balance:");
        itemHolder.txtMoneyReceivable.setSelected(true);
        itemHolder.txtImpactOnMyBalance.setSelected(true);


        itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemTransactionCalculations(this, position, transactionDTO));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        currencyCode = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.GROUP_CURRENCY_SHARE);
        Gson gson = new Gson();
        currencyDTO = gson.fromJson(currencyCode, CurrencyDTO.class);
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.tvTitle.setText(title);
        headerHolder.imgSection.setImageResource(imgSection);
    }

    interface ClickListener {
        void onItemTransactionCalculations(@NonNull final TransactionStatisticsSection section, final int itemAdapterPosition, TransactionDTO transactionDTO);
    }
}
