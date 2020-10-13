package com.fpt.gta.feature.managetransaction.transactiondetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.util.ChangeValue;

import java.util.ArrayList;
import java.util.List;

public class TransactionDetailsAdapter extends RecyclerView.Adapter<TransactionDetailsAdapter.ViewHolder> {
    private List<TransactionDTO.TransactionDetailDTO> transactionDetailDTOList;
    private CurrencyDTO currencyDTO;
    private Context mContext;

    public TransactionDetailsAdapter(List<TransactionDTO.TransactionDetailDTO> transactionDetailDTOList, CurrencyDTO currencyDTO, Context mContext) {
        this.transactionDetailDTOList = transactionDetailDTOList;
        this.mContext = mContext;
        this.currencyDTO = currencyDTO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (mContext ).inflate ( R.layout.item_transaction_details, parent, false);
        return new ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtTransactioMemberName.setText(transactionDetailDTOList.get(position).getMember().getPerson().getName());
        String cleanString = ChangeValue.formatCurrency(transactionDetailDTOList.get(position).getAmount().abs()) + " " + currencyDTO.getCode();
        holder.txtTransactionDetailsValue.setText(cleanString);
    }

    public void notifyChange(List<TransactionDTO.TransactionDetailDTO> mDetailDTOS) {
        transactionDetailDTOList = new ArrayList<>();
        transactionDetailDTOList = mDetailDTOS;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        int count = (transactionDetailDTOList != null) ? transactionDetailDTOList.size () : 0;
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTransactioMemberName, txtTransactionDetailsValue;
        LinearLayout lnlRowTransactionDetails;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            txtTransactioMemberName = (TextView) itemView.findViewById ( R.id.txtTransactioMembernName );
            txtTransactionDetailsValue = (TextView) itemView.findViewById ( R.id.txtTransactionDetailsValue );
            lnlRowTransactionDetails = (LinearLayout) itemView.findViewById ( R.id.lnlRowTransactionDetails );
        }
    }


}
