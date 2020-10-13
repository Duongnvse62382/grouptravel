package com.fpt.gta.feature.managetransaction.transactiontypeoverview;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;


final class TransactionTypeOverViewHolder extends RecyclerView.ViewHolder {
    public TextView txtTransactionName, txtTransactionCreateAt, txtTransactionPayer, txtTransactionOwner;
    public TextView txtMemberTransactionName;
    public TextView txtTransactionAmount, txtTransactionCurrencyOverView, txtTransactionGroupSharing;
    public LinearLayout lnlRowTransactionOverView;
    public View rootView;

    public LinearLayout lnlRecycleViewTransaction;

    public TransactionTypeOverViewHolder(@NonNull View itemView) {
        super(itemView);
        rootView = itemView;
        txtTransactionAmount = (TextView) itemView.findViewById(R.id.txtTransactionAmount);
        txtTransactionName = (TextView) itemView.findViewById(R.id.txtTransactionName);
        txtTransactionOwner = (TextView) itemView.findViewById(R.id.txtTransactionOwner);
        txtTransactionCreateAt = (TextView) itemView.findViewById(R.id.txtTransactionCreateAt);
        txtTransactionPayer = (TextView) itemView.findViewById(R.id.txtTransactionPayerr);
        txtTransactionCurrencyOverView = (TextView) itemView.findViewById(R.id.txtTransactionCurrencyOverView);
        txtTransactionGroupSharing = (TextView) itemView.findViewById(R.id.txtTransactionGroupSharing);
        lnlRowTransactionOverView = (LinearLayout) itemView.findViewById(R.id.lnlRowTransactionOverView);
    }
}
