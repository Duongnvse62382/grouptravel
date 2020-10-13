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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {
    private Context mContext;
    List<TransactionDTO.TransactionDetailDTO> transactionDetailDTOList;


    public BalanceAdapter(Context mContext, List<TransactionDTO.TransactionDetailDTO> transactionDetailDTOList) {
        this.mContext = mContext;
        this.transactionDetailDTOList = transactionDetailDTOList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case R.layout.item_rcv_balance:
                holder.txtBalanceDebtMoney.setText(ChangeValue.formatBigCurrency(transactionDetailDTOList.get(position).getAmount()));
                holder.txtBalanceMember.setText(transactionDetailDTOList.get(position).getMember().getPerson().getName());
                break;
            case R.layout.item_rcv_balance_reverse:
                holder.txtBalanceDebtMoney.setText(ChangeValue.formatBigCurrency(transactionDetailDTOList.get(position).getAmount().abs()));
                holder.txtBalanceMember.setText(transactionDetailDTOList.get(position).getMember().getPerson().getName());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        TransactionDTO.TransactionDetailDTO transactionDetailDTO = transactionDetailDTOList.get(position);
        if (transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return R.layout.item_rcv_balance;
        } else if (transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return R.layout.item_rcv_balance_reverse;
        }
        throw new RuntimeException("invalid layout");
    }

    @Override
    public int getItemCount() {
        int count = (transactionDetailDTOList != null) ? transactionDetailDTOList.size() : 0;
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtBalanceDebtMoney, txtBalanceMember;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBalanceDebtMoney = itemView.findViewById(R.id.txtBalanceDebtMoney);
            txtBalanceDebtMoney.setSelected(true);
            txtBalanceMember = itemView.findViewById(R.id.txtBalanceMember);
            txtBalanceMember.setSelected(true);

        }
    }

    public void notifyChangeDataDetail(List<TransactionDTO.TransactionDetailDTO> dtoList) {
        transactionDetailDTOList = new ArrayList<>();
        transactionDetailDTOList = dtoList;
        notifyDataSetChanged();
    }
}
