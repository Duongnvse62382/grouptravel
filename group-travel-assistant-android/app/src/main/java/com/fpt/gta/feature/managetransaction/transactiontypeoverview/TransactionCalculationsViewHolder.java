package com.fpt.gta.feature.managetransaction.transactiontypeoverview;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

public class TransactionCalculationsViewHolder extends RecyclerView.ViewHolder {
    public TextView txtTransactionName, txtTransactionCreateAt, txtTransactionPayer, txtTransactionOwner, txtMoneyReceivable, txtImpactOnMyBalance;
    public TextView txtMemberTransactionName;
    public TextView txtTransactionAmount, txtTransactionCurrencyOverView;
    public LinearLayout lnlRowTransactionOverView;
    public View rootView;

    public LinearLayout lnlRecycleViewTransaction;

    public TransactionCalculationsViewHolder(@NonNull View itemView) {
        super ( itemView );
        rootView = itemView;
        txtImpactOnMyBalance = (TextView) itemView.findViewById(R.id.txtImpactOnMyBalance);
        txtMoneyReceivable = (TextView) itemView.findViewById(R.id.txtMoneyReceivable);
        txtTransactionAmount = (TextView) itemView.findViewById ( R.id.txtTransactionAmount );
        txtTransactionName = (TextView) itemView.findViewById ( R.id.txtTransactionName );
        txtTransactionOwner = (TextView) itemView.findViewById(R.id.txtTransactionOwner);
        txtTransactionCreateAt = (TextView) itemView.findViewById(R.id.txtTransactionCreateAt);
        txtTransactionPayer = (TextView) itemView.findViewById(R.id.txtTransactionPayerr);
        txtTransactionCurrencyOverView = (TextView) itemView.findViewById(R.id.txtTransactionCurrencyOverView);
        lnlRowTransactionOverView = (LinearLayout) itemView.findViewById ( R.id.lnlRowTransactionOverView );
    }
}
