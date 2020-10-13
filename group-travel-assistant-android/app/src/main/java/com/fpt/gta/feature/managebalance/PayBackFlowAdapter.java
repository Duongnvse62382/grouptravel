package com.fpt.gta.feature.managebalance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.fpt.gta.util.ChangeValue;

import java.util.ArrayList;
import java.util.List;

public class PayBackFlowAdapter extends RecyclerView.Adapter<PayBackFlowAdapter.ViewHolder> {
    private List<TransactionDTO> transactionDTOList;
    private Context mContext;
    private OnItemPayBackFlowClickListener onItemPayBackFlowClickListener;

    public PayBackFlowAdapter(List<TransactionDTO> transactionDTOList, Context mContext) {
        this.transactionDTOList = transactionDTOList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rcv_paybackflow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionDTO transactionDTO =transactionDTOList.get(position);
        holder.txtPayerName.setText(TransactionHandler.payerDetailOf(transactionDTO).getMember().getPerson().getName());
        holder.txtAmoutOwes.setText(ChangeValue.formatBigCurrency(TransactionHandler.payerDetailOf(transactionDTO).getAmount().abs()));
        holder.txtOweName.setText(TransactionHandler.participantListOf(transactionDTO).get(0).getMember().getPerson().getName());
        holder.lnlPayBackFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemPayBackFlowClickListener!=null){
                    onItemPayBackFlowClickListener.onItemPayBackFlowClickListener(transactionDTOList.get(position), position);
                }
            }
        });

    }

    public interface OnItemPayBackFlowClickListener {
        void onItemPayBackFlowClickListener(TransactionDTO transactionDTO, int position);
    }

    public void setOnItemPayBackFlowClickListener(OnItemPayBackFlowClickListener onItemPayBackFlowClickListener) {
        this.onItemPayBackFlowClickListener = onItemPayBackFlowClickListener;
    }


    @Override
    public int getItemCount() {
        int count = (transactionDTOList != null) ? transactionDTOList.size () : 0;
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtOweName, txtPayerName, txtAmoutOwes;
        private LinearLayout lnlPayBackFlow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOweName = itemView.findViewById(R.id.txtOweName);
            txtOweName.setSelected(true);

            txtPayerName = itemView.findViewById(R.id.txtPayerName);
            txtPayerName.setSelected(true);

            txtAmoutOwes = itemView.findViewById(R.id.txtAmoutOwes);
            txtAmoutOwes.setSelected(true);

            lnlPayBackFlow = itemView.findViewById(R.id.lnlPayBackFlow);
        }
    }

    public void notifyChangeData(List<TransactionDTO> dtoList){
        transactionDTOList = new ArrayList<>();
        transactionDTOList = dtoList;
        notifyDataSetChanged();
    }
}
